package com.unknown.net.artnet;

public class Opcode {
	// @formatter:off
	public static final int OpPoll = 0x2000; // This is an ArtPoll packet, no other data is contained in this UDP packet.
	public static final int OpPollReply = 0x2100; // This is an ArtPollReply Packet. It contains device status information.
	public static final int OpDiagData = 0x2300; // Diagnostics and data logging packet.
	public static final int OpCommand = 0x2400; // This is an ArtCommand packet. It is used to send text based parameter commands.
	public static final int OpDataRequest = 0x2700; // This is an ArtDataRequest packet. It is used to request data such as products URLs
	public static final int OpDataReply = 0x2800; // This is an ArtDataReply packet. It is used to reply to ArtDataRequest packets.
	public static final int OpOutput = 0x5000;
	public static final int OpDmx = 0x5000; // This is an ArtDmx data packet. It contains zero start code DMX512 information for a single Universe.
	public static final int OpNzs = 0x5100; // This is an ArtNzs data packet. It contains non-zero start code (except RDM) DMX512 information for a single Universe.
	public static final int OpSync = 0x5200; // This is an ArtSync data packet. It is used to force synchronous transfer of ArtDmx packets to a node’s output.
	public static final int OpAddress = 0x6000; // This is an ArtAddress packet. It contains remote programming information for a Node.
	public static final int OpInput = 0x7000; // This is an ArtInput packet. It contains enable – disable data for DMX inputs.
	public static final int OpTodRequest = 0x8000; // This is an ArtTodRequest packet. It is used to request a Table of Devices (ToD) for RDM discovery.
	public static final int OpTodData = 0x8100; // This is an ArtTodData packet. It is used to send a Table of Devices (ToD) for RDM discovery.
	public static final int OpTodControl = 0x8200; // This is an ArtTodControl packet. It is used to send RDM discovery control messages.
	public static final int OpRdm = 0x8300; // This is an ArtRdm packet. It is used to send all non discovery RDM messages.
	public static final int OpRdmSub = 0x8400; // This is an ArtRdmSub packet. It is used to send compressed, RDM Sub-Device data.
	public static final int OpVideoSetup = 0xa010; // This is an ArtVideoSetup packet. It contains video screen setup information for nodes that implement the extended video features.
	public static final int OpVideoPalette = 0xa020; // This is an ArtVideoPalette packet. It contains colour palette setup information for nodes that implement the extended video features.
	public static final int OpVideoData = 0xa040; // This is an ArtVideoData packet. It contains display data for nodes that implement the extended video features.
	public static final int OpMacMaster = 0xf000; // This packet is deprecated.
	public static final int OpMacSlave = 0xf100; // This packet is deprecated.
	public static final int OpFirmwareMaster = 0xf200; // This is an ArtFirmwareMaster packet. It is used to upload new firmware or firmware extensions to the Node.
	public static final int OpFirmwareReply = 0xf300; // This is an ArtFirmwareReply packet. It is returned by the node to acknowledge receipt of an ArtFirmwareMaster packet or ArtFileTnMaster packet.
	public static final int OpFileTnMaster = 0xf400; // Uploads user file to node.
	public static final int OpFileFnMaster = 0xf500; // Downloads user file from node.
	public static final int OpFileFnReply = 0xf600; // Server to Node acknowledge for download packets.
	public static final int OpIpProg = 0xf800; // This is an ArtIpProg packet. It is used to re-programme the IP address and Mask of the Node.
	public static final int OpIpProgReply = 0xf900; // This is an ArtIpProgReply packet. It is returned by the node to acknowledge receipt of an ArtIpProg packet.
	public static final int OpMedia = 0x9000; // This is an ArtMedia packet. It is Unicast by a Media Server and acted upon by a Controller.
	public static final int OpMediaPatch = 0x9100; // This is an ArtMediaPatch packet. It is Unicast by a Controller and acted upon by a Media Server.
	public static final int OpMediaControl = 0x9200; // This is an ArtMediaControl packet. It is Unicast by a Controller and acted upon by a Media Server.
	public static final int OpMediaContrlReply = 0x9300; // This is an ArtMediaControlReply packet. It is Unicast by a Media Server and acted upon by a Controller.
	public static final int OpTimeCode = 0x9700; // This is an ArtTimeCode packet. It is used to transport time code over the network.
	public static final int OpTimeSync = 0x9800; // Used to synchronise real time date and clock
	public static final int OpTrigger = 0x9900; // Used to send trigger macros
	public static final int OpDirectory = 0x9a00; // Requests a node's file list
	public static final int OpDirectoryReply = 0x9b00; // Replies to OpDirectory with file list
	// @formatter:on
}
