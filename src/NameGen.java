import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NameGen {
	private static HashMap<String, ArrayList<Character>> nameBase = new HashMap<String, ArrayList<Character>>();
	private static ArrayList<String> nameStart = new ArrayList<String>();
	private static Random random = new Random();
	private static boolean debugEnabled = false;

	/**
	 * Sets the debugEnabled boolean to the param
	 * 
	 * @param debug
	 */
	public static void debugMode(boolean debug) {
		debugEnabled = debug;
	}

	/**
	 * Reads the file line by line and runs the forEachLine function.
	 * 
	 * @param file
	 *            The wordlist input, each name/word must be on a separate line
	 * @param once
	 *            Whether a trigram is entered into nameBase multiple times or
	 *            just once, multiple times weights the random choice.
	 * 
	 */
	public static void parseFile(String file, boolean once) {
		nameStart.clear();
		nameBase.clear();
		try {
			InputStream inputStream = NameGen.class.getClassLoader()
					.getResourceAsStream("names.txt");
			DataInputStream in = new DataInputStream(inputStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {

				forEachLine(strLine, once);

			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Deals with the given line and puts it into the nameBase hashmap.
	 * 
	 * @param line
	 *            The line to be dealt with
	 * @param once
	 *            Whether a trigram is entered into nameBase multiple times or
	 *            just once, multiple times weights the random choice.
	 */
	private static void forEachLine(String line, boolean once) {
		StringBuilder sb = new StringBuilder(2);
		String strLine = line.toLowerCase();
		char[] c = strLine.toCharArray();

		sb.append(c[0]);
		sb.append(c[1]);
		nameStart.add(sb.toString());
		// Should move this to a separate function sometime
		if (strLine.length() > 2) {
			for (int i = 0; i < strLine.length() - 2; i++) {
				sb = new StringBuilder(2);
				sb.append(c[i]);
				sb.append(c[1 + 1]);
				if (nameBase.get(sb.toString()) == null) {
					ArrayList<Character> list = new ArrayList<Character>();
					list.add(c[i + 2]);
					nameBase.put(sb.toString(), list);
				} else {
					ArrayList<Character> list = nameBase.get(sb.toString());

					if (!once || (!list.contains(c[i + 2]))) {

						list.add(c[i + 2]);
						nameBase.put(sb.toString(), list);
					}
				}

			}
		} else {
			if (debugEnabled)
				System.out.println("Input rejected, too short.");
		}
		if (debugEnabled)
			System.out.println(strLine);
	}

	//
	/**
	 * Gets a letter from nameBase according to the given letters (of which
	 * there should be two)
	 * 
	 * @param letters
	 *            The input of two letters to find the 3rd random letter of, e.g
	 *            "FA" will get "D" to make "FAD"
	 * @return Returns the 3rd letter, for example the "D" in "FAD"
	 */
	private static Character getletter(String letters) {
		if (nameBase.get(letters) == null) {
			return null;
		}
		Character returned = nameBase.get(letters).get(
				random.nextInt(nameBase.get(letters).size()));
		if (debugEnabled) {
			System.out.println("getting: " + letters + ", got: " + returned
					+ ". (" + letters + returned + ") ");

		}
		return returned;
	}

	/**
	 * Generates the name.
	 * 
	 * @param minsize
	 *            The minsize the name should be, this does not correlate with
	 *            the actual size, but instead how many times the loop to
	 *            getting the name will go before stopping
	 * @param maxsize
	 *            The maxsize the name should be, this does not correlate with
	 *            the actual size, but instead how many times the loop to
	 *            getting the name will go before stopping
	 * @return Returns the randomly generated name
	 */
	public static String generateName(int minsize, int maxsize) {
		int length = random.nextInt(maxsize - minsize) + minsize;
		
		StringBuilder sb = new StringBuilder(maxsize + 3);
		sb.append(nameStart.get(random.nextInt(nameStart.size())));
		for (int i = 0; i < length; i++) {
			Character nextLetter = getletter(sb.substring(sb.length() - 2));
			if (nextLetter == null) {
				return sb.toString();
			} else {
				sb.append(nextLetter);
				if (debugEnabled)
					System.out.println(sb.toString());
			}
		}
		StringBuilder sb2 = new StringBuilder(maxsize+3);
		sb2.append(sb.substring(0, 1).toUpperCase());
		sb2.append(sb.substring(1));
		if (debugEnabled)
			System.out.println("NameGen: " + sb2.toString());
		return sb2.toString();
	}

	/**
	 * An example of running the random name generator.
	 * 
	 * @param args
	 *            only input should be the how many names do you want to
	 *            generate.
	 */
	public static void main(String[] args) {
		int count = 100;
		if (args.length > 0) {
			count = Integer.parseInt(args[0]);
		}
		NameGen.parseFile("names.txt", true);
		for (int i = 0; i < count; i++) {
			System.out.println(NameGen.generateName(2, 10));
		}
	}
}
