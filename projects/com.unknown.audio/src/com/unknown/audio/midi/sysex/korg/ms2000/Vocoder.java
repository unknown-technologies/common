package com.unknown.audio.midi.sysex.korg.ms2000;

public class Vocoder {
	private int midiChannel;
	private int assignMode;
	private boolean eg1Reset;
	private boolean eg2Reset;
	private boolean triggerMode;
	private int keyPriority;
	private int unisonDetune;

	private int tune;
	private int bendRange;
	private int transpose;
	private int vibratoInt;

	private int wave;
	private int waveformCtl1;
	private int waveformCtl2;
	private int dwgsWave;

	private boolean hpfGate;

	private int portamentoTime;

	private int osc1Level;
	private int ext1Level;
	private int noiseLevel;

	private int hpfLevel;
	private int gateSense;
	private int threshold;

	private int shift;
	private int cutoff;
	private int resonance;
	private int modSource;
	private int filterIntensity;
	private int efSense;

	private int ampLevel;
	private int ampDirectLevel;
	private boolean distortion;
	private int ampVelSense;
	private int ampKeyTrack;

	private int eg1Attack;
	private int eg1Decay;
	private int eg1Sustain;
	private int eg1Release;

	private int eg2Attack;
	private int eg2Decay;
	private int eg2Sustain;
	private int eg2Release;

	private int lfo1KeySync;
	private int lfo1Wave;
	private int lfo1Frequency;
	private boolean lfo1TempoSync;
	private int lfo1SyncNote;

	private int lfo2KeySync;
	private int lfo2Wave;
	private int lfo2Frequency;
	private boolean lfo2TempoSync;
	private int lfo2SyncNote;

	private final int[] chLevel = new int[16];
	private final int[] chPan = new int[16];

	public void read(byte[] data, int offset) {
		// TODO
	}

	public void write(byte[] data, int offset) {
		// TODO
	}
}
