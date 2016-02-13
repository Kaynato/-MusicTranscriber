package net.suizinshu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.*;

public class MidiBuilder {

	public static void main(String args[]) throws FileNotFoundException {

		if (args.length <= 0)
			args = new String[]{"D:/Cloud/Dropbox/College/CWRU/Hackathon/midiRNN/chopin_txt/chpn-p7.txt"};
			
		File textFile = new File(args[0]);
		
		if (!textFile.exists())
			throw new FileNotFoundException("File not found or is invalid!");
			
		
		buildMidi(textFile);

	}

	private static void buildMidi(File textFile) {
		try {
	//****  Create a new MIDI sequence with 24 ticks per beat  ****
			Sequence s = new Sequence(Sequence.PPQ,24);

	//****  Obtain a MIDI track from the sequence  ****
			Track t = s.createTrack();

			MidiEvent me;
			ShortMessage mm;
			writeHeader(t);
			
			List<NoteEvent> noteEvents = new ArrayList<NoteEvent>();
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			
			for (String line = reader.readLine(); line != null; line = reader.readLine())
				noteEvents.add(new NoteEvent(line));
			
			System.out.println(noteEvents); // TODO
			
			long tick = 0;
			for (int i = 0; i < noteEvents.size(); i++) {
				NoteEvent e = noteEvents.get(i);
				tick += e.relativeTime;
				e.activationTime = tick;
			}
			
			noteEvents.forEach((ne) -> System.out.println(ne.activationTime));

	//****  note on - middle C  ****
			mm = new ShortMessage();
			mm.setMessage(0x90,0x3C,0x60);
			me = new MidiEvent(mm,(long)1);
			t.add(me);

	//****  note off - middle C - 120 ticks later  ****
			mm = new ShortMessage();
			mm.setMessage(0x80,0x3C,0x40);
			me = new MidiEvent(mm,(long)121);
			t.add(me);

	//****  set end of track (meta event) 19 ticks later  ****
			endTrack(t);

	//****  write the MIDI sequence to a MIDI file  ****
			File f = new File("midifile.mid");
			MidiSystem.write(s,1,f);
			
			reader.close();
		} catch(Exception e) {
			System.out.println("Exception caught " + e.toString());
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

	private static void endTrack(Track t) throws InvalidMidiDataException {
		MidiEvent me;
		MetaMessage mt;
		mt = new MetaMessage();
		byte[] bet = {}; // empty array
		mt.setMessage(0x2F,bet,0);
		me = new MidiEvent(mt, (long)140);
		t.add(me);
	}

	
	
	
}
