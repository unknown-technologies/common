package com.unknown.posix.api.net;

import com.unknown.posix.api.io.Iovec;

public class Msghdr {
	public Sockaddr msg_name;
	public Iovec[] msg_iov;
	public int msg_iovlen;
	public Cmsghdr msg_control;
	public int msg_flags;
}
