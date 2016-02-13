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
 * @author Zicheng Gao
 *
 */
public class MidiParser {

	// MIDI's own internal handling codes
	private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;

	private static boolean debug = false;

	/**
	 * Convert a directory. Output also.
	 * @param args
	 */
	public static void midiDirToNumDir(String inName, String outName) {
		// Directory to convert
		File inputDir = new File(inName);
		List<File> midiFiles = feedInputDir(inputDir);	
		String outputDirPath = prepOutputGetPath(outName);
		long startTime = System.nanoTime();
		
		for (File midiFile : midiFiles) {
			StringBuilder outSB = new StringBuilder(outputDirPath + '\\' + midiFile.getName());
			outSB.delete(outSB.lastIndexOf("."), outSB.length());
			outSB.append(".txt");

			convertSingleMidiToNum(midiFile, new File(outSB.toString()));
		}

		double secondsTaken = (System.nanoTime() - startTime) / 1_000_000_000;

		System.out.println("Finished! Processed " + midiFiles.size() + " files in " + secondsTaken + " seconds.");
	}

	private static void convertSingleMidiToNum(File midiFile, File outFile) {
		try {
			OutputStreamWriter outWriter = new OutputStreamWriter(new FileOutputStream(outFile));

			List<NoteEvent> noteList = parseMidi(midiFile);

			outWriter.write(noteList.get(0).toString());

			for (int i = 1; i < noteList.size() - 1; i++)
				outWriter.write('\n' + noteList.get(i).toString());
			
			outWriter.close();
		} catch (FileNotFoundException e) {
			System.err.println("Output file was not automatically created!?");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String prepOutputGetPath(String outName) {
		// Prepare output directory
		File outputDir = new File(outName);

		if (!outputDir.exists())
			outputDir.mkdirs();

		String outputDirPath = outputDir.getAbsolutePath();
		return outputDirPath;
	}

	private static List<File> feedInputDir(File inputDir) {
		// Filelist initialization
		List<File> midiFiles;

		// Ensure that it is a directory
		if (inputDir.isDirectory())
			midiFiles = (List<File>) FileUtils.listFiles(inputDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		else {
			throw new IllegalAccessError("Not a valid directory!");
		}

		if (midiFiles.size() == 0) {
			throw new IllegalAccessError("Input directory is empty!");
		}
		return midiFiles;
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

			// All tracks
			Track[] tracks = sequence.getTracks();
			
			// Slide all other tracks into track 0
			for (int i = 1; i < tracks.length; i++) {
				for (int j = 0; j < tracks[i].size(); j++) {
					tracks[0].add(tracks[i].get(j));
				}
			}

			// Current time - for the relative time
			long timeOfPrevNote = 0;

			// Run through track
			for (int i=0; i < tracks[0].size(); i++) {
				// Get event at location
				MidiEvent event = tracks[0].get(i);

				// Obtain message
				MidiMessage message = event.getMessage();

				// Ensure that it is ShortMessage
				if (message instanceof ShortMessage) {

					// TIME OF EVENT
					long absoluteTime = event.getTick();

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
								activeNote.duration = (int)(absoluteTime - activeNote.activationTime);	                    			
								unclosedNotes.remove(activeNote);
								velocity = activeNote.velocity;
								if (debug) {
									System.out.println("Closed note " + key + " @" + absoluteTime
											+ " " + (absoluteTime - timeOfPrevNote) + " ticks later,"
											+ " opened @" + activeNote.activationTime);
								}
							}
						}
						// NOTE ON
					} else if (sm.getCommand() == NOTE_ON) {
						// Time since previous event
						long timeSincePrevNote = absoluteTime - timeOfPrevNote;

						// Set time to this event time
						timeOfPrevNote = absoluteTime;

						NoteEvent note = new NoteEvent(absoluteTime, timeSincePrevNote, key, velocity, -1);
						noteEvents.add(note);
						unclosedNotes.add(note);

						if (debug) {
							System.out.println("Opened note " + key + " @" + absoluteTime
									+ " " + timeSincePrevNote + " ticks later,");
						}
					}

					// Otherwise the command is not a note command and we will ignore it for now
				}
				// Otherwise the message is some other thing and we will ignore it for now.

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

}
