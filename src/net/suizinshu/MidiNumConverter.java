package net.suizinshu;

import java.io.FileNotFoundException;
import java.io.IOError;
import java.util.Scanner;

public class MidiNumConverter {

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length > 0)
			processInput(args);
		else {
			Scanner scanner = new Scanner(System.in);
			System.out.println(
					"============================\n" + 
					"||   MIDI NUM CONVERTER   ||\n" +
					"============================\n" +
					"\n" +
					"\t0: Diagnose MIDI\n" +
					"\t1: MIDI Directory to TXT Directory\n" +
					"\t2: TXT File to MIDI file" +
					"\t3: TXT File to MIDI file (same filename same location different extension)\n"
					);
			String tmp;
			switch (scanner.nextInt()) {
				case 0:
					System.out.println("Please input midi to diagnose:");
					MidiDiagnoser.diagnose(scanner.next());
					break;
				case 1:
					System.out.println("Please input midi directory to convert:");
					tmp = scanner.next();
					System.out.println("Please input output directory to target:");
					MidiParser.midiDirToNumDir(tmp, scanner.next());
					break;
				case 2:
					System.out.println("Please input text file to convert:");
					tmp = scanner.next();
					System.out.println("Please input target midi to output:");
					String targ = scanner.next();
					System.out.println("Please input desired tempo:");
					MidiBuilder.numToMidi(tmp, targ, scanner.next());
					break;
//				case 3:
//					
			}
			scanner.close();
		}
		
	}

	private static void processInput(String[] args) throws IOError {
		if (args[0].equalsIgnoreCase("toMid")) {
			if (args.length <= 4) {
				System.err.println("Please give input: TXT_FILE MIDI_FILE TEMPO");
			} 
			else {
				try {
					MidiBuilder.numToMidi(args[1], args[2], args[3]);
				} catch (FileNotFoundException e) {
					System.err.println("File not found.");
				}
			}
		}
		else if (args[0].equalsIgnoreCase("toNum")) {
			if (args.length <= 3) {
				if (args.length < 2) {
					System.err.println("Please give input: \n"
							+ "\tIN_DIRECTORY OUT_DIRECTORY\n"
							+ "Where IN_DIRECTORY contains .midi files to be converted into txt and put"
							+ " into the OUT_DIRECTORY.");
					return;
				}
				else {
					MidiParser.midiDirToNumDir(args[1], args[2]);
				}
			}
		}
		else if (args[0].equalsIgnoreCase("diag")) {
				MidiDiagnoser.diagnose(args[1]);
		}
	}

}
