package com.unknown.audio.synth;

import java.awt.BorderLayout;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

import com.unknown.audio.analysis.Frequency;
import com.unknown.audio.synth.fx.Effect;
import com.unknown.audio.synth.fx.RingDelay;
import com.unknown.audio.synth.osc.SawOscillator;
import com.unknown.audio.synth.strip.ChannelStrip;
import com.unknown.audio.synth.strip.MonoPanner;
import com.unknown.util.ui.Oscilloscope;

public class Synthesizer implements Closeable, Receiver {
	private SourceDataLine waveout;
	private int channels;
	private int sampleRate;

	private volatile boolean stop = false;

	private Voice[] voices;
	private int[] notes;
	private ChannelStrip[] strips;
	private VoiceAllocator allocator;
	private int bend;

	private double bendRange = 2.0;

	private Oscilloscope scope;

	public Synthesizer(int channels, int sampleRate, int voiceCount, Supplier<? extends Voice> voiceFactory)
			throws LineUnavailableException {
		this.channels = channels;
		this.sampleRate = sampleRate;

		voices = new Voice[voiceCount];
		strips = new ChannelStrip[voiceCount];
		notes = new int[voiceCount];
		Random rng = new Random();
		for(int i = 0; i < voices.length; i++) {
			voices[i] = voiceFactory.get();
			voices[i].setTuning((rng.nextFloat() - 0.5f) * 0.01f);
			if(channels == 2) {
				MonoPanner panner = new MonoPanner();
				// panner.setPanning(rng.nextFloat() - 0.5f);
				strips[i] = new ChannelStrip(panner);
			} else {
				throw new IllegalArgumentException("invalid channel count");
			}
			RingDelay delay = new RingDelay(sampleRate, channels, new float[] { 0.137f, 0.330f });
			delay.setMix(0.19f);
			strips[i].setEffects(new Effect[] { delay });
			strips[i].setVolume(0.2f);
		}
		allocator = new PolyphonicVoiceAllocator(voiceCount);

		// @formatter:off
		AudioFormat format = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,	// encoding
				sampleRate,				// sample rate
				16,					// bit/sample
				channels,				// channels
				2 * channels,
				sampleRate,
				true					// big-endian
		);
		// @formatter:on

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("Line matching " + info + " not supported");
		}

		waveout = (SourceDataLine) AudioSystem.getLine(info);
		waveout.open(format, 4096);

		scope = new Oscilloscope();
		scope.setSampleRate(sampleRate);
		scope.setVoltageDivision(0.05);
		scope.setTimeDivision(0.001);
		JFrame frame = new JFrame("Oscilloscope");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setSize(300, 300);
		frame.setLayout(new BorderLayout());
		frame.add(BorderLayout.CENTER, scope);
		frame.setVisible(true);
	}

	@Override
	public void close() {
		waveout.close();
	}

	public int getChannels() {
		return channels;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void stop() {
		stop = true;
	}

	public void run() {
		stop = false;
		waveout.start();
		while(!stop) {
			byte[] buffer = mix();
			waveout.write(buffer, 0, buffer.length);
		}
		waveout.stop();
	}

	private static float clamp(float x, float min, float max) {
		return Math.max(Math.min(x, max), min);
	}

	private float[] history = new float[4800];
	private int step = 0;

	private byte[] mix() {
		float[] mix = new float[channels];
		for(int i = 0; i < voices.length; i++) {
			float value = voices[i].process();
			strips[i].process(mix, new float[] { value });
		}
		history[step++] = mix[0];
		if(step >= history.length) {
			scope.setSignal(history);
			step = 0;
		}

		byte[] result = new byte[2 * channels];
		for(int i = 0; i < channels; i++) {
			float flt = clamp(mix[i], -1.0f, 1.0f);
			short data = (short) (flt * Short.MAX_VALUE);
			result[2 * i] = (byte) (data >> 8);
			result[2 * i + 1] = (byte) data;
		}

		return result;
	}

	private double getPitch(int note) {
		double off = bend / 8192.0 * bendRange;
		return Frequency.MIDInoteToFreq(note + off);
	}

	public void setPitchBendRange(double range) {
		bendRange = range;
	}

	public void noteOn(int note, int velocity) {
		int voice = allocator.noteOn(note);
		if(voice != VoiceAllocator.NONE) {
			notes[voice] = note;
			voices[voice].setPitch(getPitch(note));
			voices[voice].setVelocity(velocity);
			voices[voice].on();
		} else {
			System.out.println("no voice available for note " + note);
		}
	}

	public void noteOff(int note, @SuppressWarnings("unused") int velocity) {
		int voice = allocator.noteOff(note);
		if(voice != VoiceAllocator.NONE) {
			voices[voice].off();
		}
	}

	public void programChange(int program) {
		System.out.println("PROGRAM_CHANGE " + program);
	}

	public void controlChange(int cc, int value) {
		for(Voice voice : voices) {
			voice.setParameter(cc, value);
		}
	}

	public void pitchBend(int val1, int val2) {
		bend = val1 + (val2 << 7) - 8192;
		for(int i = 0; i < voices.length; i++) {
			voices[i].setPitch((float) getPitch(notes[i]));
		}
	}

	@Override
	public void send(MidiMessage message, long time) {
		try {
			if(message instanceof ShortMessage) {
				ShortMessage msg = (ShortMessage) message;
				int cmd = msg.getCommand();
				switch(cmd) {
				case ShortMessage.NOTE_ON:
					if(msg.getData2() == 0) {
						noteOff(msg.getData1(), 64);
					} else {
						noteOn(msg.getData1(), msg.getData2());
					}
					break;
				case ShortMessage.NOTE_OFF:
					noteOff(msg.getData1(), msg.getData2());
					break;
				case ShortMessage.PROGRAM_CHANGE:
					programChange(msg.getData1());
					break;
				case ShortMessage.CONTROL_CHANGE:
					controlChange(msg.getData1(), msg.getData2());
					break;
				case ShortMessage.PITCH_BEND:
					pitchBend(msg.getData1(), msg.getData2());
					break;
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public static void main(String[] args) throws Throwable {
		System.out.println("Enumerating MIDI devices");
		List<Info> devices = new ArrayList<>();
		for(Info info : MidiSystem.getMidiDeviceInfo()) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);
				int maxin = device.getMaxTransmitters();
				if(maxin == 0) {
					continue;
				}
				devices.add(info);
			} catch(Throwable t) {
				System.out.printf("Error reading device: %s\n", t.getMessage());
				System.out.printf("device: '%s' (%s, %s)\n", info.getName(), info.getVendor(),
						info.getDescription());
				throw t;
			}
		}
		System.out.println("Available MIDI IN devices:");
		for(int i = 0; i < devices.size(); i++) {
			Info info = devices.get(i);
			System.out.printf("Device %d: '%s' (%s, %s)\n", i, info.getName(), info.getVendor(),
					info.getDescription());
		}

		if(args.length != 1) {
			System.out.println("Synthesizer device-name");
			return;
		}

		MidiDevice device = null;
		for(Info info : devices) {
			if(info.getName().equals(args[0])) {
				System.out.printf("Selecting device %s (%s, %s)\n", info.getName(), info.getVendor(),
						info.getDescription());
				device = MidiSystem.getMidiDevice(info);
				break;
			}
		}

		if(device == null) {
			return;
		}

		int sampleRate = 48000;
		Synthesizer synth = new Synthesizer(2, sampleRate, 16,
				() -> new SynthVoice2(sampleRate, () -> new SawOscillator(sampleRate),
						() -> new ADSREnvelopeGenerator(sampleRate, 0.01f, 0, 1, 0.01f)));

		device.open();
		device.getTransmitter().setReceiver(synth);
		synth.run();
	}
}
