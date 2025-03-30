package com.unknown.posix.api.net;

import java.util.HashMap;
import java.util.Map;

public class In {
	// @formatter:off
	public static final int IPPROTO_IP = 0;        /* Dummy protocol for TCP               */
	public static final int IPPROTO_ICMP = 1;      /* Internet Control Message Protocol    */
	public static final int IPPROTO_IGMP = 2;      /* Internet Group Management Protocol   */
	public static final int IPPROTO_IPIP = 4;      /* IPIP tunnels (older KA9Q tunnels use 94) */
	public static final int IPPROTO_TCP = 6;       /* Transmission Control Protocol        */
	public static final int IPPROTO_EGP = 8;       /* Exterior Gateway Protocol            */
	public static final int IPPROTO_PUP = 12;      /* PUP protocol                         */
	public static final int IPPROTO_UDP = 17;      /* User Datagram Protocol               */
	public static final int IPPROTO_IDP = 22;      /* XNS IDP protocol                     */
	public static final int IPPROTO_TP = 29;       /* SO Transport Protocol Class 4        */
	public static final int IPPROTO_DCCP = 33;     /* Datagram Congestion Control Protocol */
	public static final int IPPROTO_IPV6 = 41;     /* IPv6-in-IPv4 tunnelling              */
	public static final int IPPROTO_RSVP = 46;     /* RSVP Protocol                        */
	public static final int IPPROTO_GRE = 47;      /* Cisco GRE tunnels (rfc 1701,1702)    */
	public static final int IPPROTO_ESP = 50;      /* Encapsulation Security Payload protocol */
	public static final int IPPROTO_AH = 51;       /* Authentication Header protocol       */
	public static final int IPPROTO_MTP = 92;      /* Multicast Transport Protocol         */
	public static final int IPPROTO_BEETPH = 94;   /* IP option pseudo header for BEET     */
	public static final int IPPROTO_ENCAP = 98;    /* Encapsulation Header                 */
	public static final int IPPROTO_PIM = 103;     /* Protocol Independent Multicast       */
	public static final int IPPROTO_COMP = 108;    /* Compression Header Protocol          */
	public static final int IPPROTO_SCTP = 132;    /* Stream Control Transport Protocol    */
	public static final int IPPROTO_UDPLITE = 136; /* UDP-Lite (RFC 3828)                  */
	public static final int IPPROTO_MPLS = 137;    /* MPLS in IP (RFC 4023)                */
	public static final int IPPROTO_RAW = 255;     /* Raw IP packets                       */
	// @formatter:on

	private static Map<Integer, String> ipproto;

	static {
		ipproto = new HashMap<>();
		ipproto.put(IPPROTO_IP, "IPPROTO_IP");
		ipproto.put(IPPROTO_ICMP, "IPPROTO_ICMP");
		ipproto.put(IPPROTO_IGMP, "IPPROTO_IGMP");
		ipproto.put(IPPROTO_IPIP, "IPPROTO_IPIP");
		ipproto.put(IPPROTO_TCP, "IPPROTO_TCP");
		ipproto.put(IPPROTO_EGP, "IPPROTO_EGP");
		ipproto.put(IPPROTO_PUP, "IPPROTO_PUP");
		ipproto.put(IPPROTO_UDP, "IPPROTO_UDP");
		ipproto.put(IPPROTO_IDP, "IPPROTO_IDP");
		ipproto.put(IPPROTO_TP, "IPPROTO_TP");
		ipproto.put(IPPROTO_DCCP, "IPPROTO_DCCP");
		ipproto.put(IPPROTO_IPV6, "IPPROTO_IPV6");
		ipproto.put(IPPROTO_RSVP, "IPPROTO_RSVP");
		ipproto.put(IPPROTO_GRE, "IPPROTO_GRE");
		ipproto.put(IPPROTO_ESP, "IPPROTO_ESP");
		ipproto.put(IPPROTO_AH, "IPPROTO_AH");
		ipproto.put(IPPROTO_MTP, "IPPROTO_MTP");
		ipproto.put(IPPROTO_BEETPH, "IPPROTO_BEETPH");
		ipproto.put(IPPROTO_ENCAP, "IPPROTO_ENCAP");
		ipproto.put(IPPROTO_PIM, "IPPROTO_PIM");
		ipproto.put(IPPROTO_COMP, "IPPROTO_COMP");
		ipproto.put(IPPROTO_SCTP, "IPPROTO_SCTP");
		ipproto.put(IPPROTO_UDPLITE, "IPPROTO_UDPLITE");
		ipproto.put(IPPROTO_MPLS, "IPPROTO_MPLS");
		ipproto.put(IPPROTO_RAW, "IPPROTO_RAW");
	}

	public static String ipproto(int proto) {
		String result = ipproto.get(proto);
		if(result == null) {
			return Integer.toString(proto);
		} else {
			return result;
		}
	}
}
