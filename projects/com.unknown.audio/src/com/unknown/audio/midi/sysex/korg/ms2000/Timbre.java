package com.unknown.audio.midi.sysex.korg.ms2000;

public class Timbre {
	private int midiChannel;
	private int assignMode;
	private boolean eg1Reset;
	private boolean eg2Reset;
	private boolean triggerMode;
	private int keyPriority;
	private int unisonDetune;

	// pitch
	private int tune;
	private int bendRange;
	private int transpose;
	private int vibratoInt;

	// OSC1
	private int osc1Wave;
	private int osc1WaveformCtl1;
	private int osc1WaveformCtl2;
	private int osc1DwgsWave;

	// OSC2
	private int osc2ModSelect;
	private int osc2Wave;
	private int osc2Semitone;
	private int osc2Tune;

	// pitch (2)
	private int portamentoTime;

	// mixer
	private int osc1Level;
	private int osc2Level;
	private int noise;

	// filter
	private int filterType;
	private int filterCutoff;
	private int filterResonance;
	private int filterEG1Intensity;
	private int filterVelocitySense;
	private int filterKeyboardTrack;

	// AMP
	private int ampLevel;
	private int ampPanpot;
	private boolean ampSw;
	private boolean ampDistortion;
	private int ampVelocitySense;
	private int ampKeyboardTrack;

	// EG1
	private int eg1Attack;
	private int eg1Decay;
	private int eg1Sustain;
	private int eg1Release;

	// EG2
	private int eg2Attack;
	private int eg2Decay;
	private int eg2Sustain;
	private int eg2Release;

	// LFO1
	private int lfo1KeySync;
	private int lfo1Wave;
	private int lfo1Frequency;
	private boolean lfo1TempoSync;
	private int lfo1SyncNote;

	// LFO2
	private int lfo2KeySync;
	private int lfo2Wave;
	private int lfo2Frequency;
	private boolean lfo2TempoSync;
	private int lfo2SyncNote;

	// patch
	private int patch1Destination;
	private int patch1Source;
	private int patch1Intensity;

	private int patch2Destination;
	private int patch2Source;
	private int patch2Intensity;

	private int patch3Destination;
	private int patch3Source;
	private int patch3Intensity;

	private int patch4Destination;
	private int patch4Source;
	private int patch4Intensity;

	// SEQ
	private boolean seqActive;
	private boolean seqRunMode;
	private int seqResolution;
	private int seqLastStep;
	private int seqType;
	private int seqKeySync;

	private final SEQParameter seq1 = new SEQParameter();
	private final SEQParameter seq2 = new SEQParameter();
	private final SEQParameter seq3 = new SEQParameter();

	public void read(byte[] data, int offset) {
		midiChannel = data[offset];
		assignMode = (data[offset + 1] >> 6) & 0x03;
		eg2Reset = (data[offset + 1] & 0x20) != 0;
		eg1Reset = (data[offset + 1] & 0x10) != 0;
		triggerMode = (data[offset + 1] & 0x80) != 0;
		keyPriority = data[offset + 1] & 0x03;
		unisonDetune = data[offset + 2];

		// pitch
		tune = data[offset + 3] - 64;
		bendRange = data[offset + 4] - 64;
		transpose = data[offset + 5] - 64;
		vibratoInt = data[offset + 6] - 64;

		// OSC1
		osc1Wave = data[offset + 7];
		osc1WaveformCtl1 = data[offset + 8];
		osc1WaveformCtl2 = data[offset + 9];
		osc1DwgsWave = data[offset + 10];

		// OSC2
		osc2ModSelect = (data[offset + 12] >> 4) & 0x03;
		osc2Wave = data[offset + 12] & 0x03;
		osc2Semitone = data[offset + 13] - 64;
		osc2Tune = data[offset + 14] - 64;

		// pitch (2)
		portamentoTime = data[offset + 15] & 0x7F;

		// mixer
		osc1Level = data[offset + 16];
		osc2Level = data[offset + 17];
		noise = data[offset + 18];

		// filter
		filterType = data[offset + 19];
		filterCutoff = data[offset + 20];
		filterResonance = data[offset + 21];
		filterEG1Intensity = data[offset + 22] - 64;
		filterVelocitySense = data[offset + 23] - 64;
		filterKeyboardTrack = data[offset + 24] - 64;

		// AMP
		ampLevel = data[offset + 25];
		ampPanpot = data[offset + 26];
		ampSw = (data[offset + 27] & 0x40) != 0;
		ampDistortion = (data[offset + 27] & 0x01) != 0;
		ampVelocitySense = data[offset + 28] - 64;
		ampKeyboardTrack = data[offset + 29] - 64;

		// EG1
		eg1Attack = data[offset + 30];
		eg1Decay = data[offset + 31];
		eg1Sustain = data[offset + 32];
		eg1Release = data[offset + 33];

		// EG2
		eg2Attack = data[offset + 34];
		eg2Decay = data[offset + 35];
		eg2Sustain = data[offset + 36];
		eg2Release = data[offset + 37];

		// LFO1
		lfo1KeySync = (data[offset + 38] >> 4) & 0x03;
		lfo1Wave = data[offset + 38] & 0x03;
		lfo1Frequency = data[offset + 39];
		lfo1TempoSync = (data[offset + 40] & 0x80) != 0;
		lfo1SyncNote = data[offset + 40] & 0x0F;

		// LFO2
		lfo2KeySync = (data[offset + 41] >> 4) & 0x03;
		lfo2Wave = data[offset + 41] & 0x03;
		lfo2Frequency = data[offset + 42];
		lfo2TempoSync = (data[offset + 43] & 0x80) != 0;
		lfo2SyncNote = data[offset + 43] & 0x0F;

		// patch
		patch1Destination = (data[offset + 44] >> 4) & 0x0F;
		patch1Source = data[offset + 44] & 0x0F;
		patch1Intensity = data[offset + 45] - 64;
		patch2Destination = (data[offset + 46] >> 4) & 0x0F;
		patch2Source = data[offset + 46] & 0x0F;
		patch2Intensity = data[offset + 47] - 64;
		patch3Destination = (data[offset + 48] >> 4) & 0x0F;
		patch3Source = data[offset + 48] & 0x0F;
		patch3Intensity = data[offset + 49] - 64;
		patch4Destination = (data[offset + 50] >> 4) & 0x0F;
		patch4Source = data[offset + 50] & 0x0F;
		patch4Intensity = data[offset + 51] - 64;

		// SEQ
		seqActive = (data[offset + 52] & 0x80) != 0;
		seqRunMode = (data[offset + 52] & 0x40) != 0;
		seqResolution = data[offset + 52] & 0x0F;
		seqLastStep = (data[offset + 53] >> 4) & 0x0F;
		seqType = (data[offset + 53] >> 2) & 0x03;
		seqKeySync = data[offset + 53] & 0x03;

		seq1.read(data, offset + 54);
		seq2.read(data, offset + 72);
		seq3.read(data, offset + 90);
	}

	public void write(byte[] data, int offset) {
		data[offset] = (byte) midiChannel;
		data[offset + 1] = (byte) (((assignMode & 0x03) << 6) | (eg2Reset ? 0x20 : 0) | (eg1Reset ? 0x10 : 0) |
				(triggerMode ? 0x08 : 0) | (keyPriority & 0x03));
		data[offset + 2] = (byte) (unisonDetune & 0x7F);
		data[offset + 3] = (byte) ((tune + 64) & 0x7F);
		data[offset + 4] = (byte) ((bendRange + 64) & 0x7F);
		data[offset + 5] = (byte) ((transpose + 64) & 0x7F);
		data[offset + 6] = (byte) ((vibratoInt + 64) & 0x7F);
		data[offset + 7] = (byte) (osc1Wave & 0x07);
		data[offset + 8] = (byte) (osc1WaveformCtl1 & 0x7F);
		data[offset + 9] = (byte) (osc1WaveformCtl2 & 0x7F);
		data[offset + 10] = (byte) (osc1DwgsWave & 0x3F);
		data[offset + 12] = (byte) (((osc2ModSelect & 0x03) << 2) | (osc2Wave & 0x03));
		data[offset + 13] = (byte) ((osc2Semitone + 64) & 0x7F);
		data[offset + 14] = (byte) ((osc2Tune + 64) & 0x7F);
		data[offset + 15] = (byte) (portamentoTime & 0x7F);
		data[offset + 16] = (byte) (osc1Level & 0x7F);
		data[offset + 17] = (byte) (osc2Level & 0x7F);
		data[offset + 18] = (byte) (noise & 0x7F);
		data[offset + 19] = (byte) (filterType & 0x03);
		data[offset + 20] = (byte) (filterCutoff & 0x7F);
		data[offset + 21] = (byte) (filterResonance & 0x7F);
		data[offset + 22] = (byte) ((filterEG1Intensity + 64) & 0x7F);
		data[offset + 23] = (byte) ((filterVelocitySense + 64) & 0x7F);
		data[offset + 24] = (byte) ((filterKeyboardTrack + 64) & 0x7F);
		data[offset + 25] = (byte) (ampLevel & 0x7F);
		data[offset + 26] = (byte) (ampPanpot & 0x7F);
		data[offset + 27] = (byte) ((ampSw ? 0x40 : 0) | (ampDistortion ? 1 : 0));
		data[offset + 28] = (byte) ((ampVelocitySense + 64) & 0x7F);
		data[offset + 29] = (byte) ((ampKeyboardTrack + 64) & 0x7F);
		data[offset + 30] = (byte) (eg1Attack & 0x7F);
		data[offset + 31] = (byte) (eg1Decay & 0x7F);
		data[offset + 32] = (byte) (eg1Sustain & 0x7F);
		data[offset + 33] = (byte) (eg1Release & 0x7F);
		data[offset + 34] = (byte) (eg2Attack & 0x7F);
		data[offset + 35] = (byte) (eg2Decay & 0x7F);
		data[offset + 36] = (byte) (eg2Sustain & 0x7F);
		data[offset + 37] = (byte) (eg2Release & 0x7F);
		data[offset + 38] = (byte) (((lfo1KeySync & 0x03) << 4) | (lfo1Wave & 0x03));
		data[offset + 39] = (byte) (lfo1Frequency & 0x7F);
		data[offset + 40] = (byte) ((lfo1TempoSync ? 0x80 : 0) | (lfo1SyncNote & 0x0F));
		data[offset + 41] = (byte) (((lfo2KeySync & 0x03) << 4) | (lfo2Wave & 0x03));
		data[offset + 42] = (byte) (lfo2Frequency & 0x7F);
		data[offset + 43] = (byte) ((lfo2TempoSync ? 0x80 : 0) | (lfo2SyncNote & 0x0F));
		data[offset + 44] = (byte) (((patch1Destination & 0x0F) << 4) | (patch1Source & 0x0F));
		data[offset + 45] = (byte) ((patch1Intensity + 64) & 0x7F);
		data[offset + 46] = (byte) (((patch2Destination & 0x0F) << 4) | (patch2Source & 0x0F));
		data[offset + 47] = (byte) ((patch2Intensity + 64) & 0x7F);
		data[offset + 48] = (byte) (((patch3Destination & 0x0F) << 4) | (patch3Source & 0x0F));
		data[offset + 49] = (byte) ((patch3Intensity + 64) & 0x7F);
		data[offset + 50] = (byte) (((patch4Destination & 0x0F) << 4) | (patch4Source & 0x0F));
		data[offset + 51] = (byte) ((patch4Intensity + 64) & 0x7F);
		data[offset + 52] = (byte) ((seqActive ? 0x80 : 0) | (seqRunMode ? 0x40 : 0) | (seqResolution & 0x0F));
		data[offset + 53] = (byte) (((seqLastStep & 0x0F) << 4) | ((seqType & 0x03) << 2) |
				(seqKeySync & 0x03));

		seq1.write(data, offset + 54);
		seq2.write(data, offset + 72);
		seq3.write(data, offset + 90);
	}

	public void copy(Timbre timbre) {
		midiChannel = timbre.midiChannel;
		assignMode = timbre.assignMode;
		eg2Reset = timbre.eg2Reset;
		eg1Reset = timbre.eg1Reset;
		triggerMode = timbre.triggerMode;
		keyPriority = timbre.keyPriority;
		unisonDetune = timbre.unisonDetune;

		// pitch
		tune = timbre.tune;
		bendRange = timbre.bendRange;
		transpose = timbre.transpose;
		vibratoInt = timbre.vibratoInt;

		// OSC1
		osc1Wave = timbre.osc1Wave;
		osc1WaveformCtl1 = timbre.osc1WaveformCtl1;
		osc1WaveformCtl2 = timbre.osc1WaveformCtl2;
		osc1DwgsWave = timbre.osc1DwgsWave;

		// OSC2
		osc2ModSelect = timbre.osc2ModSelect;
		osc2Wave = timbre.osc2Wave;
		osc2Semitone = timbre.osc2Semitone;
		osc2Tune = timbre.osc2Tune;

		// pitch (2)
		portamentoTime = timbre.portamentoTime;

		// mixer
		osc1Level = timbre.osc1Level;
		osc2Level = timbre.osc2Level;
		noise = timbre.noise;

		// filter
		filterType = timbre.filterType;
		filterCutoff = timbre.filterCutoff;
		filterResonance = timbre.filterResonance;
		filterEG1Intensity = timbre.filterEG1Intensity;
		filterVelocitySense = timbre.filterVelocitySense;
		filterKeyboardTrack = timbre.filterKeyboardTrack;

		// AMP
		ampLevel = timbre.ampLevel;
		ampPanpot = timbre.ampPanpot;
		ampSw = timbre.ampSw;
		ampDistortion = timbre.ampDistortion;
		ampVelocitySense = timbre.ampVelocitySense;
		ampKeyboardTrack = timbre.ampKeyboardTrack;

		// EG1
		eg1Attack = timbre.eg1Attack;
		eg1Decay = timbre.eg1Decay;
		eg1Sustain = timbre.eg1Sustain;
		eg1Release = timbre.eg1Release;

		// EG2
		eg2Attack = timbre.eg2Attack;
		eg2Decay = timbre.eg2Decay;
		eg2Sustain = timbre.eg2Sustain;
		eg2Release = timbre.eg2Release;

		// LFO1
		lfo1KeySync = timbre.lfo1KeySync;
		lfo1Wave = timbre.lfo1Wave;
		lfo1Frequency = timbre.lfo1Frequency;
		lfo1TempoSync = timbre.lfo1TempoSync;
		lfo1SyncNote = timbre.lfo1SyncNote;

		// LFO2
		lfo2KeySync = timbre.lfo2KeySync;
		lfo2Wave = timbre.lfo2Wave;
		lfo2Frequency = timbre.lfo2Frequency;
		lfo2TempoSync = timbre.lfo2TempoSync;
		lfo2SyncNote = timbre.lfo2SyncNote;

		// patch
		patch1Destination = timbre.patch1Destination;
		patch1Source = timbre.patch1Source;
		patch1Intensity = timbre.patch1Intensity;
		patch2Destination = timbre.patch2Destination;
		patch2Source = timbre.patch2Source;
		patch2Intensity = timbre.patch2Intensity;
		patch3Destination = timbre.patch3Destination;
		patch3Source = timbre.patch3Source;
		patch3Intensity = timbre.patch3Intensity;
		patch4Destination = timbre.patch4Destination;
		patch4Source = timbre.patch4Source;
		patch4Intensity = timbre.patch4Intensity;

		// SEQ
		seqActive = timbre.seqActive;
		seqRunMode = timbre.seqRunMode;
		seqResolution = timbre.seqResolution;
		seqLastStep = timbre.seqLastStep;
		seqType = timbre.seqType;
		seqKeySync = timbre.seqKeySync;

		seq1.copy(timbre.seq1);
		seq2.copy(timbre.seq2);
		seq3.copy(timbre.seq3);
	}

	@Override
	public Timbre clone() {
		Timbre t = new Timbre();
		t.copy(this);
		return t;
	}

	@Override
	public String toString() {
		return "Timbre[SEQ1=" + seq1 + ",SEQ2=" + seq2 + "]";
	}
}
