package com.unknown.posix.vfs;

import java.util.Iterator;

import com.unknown.posix.api.Dirent;
import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.io.DirectoryStream;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;

public abstract class VFSDirectoryStream extends DirectoryStream {
	private VFSDirectory dir;
	private Iterator<Dirent> iterator;
	private Dirent last = null;

	public VFSDirectoryStream(VFSDirectory dir, Iterator<Dirent> iterator) {
		this.dir = dir;
		this.iterator = iterator;
	}

	private Dirent peek() {
		if(last != null) {
			return last;
		} else {
			last = iterator.next();
			return last;
		}
	}

	private void next() {
		assert last != null;
		last = null;
	}

	private boolean hasNext() {
		return last != null || iterator.hasNext();
	}

	@Override
	public long getdents(PosixPointer ptr, long count, int type) throws PosixException {
		long total = 0;
		PosixPointer p = ptr;
		while(hasNext()) {
			Dirent dirent = peek();
			if(type == Dirent.DIRENT_64) {
				int size = dirent.size64();
				if(total + size <= count) {
					p = dirent.write64(p);
					total += size;
					next();
				} else if(total == 0) {
					throw new PosixException(Errno.EINVAL);
				} else {
					break;
				}
			} else if(type == Dirent.DIRENT_32) {
				int size = dirent.size32();
				if(total + size <= count) {
					p = dirent.write32(p);
					total += size;
					next();
				} else if(total == 0) {
					throw new PosixException(Errno.EINVAL);
				} else {
					break;
				}
			} else if(type == Dirent.DIRENT64) {
				int size = dirent.size64();
				if(total + size <= count) {
					p = dirent.writeDirent64(p);
					total += size;
					next();
				} else if(total == 0) {
					throw new PosixException(Errno.EINVAL);
				} else {
					break;
				}
			} else {
				throw new IllegalArgumentException("unknown type");
			}
		}
		return total;
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		return 0;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		dir.stat(buf);
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		dir.statx(mask, buf);
	}
}
