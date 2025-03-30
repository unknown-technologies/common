package com.unknown.posix.vfs;

import static com.unknown.posix.api.io.Fcntl.O_RDONLY;
import static com.unknown.posix.api.io.Fcntl.O_RDWR;
import static com.unknown.posix.api.io.Fcntl.O_TMPFILE;
import static com.unknown.posix.api.io.Fcntl.O_WRONLY;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.unknown.posix.api.ByteBufferPosixPointer;
import com.unknown.posix.api.Errno;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.Stream;
import com.unknown.posix.api.mem.Mman;
import com.unknown.util.BitTest;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class NativeFileStream extends Stream {
	private static final Logger log = Trace.create(NativeFileStream.class);

	private Path path;
	private RandomAccessFile file;
	private boolean r;
	private boolean w;

	public NativeFileStream(Path path, int flags) throws PosixException {
		this.path = path;
		int rdwr = flags & 0x3;
		this.statusFlags = flags;
		try {
			switch(rdwr) {
			case O_RDONLY:
				if(BitTest.test(flags, O_TMPFILE)) {
					throw new PosixException(Errno.EINVAL);
				}
				if(isdir(path)) {
					throw new PosixException(Errno.EISDIR);
				}
				file = new RandomAccessFile(path.toFile(), "r");
				r = true;
				w = false;
				break;
			case O_WRONLY:
				if(BitTest.test(flags, O_TMPFILE)) {
					throw new PosixException(Errno.EINVAL);
				}
				if(isdir(path)) {
					throw new PosixException(Errno.EISDIR);
				}
				if(path.toFile().exists() && Posix.WARN_ON_FILE_DELETE) {
					log.log(Levels.WARNING,
							String.format("opening existing file '%s' in O_WRONLY mode!",
									path.toString()));
				}
				file = new RandomAccessFile(path.toFile(), "rw");
				r = false;
				w = true;
				break;
			case O_RDWR:
				if(BitTest.test(flags, O_TMPFILE)) {
					throw new PosixException(Errno.EINVAL);
				}
				if(isdir(path)) {
					throw new PosixException(Errno.EISDIR);
				}
				if(path.toFile().exists() && Posix.WARN_ON_FILE_DELETE) {
					log.log(Levels.WARNING,
							String.format("opening existing file '%s' in O_RDWR mode!",
									path.toString()));
				}
				file = new RandomAccessFile(path.toFile(), "rw");
				r = true;
				w = true;
				break;
			default:
				throw new PosixException(Errno.EINVAL);
			}
		} catch(FileNotFoundException e) {
			throw new PosixException(Errno.ENOENT);
		}
	}

	private static boolean isdir(Path path) {
		return path.toFile().isDirectory();
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws PosixException {
		if(!r) {
			throw new PosixException(Errno.EBADF);
		}
		try {
			int n = file.read(buf, offset, length);
			if(n == -1) {
				return 0;
			} else {
				return n;
			}
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int write(byte[] buf, int offset, int length) throws PosixException {
		if(!w) {
			throw new PosixException(Errno.EBADF);
		}
		try {
			file.write(buf, offset, length);
			return length;
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		if(!r) {
			throw new PosixException(Errno.EBADF);
		}
		if(fileOffset < 0) {
			throw new PosixException(Errno.EINVAL);
		}
		try {
			FileChannel chan = file.getChannel();
			ByteBuffer bbuf = ByteBuffer.wrap(buf, offset, length);
			int n = chan.read(bbuf, fileOffset);
			return n;
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		if(!w) {
			throw new PosixException(Errno.EBADF);
		}
		if(fileOffset < 0) {
			throw new PosixException(Errno.EINVAL);
		}
		try {
			FileChannel chan = file.getChannel();
			ByteBuffer bbuf = ByteBuffer.wrap(buf, offset, length);
			int n = chan.write(bbuf, fileOffset);
			return n;
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int close() throws PosixException {
		try {
			file.close();
			return 0;
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		FileChannel chan = file.getChannel();
		try {
			long pos;
			switch(whence) {
			case SEEK_SET:
				pos = offset;
				break;
			case SEEK_CUR:
				pos = chan.position() + offset;
				break;
			case SEEK_END:
				pos = chan.size() + offset;
				break;
			default:
				throw new PosixException(Errno.EINVAL);
			}
			if(offset > 0 && pos < 0) {
				throw new PosixException(Errno.EOVERFLOW);
			}
			if(pos < 0) {
				throw new PosixException(Errno.EINVAL);
			}
			chan.position(pos);
			return chan.position();
		} catch(IOException e) {
			throw new PosixException(Errno.EINVAL);
		}
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		NativeFile.stat(path, buf);
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		NativeFile.statx(path, mask, buf);
	}

	@Override
	public void ftruncate(long length) throws PosixException {
		FileChannel chan = file.getChannel();
		try {
			long size = chan.size();
			if(size < length) {
				long bufsize = length - size;
				assert bufsize == (int) bufsize;
				ByteBuffer zeros = ByteBuffer.allocate((int) bufsize);
				chan.write(zeros, size);
			} else {
				chan.truncate(length);
			}
		} catch(NonWritableChannelException e) {
			throw new PosixException(Errno.EBADF);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	private static final long PAGE_SIZE = 4096;
	private static final long PAGE_MASK = ~(PAGE_SIZE - 1);

	private static long roundToPageSize(long size) {
		long base = size & PAGE_MASK;
		if(base != size) {
			return base + PAGE_SIZE;
		} else {
			return base;
		}
	}

	@Override
	public PosixPointer mmap(long size, int prot, int flags, long off) throws PosixException {
		FileChannel chan = file.getChannel();
		if((off & ~PAGE_MASK) != 0) {
			throw new PosixException(Errno.EINVAL);
		}
		try {
			if(Long.compareUnsigned(off, chan.size()) > 0) {
				throw new PosixException(Errno.EINVAL);
			}
			long realsize = roundToPageSize(size);
			long rem = chan.size() - off;
			assert chan.size() > 0;
			assert rem > 0;
			// TODO: why was there a buffer size truncation?
			// if(Long.compareUnsigned(rem, realsize) < 0) {
			// realsize = rem;
			// }
			if(BitTest.test(flags, Mman.MAP_PRIVATE) && BitTest.test(prot, Mman.PROT_WRITE)) {
				assert size == (int) size;
				ByteBuffer buf = ByteBuffer.allocate((int) realsize);
				chan.read(buf, off);
				// ByteBuffer buf = chan.map(MapMode.PRIVATE, off, size);
				return new ByteBufferPosixPointer(buf, 0, roundToPageSize(size), realsize,
						path.toAbsolutePath().normalize().toString());
			} else if(BitTest.test(flags, Mman.MAP_SHARED)) {
				// TODO: implement properly
				assert size == (int) size;
				ByteBuffer buf = ByteBuffer.allocate((int) realsize);
				chan.read(buf, off);
				return new ByteBufferPosixPointer(buf, 0, roundToPageSize(size), realsize,
						path.toAbsolutePath().normalize().toString());
			} else {
				// TODO: implement write mode
				ByteBuffer buf = chan.map(MapMode.READ_ONLY, off, size);
				return new ByteBufferPosixPointer(buf, 0, roundToPageSize(size), realsize,
						path.toAbsolutePath().normalize().toString());
			}
		} catch(IOException e) {
			e.printStackTrace();
			throw new PosixException(Errno.EIO);
		}
	}
}
