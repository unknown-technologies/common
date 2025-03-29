package com.unknown.util.timeseries;

import java.time.Instant;

public interface TimeSeriesProcessor {
	void process(Instant timestamp, byte[] data);
}
