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
					"||   MIDI NUM CONVERTER   || - Absolute Key Edition\n" +
					"============================ - \"Contemporary Piano Edition\"\n" +
					"Zicheng Gao\n" +
					"\n" +
					"\t\tPrepares polyphonic midi into numeric CSV-like format \n" + 
					"\t\tfor training character-based Recurrent Neural Networks.\n" +
					"\n" +
					"\t0: Diagnose MIDI\n" +
					"\t1: MIDI Directory to TXT Directory\n" +
					"\t2: TXT File to MIDI file\n" +
					"\t3: TXT File to MIDI file (same filename same location different extension)\n" +
					"\t99: Exit."
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
					System.out.println("...Completed.");
					break;
				case 3:
					System.out.println("Please input text file to convert:");
					tmp = scanner.next();
					StringBuilder sb = new StringBuilder(tmp);
					sb.delete(sb.lastIndexOf("."), sb.length()).append(".mid");
					System.out.println("Please input desired tempo:");
					MidiBuilder.numToMidi(tmp, sb.toString(), scanner.next());
					break;
			}
			System.out.println("\nHave a nice day!");
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
