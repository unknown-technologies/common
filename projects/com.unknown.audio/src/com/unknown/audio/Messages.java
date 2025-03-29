package com.unknown.audio;

import com.unknown.util.exception.ExceptionId;

public class Messages {
	// @formatter:off
	public static final ExceptionId PLAYER_THREAD_FAIL	= new ExceptionId("SNDP0001E", "Error in audio player thread: {0}");
	public static final ExceptionId PLAYER_LINE_OPEN	= new ExceptionId("SNDP0002I", "Audio line opened");
	public static final ExceptionId PLAYER_LINE_CLOSE	= new ExceptionId("SNDP0003I", "Audio line closed");
	public static final ExceptionId PLAYER_LINE_START	= new ExceptionId("SNDP0004I", "Audio line started");
	public static final ExceptionId PLAYER_LINE_STOP	= new ExceptionId("SNDP0005I", "Audio line stopped");
	public static final ExceptionId PLAYER_START		= new ExceptionId("SNDP0006I", "Audio playback started");
	public static final ExceptionId PLAYER_STOP		= new ExceptionId("SNDP0007I", "Audio playback stopped");
	public static final ExceptionId PLAYER_STOP_EOF		= new ExceptionId("SNDP0008I", "Audio playback stopped (sound file ended)");
	public static final ExceptionId PLAYER_LINE_STILL_OPEN	= new ExceptionId("SNDP0009W", "Audio line still open; closing");
	public static final ExceptionId PLAYER_THREAD_STILL_OPEN= new ExceptionId("SNDP0010W", "Player thread still alive; interrupting");
	public static final ExceptionId PLAYER_ALREADY_STOPPED	= new ExceptionId("SNDP0011I", "Player already stopped");
	// @formatter:on
}
