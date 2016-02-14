package net.suizinshu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.*;

public class MidiBuilder {

	// MIDI's own internal handling codes
	private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;
	
	/**
	 * Inputs: INFILE OUT
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void numToMidi(String inName, String outName, String tempoStr) throws FileNotFoundException {
		File textFile = new File(inName);
		File midiFile = new File(outName);
		int tempo = Integer.parseInt(tempoStr);
		
		if (!textFile.exists())
			throw new FileNotFoundException("Text file not found or is invalid!");
		
		buildMidi(textFile, midiFile, tempo);
	}

	private static void buildMidi(File textFile, File midiFile, int tempo) {
		int skips = 0;
		int lines = 0;
		try {
	//****  Create a new MIDI sequence with 24 ticks per beat  ****
			Sequence s = new Sequence(Sequence.PPQ,tempo);

	//****  Obtain a MIDI track from the sequence  ****
			Track t = s.createTrack();
			
			writeHeader(t);
			
			// Intitialize reader
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			
			// Initialize noteEvent list
			List<NoteEvent> noteEvents = new ArrayList<NoteEvent>();
			
			// Parse all noteEvents
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				lines++;
				if (line.length() > 6 && Character.isDigit(line.charAt(0)))
					noteEvents.add(new NoteEvent(line));
				else
					skips++;
			}
			
			// Determine activationTimes for each
			long tick = 0;
			for (int i = 0; i < noteEvents.size(); i++) {
				NoteEvent e = noteEvents.get(i);
				tick += e.relativeTime;
				e.activationTime = tick;
			}
			
			long endTime = tick;
			int finalDuration = 0;
			int endPadding = 10;
			// Determine endTime
			for (int i = noteEvents.size() - 1; i == noteEvents.size() - 1 || 
					noteEvents.get(i+1).relativeTime == 0; i--) {
				finalDuration = Math.max(noteEvents.get(i).duration, finalDuration);
			}
			endTime += finalDuration + endPadding;
			
			noteEventIntoMIDIEvents(t, noteEvents);

	//****  set end of track (meta event) 19 ticks later  ****
			endTrack(t, endTime);

	//****  write the MIDI sequence to a MIDI file  ****
			MidiSystem.write(s,1,midiFile);
			
			reader.close();
			
			System.out.println(skips + " out of " + lines + " lines were malformed, being " + 
					   ((double)(100 * skips))/((double)lines) + "%.");
		} catch(Exception e) {
			System.err.println("Exception while processing line " + lines);
			e.printStackTrace();
		}
	}

	private static void noteEventIntoMIDIEvents(Track t, List<NoteEvent> noteEvents)
			throws InvalidMidiDataException {
		ShortMessage mm;
		MidiEvent me;
		
		
		// Create events and add in order
		for (NoteEvent note : noteEvents) {

			while (note.key < 0)
				note.key += 32;
			if (note.key > 127)
				note.key %= 128;
			
			while (note.velocity < 0)
				note.velocity += 50;
			if (note.velocity > 100)
				note.velocity %= 100;
			
			mm = new ShortMessage(NOTE_ON, note.key, note.velocity);
			me = new MidiEvent(mm, note.activationTime);
			t.add(me);
			mm = new ShortMessage(NOTE_OFF, note.key, note.velocity);
			me = new MidiEvent(mm, note.activationTime + note.duration);
			t.add(me);
		}
	}

	private static void writeHeader(Track t) throws InvalidMidiDataException {
//****  General MIDI sysex -- turn on General MIDI sound set  ****
		byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
		SysexMessage sm = new SysexMessage();
		sm.setMessage(b, 6);
		MidiEvent me = new MidiEvent(sm,(long)0);
		t.add(me);

//****  set tempo (meta event)  ****
		MetaMessage mt = new MetaMessage();
		byte[] bt = {0x02, (byte)0x00, 0x00};
		mt.setMessage(0x51 ,bt, 3);
		me = new MidiEvent(mt,(long)0);
		t.add(me);

//****  set track name (meta event)  ****
		mt = new MetaMessage();
		String TrackName = new String("TXT-TO-MIDI GENERATED TRACK");
		mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
		me = new MidiEvent(mt,(long)0);
		t.add(me);

//****  set omni on  ****
		ShortMessage mm = new ShortMessage();
		mm.setMessage(0xB0, 0x7D,0x00);
		me = new MidiEvent(mm,(long)0);
		t.add(me);

//****  set poly on  ****
		mm = new ShortMessage();
		mm.setMessage(0xB0, 0x7F,0x00);
		me = new MidiEvent(mm,(long)0);
		t.add(me);

//****  set instrument to Piano  ****
		mm = new ShortMessage();
		mm.setMessage(0xC0, 0x00, 0x00);
		me = new MidiEvent(mm,(long)0);
		t.add(me);
	}

	private static void endTrack(Track t, long endTime) throws InvalidMidiDataException {
		MidiEvent me;
		MetaMessage mt;
		mt = new MetaMessage();
		byte[] bet = {}; // empty array
		mt.setMessage(0x2F,bet,0);
		me = new MidiEvent(mt, endTime);
		t.add(me);
	}

	
	
	
}
