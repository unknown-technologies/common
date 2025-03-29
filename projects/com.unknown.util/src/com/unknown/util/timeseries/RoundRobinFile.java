package com.unknown.util.timeseries;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.Arrays;

import com.unknown.util.io.Endianess;

public class RoundRobinFile implements AutoCloseable {
	private static final byte[] MAGIC = { 'R', 'R', 'D', 'B' };
	private static final byte CURRENT_VERSION = 1;

	private static final int HEADER_SIZE = 32;
	private static final int TIMESTAMP_SIZE = 5;

	private final RandomAccessFile file;
	private final int maxRecords;
	private final int recordSize;
	private final int interval;

	private int writePointer;

	public RoundRobinFile(File path) throws IOException {
		this(path, 0, 0, 0);
	}

	public RoundRobinFile(File path, boolean readonly) throws IOException {
		this(path, 0, 0, 0, readonly);
	}

	public RoundRobinFile(File path, int recordSize, int maxRecords) throws IOException {
		this(path, recordSize, maxRecords, 0);
	}

	public RoundRobinFile(File path, int recordSize, int maxRecords, int interval) throws IOException {
		this(path, recordSize, maxRecords, interval, false);
	}

	public RoundRobinFile(File path, int recordSize, int maxRecords, int interval, boolean readonly)
			throws IOException {
		file = new RandomAccessFile(path, readonly ? "r" : "rwd");

		long length = file.length();
		if(length == 0) {
			if(maxRecords <= 0) {
				throw new IllegalArgumentException("invalid record count");
			}
			if(recordSize <= 0) {
				throw new IllegalArgumentException("invalid record size");
			}

			this.maxRecords = maxRecords;
			this.recordSize = recordSize;
			this.interval = interval;

			initializeFile();
		} else if(length < HEADER_SIZE) {
			throw new IOException("Invalid round robin file");
		} else {
			byte[] header = new byte[HEADER_SIZE];
			file.readFully(header);

			if(!Arrays.equals(MAGIC, 0, 4, header, 0, 4)) {
				throw new IOException("Not a round robin file");
			}

			if(header[31] != CURRENT_VERSION) {
				throw new IOException("Incompatible file version");
			}

			int storedMaxRecords = Endianess.get32bitBE(header, 4);
			int storedRecordSize = Endianess.get32bitBE(header, 8);
			int storedInterval = Endianess.get32bitBE(header, 12);
			writePointer = Endianess.get32bitBE(header, 16);

			this.maxRecords = storedMaxRecords;
			this.recordSize = storedRecordSize;
			this.interval = storedInterval;

			if(storedMaxRecords != maxRecords && maxRecords > 0) {
				throw new IOException("Parameter mismatch: max records");
			}
			if(storedRecordSize != recordSize && recordSize > 0) {
				throw new IOException("Parameter mismatch: record size");
			}
			if(storedInterval != interval && interval > 0) {
				throw new IOException("Parameter mismatch: interval");
			}
		}
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	private void initializeFile() throws IOException {
		writePointer = 0;

		long fileSize = maxRecords * ((long) recordSize + TIMESTAMP_SIZE) + HEADER_SIZE;
		file.setLength(fileSize);

		byte[] header = new byte[HEADER_SIZE];
		System.arraycopy(MAGIC, 0, header, 0, MAGIC.length);
		Endianess.set32bitBE(header, 4, maxRecords);
		Endianess.set32bitBE(header, 8, recordSize);
		Endianess.set32bitBE(header, 12, interval);
		Endianess.set32bitBE(header, 16, writePointer);
		header[31] = CURRENT_VERSION;

		file.seek(0);
		file.write(header);
	}

	public int getMaxRecords() {
		return maxRecords;
	}

	public int getRecordSize() {
		return recordSize;
	}

	public int getWritePointer() {
		return writePointer;
	}

	public Instant read(int id, byte[] record) throws IOException {
		if(record.length != recordSize) {
			throw new IOException("invalid buffer size");
		}

		byte[] buf = new byte[TIMESTAMP_SIZE + recordSize];

		int pos = id;
		if(id < 0) {
			pos += maxRecords;
		}

		long offset = pos * ((long) recordSize + TIMESTAMP_SIZE) + HEADER_SIZE;

		file.seek(offset);
		file.readFully(buf);

		System.arraycopy(buf, TIMESTAMP_SIZE, record, 0, recordSize);

		long ts = Endianess.get40bitBE(buf);
		if(ts == 0) {
			return null;
		} else {
			return Instant.ofEpochSecond(ts);
		}
	}

	public Instant readTimestamp(int id) throws IOException {
		byte[] buf = new byte[TIMESTAMP_SIZE];

		int pos = id;
		if(id < 0) {
			pos += maxRecords;
		}

		long offset = pos * ((long) recordSize + TIMESTAMP_SIZE) + HEADER_SIZE;

		file.seek(offset);
		file.readFully(buf);

		long ts = Endianess.get40bitBE(buf);
		if(ts == 0) {
			return null;
		} else {
			return Instant.ofEpochSecond(ts);
		}
	}

	private int get(int point) {
		int id = (writePointer + point) % maxRecords;
		if(id < 0) {
			id += maxRecords;
		}
		return id;
	}

	private int findTimestamp(Instant timestamp) throws IOException {
		int l = 0;
		int r = maxRecords - 1;

		if(interval != 0) {
			Instant last = readTimestamp(get(r));
			if(last != null) {
				if(last.isBefore(timestamp)) {
					return r;
				}

				long tslast = last.getEpochSecond();
				long ts = timestamp.getEpochSecond();
				long delta = (tslast - ts) / interval;

				if(delta < maxRecords) {
					l = (int) (r - delta);

					Instant hintTS0 = readTimestamp(get(l - 1));
					Instant hintTS1 = readTimestamp(get(l));
					if(hintTS1 != null && hintTS1.isAfter(timestamp) && hintTS0 != null &&
							hintTS0.isBefore(timestamp)) {
						return l - 1;
					}
					Instant hintTS2 = readTimestamp(get(l + 1));
					if(hintTS1 != null && hintTS1.isBefore(timestamp) && hintTS2 != null &&
							hintTS2.isAfter(timestamp)) {
						return l;
					}
				}
			}
		}

		while(l <= r) {
			int m = (l + r) / 2;
			Instant ts = readTimestamp(get(m));
			if(ts == null || ts.isBefore(timestamp)) {
				l = m + 1;
			} else if(ts.isAfter(timestamp)) {
				r = m - 1;
			} else {
				return m;
			}
		}

		return r;
	}

	public void stream(Instant from, Instant to, TimeSeriesProcessor processor) throws IOException {
		int start = findTimestamp(from);
		int end = findTimestamp(to);
		int count = end - start;

		int startIdx = get(start);
		int endIdx = get(end);

		byte[] data = new byte[recordSize];

		if(startIdx > endIdx) {
			// wrap
			int len1 = maxRecords - startIdx;
			int len2 = count - len1;

			byte[] buf = new byte[len1 * (recordSize + TIMESTAMP_SIZE)];

			long offset = startIdx * ((long) recordSize + TIMESTAMP_SIZE) + HEADER_SIZE;
			file.seek(offset);
			file.readFully(buf);
			for(int off = 0; off < buf.length; off += recordSize + TIMESTAMP_SIZE) {
				System.arraycopy(buf, off + 5, data, 0, data.length);
				long ts = Endianess.get40bitBE(buf, off);
				if(ts != 0) {
					Instant time = Instant.ofEpochSecond(ts);
					processor.process(time, data);
				}
			}

			buf = new byte[len2 * (recordSize + TIMESTAMP_SIZE)];
			file.seek(HEADER_SIZE);
			file.readFully(buf);
			for(int off = 0; off < buf.length; off += recordSize + TIMESTAMP_SIZE) {
				System.arraycopy(buf, off + 5, data, 0, data.length);
				long ts = Endianess.get40bitBE(buf, off);
				if(ts != 0) {
					Instant time = Instant.ofEpochSecond(ts);
					processor.process(time, data);
				}
			}
		} else {
			// linear
			byte[] buf = new byte[count * (recordSize + TIMESTAMP_SIZE)];

			long offset = startIdx * ((long) recordSize + TIMESTAMP_SIZE) + HEADER_SIZE;
			file.seek(offset);
			file.readFully(buf);
			for(int off = 0; off < buf.length; off += recordSize + TIMESTAMP_SIZE) {
				System.arraycopy(buf, off + 5, data, 0, data.length);
				long ts = Endianess.get40bitBE(buf, off);
				if(ts != 0) {
					Instant time = Instant.ofEpochSecond(ts);
					processor.process(time, data);
				}
			}
		}
	}

	public void append(Instant timestamp, byte[] record) throws IOException {
		if(record.length != recordSize) {
			throw new IOException("invalid record size");
		}

		long ts = timestamp.getEpochSecond();
		byte[] buf = new byte[TIMESTAMP_SIZE + recordSize];
		Endianess.set40bitBE(buf, ts);
		System.arraycopy(record, 0, buf, TIMESTAMP_SIZE, recordSize);

		long offset = writePointer * ((long) recordSize + TIMESTAMP_SIZE) + HEADER_SIZE;
		file.seek(offset);
		file.write(buf);

		writePointer = (writePointer + 1) % maxRecords;

		buf = new byte[4];
		Endianess.set32bitBE(buf, 0, writePointer);
		file.seek(16);
		file.write(buf);
	}
}
