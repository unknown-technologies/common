package com.unknown.platform.windows.registry;

import java.util.Date;

import com.unknown.platform.windows.Windows;

public class RegistryKeyInfo {
	private final int subkeys;
	private final int maxSubkeyLength;
	private final int maxClassLength;
	private final int values;
	private final int maxValueNameLength;
	private final int maxValueLength;
	private final int securityDescriptor;
	private final long dateTime;

	public RegistryKeyInfo(int subkeys, int maxSubkeyLength, int maxClassLength, int values, int maxValueNameLength,
			int maxValueLength, int securityDescriptor, int lowDateTime, int highDateTime) {
		this.subkeys = subkeys;
		this.maxSubkeyLength = maxSubkeyLength;
		this.maxClassLength = maxClassLength;
		this.values = values;
		this.maxValueNameLength = maxValueNameLength;
		this.maxValueLength = maxValueLength;
		this.securityDescriptor = securityDescriptor;
		this.dateTime = ((long) highDateTime << 32) | Integer.toUnsignedLong(lowDateTime);
	}

	public int getSubkeys() {
		return subkeys;
	}

	public int getMaxSubkeyLength() {
		return maxSubkeyLength;
	}

	public int getMaxClassLength() {
		return maxClassLength;
	}

	public int getValues() {
		return values;
	}

	public int getMaxValueNameLength() {
		return maxValueNameLength;
	}

	public int getMaxValueLength() {
		return maxValueLength;
	}

	public int getSecurityDescriptor() {
		return securityDescriptor;
	}

	public Date getDateTime() {
		return new Date(Windows.windowsTickToUnixSeconds(dateTime) * 1000);
	}

	@Override
	public String toString() {
		return "RegistryKeyInfo[subkeys=" + subkeys + ",maxSubkeyLength=" + maxSubkeyLength +
				",maxClassLength=" + maxClassLength + ",values=" + values + ",maxValueNameLength=" +
				maxValueNameLength + ",maxValueLength=" + maxValueLength + ",securityDescriptor=" +
				securityDescriptor + ",dateTime=\"" + getDateTime() + "\"]";
	}
}
