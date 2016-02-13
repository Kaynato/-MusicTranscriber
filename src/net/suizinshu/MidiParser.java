package net.suizinshu;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Convert midi files into .txt file representations.
 * 
 * Convert all midi files into a standardized tempo.
 * 
 * Each note is expressed as such:
 * 		NOTE_NUM DURATION VELOCITY
 * 
 * 		And separated by a \n.
 * 
 * 
 * @author Zicheng Gao
 *
 */
public class MidiParser {

	// MIDI's own internal handling codes
	private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;
	
	private static final boolean debug = true;

	/**
	 * Convert a directory. Output also.
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length <= 0) {
			args = new String[2];
			// TODO use actual things.
			args[0] = "D:/Users/Brian/Music/pianomidi/tester";
			args[1] = "D:/Cloud/Dropbox/College/CWRU/Hackathon/midiRNN/tester";
		}

		// Directory to convert
		File inputDir = new File(args[0]);

		// Filelist initialization
		List<File> midiFiles;

		// Ensure that it is a directory
		if (inputDir.isDirectory())
			midiFiles = (List<File>) FileUtils.listFiles(inputDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		else {
			System.err.println("Not a valid directory!");
			return;
		}

		if (midiFiles.size() == 0) {
			System.err.println("Directory is empty!");
			return;
		}	

		// Prepare output directory
		File outputDir = new File(args[1]);

		if (!outputDir.exists())
			outputDir.mkdirs();

		String outputDirPath = outputDir.getAbsolutePath();

		for (File midiFile : midiFiles) {
			StringBuilder outSB = new StringBuilder(outputDirPath + '\\' + midiFile.getName());
			outSB.delete(outSB.lastIndexOf("."), outSB.length());
			outSB.append(".txt");
			
			String outFileName = outSB.toString();
			
			File outFile = new File(outFileName);

			try {
				OutputStreamWriter outWriter = new OutputStreamWriter(new FileOutputStream(outFile));
				
				List<NoteEvent> noteList = parseMidi(midiFile);
				
				outWriter.write(noteList.get(0).toString());
				
				for (int i = 1; i < noteList.size() - 1; i++) {
					outWriter.write('\n' + noteList.get(i).toString());
				}
				
				
				outWriter.close();
			} catch (FileNotFoundException e) {
				System.err.println("Output file was not automatically created!?");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
		}

	}

	private static List<NoteEvent> parseMidi(File midiFile) {

		/**
		 * Relative time, absolute note
		 * Absolute velocity, absolute duration
		 * 
		 * list of 4-vectors for note representation which can be fed into ANN.
		 */

		List<NoteEvent> noteEvents = new LinkedList<NoteEvent>();
		List<NoteEvent> unclosedNotes = new LinkedList<NoteEvent>();

		try {
			// Obtain midi information
			Sequence sequence = MidiSystem.getSequence(midiFile);

			// Current time - for the relative time
			long prevTick = 0;

			// Read track from sequence
			for (Track track :  sequence.getTracks()) {

				// Run through track
				for (int i=0; i < track.size(); i++) {
					// Get event at location
					MidiEvent event = track.get(i);

					// TIME OF EVENT
					long tick = event.getTick();

					// Time since previous event
					long timeSincePrev = tick - prevTick;

					// Set time to this event time
					prevTick = tick;

					// Obtain message
					MidiMessage message = event.getMessage();

					// Ensure that it is ShortMessage
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;

						// KEY NUM
						int key = sm.getData1();

						// VELOCITY
						int velocity = sm.getData2();

						// NOTE OFF
						if (sm.getCommand() == NOTE_OFF || velocity == 0) {
							for (int j = 0; j < unclosedNotes.size(); j++) {
								NoteEvent activeNote = unclosedNotes.get(j);
								if (activeNote.key == key) {
									activeNote.duration = (int)(tick - activeNote.activationTime);	                    			
									unclosedNotes.remove(activeNote);
								}
								if (debug) {
									System.out.println("Closed note " + key + " @" + tick
											+ " " + timeSincePrev + " ticks later,"
											+ " opened @" + activeNote.activationTime);
								}
							}
							// NOTE ON
						} else if (sm.getCommand() == NOTE_ON) {
							NoteEvent note = new NoteEvent(tick, timeSincePrev, key, velocity, -1);
							noteEvents.add(note);
							unclosedNotes.add(note);
							
							if (debug) {
								System.out.println("Opened note " + key + " @" + tick
										+ " " + timeSincePrev + " ticks later,");
							}
						}
						// Otherwise the command is not a note command and we will ignore it for now
					}
					// Otherwise the message is some other thing and we will ignore it for now.
				}
			}
		} catch (InvalidMidiDataException e) {
			System.err.println("Midi data invalid!");
		} catch (IOException e) {
			System.err.println("I/O Exception occurred!");
		}

		if (!unclosedNotes.isEmpty())
			System.err.println("Warning! Some notes remain unclosed.");
		
//		System.out.println("Converted " + midiFile.getName());

		return noteEvents;
	}

	private static final class NoteEvent {
		public long activationTime;
		public long relativeTime;
		public int key;
		public int velocity;
		public int duration;

		public NoteEvent(long activationTime, long relativeTime, int key, int velocity, int duration) {
			this.activationTime = activationTime;
			this.relativeTime = relativeTime;
			this.key = key;
			this.velocity = velocity;
			this.duration = duration;
		}

		@Override
		public String toString() {
			return (relativeTime + " " + key + " " + velocity + " " + duration);
		}

	}

}
