import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NameGen {
	private static HashMap<String, ArrayList<String>> nameBase = new HashMap<String, ArrayList<String>>();
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
		String strLine = line.toLowerCase();
		char[] c = strLine.toCharArray();

		nameStart.add(c[0] + "" + c[1]);
		// Should move this to a separate function sometime
		if (strLine.length() > 2) {
			for (int i = 0; i < strLine.length() - 2; i++) {
				if (nameBase.get(c[i] + "" + c[i + 1]) == null) {
					ArrayList<String> list = new ArrayList<String>();
					list.add("" + c[i + 2]);
					nameBase.put(c[i] + "" + c[i + 1], list);
				} else {
					ArrayList<String> list = nameBase.get(c[i] + "" + c[i + 1]);

					if (!once || (!list.contains("" + c[i + 2]) && once)) {

						list.add("" + c[i + 2]);
						nameBase.put(c[i] + "" + c[i + 1], list);
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
	private static String getletter(String letters) {
		if (nameBase.get(letters) == null) {
			return null;
		}
		String returned = nameBase.get(letters).get(
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
		String name = nameStart.get(random.nextInt(nameStart.size()));

		for (int i = 0; i < length; i++) {
			String nextLetter = getletter(name.substring(name.length() - 2));
			if (nextLetter == null) {
				return name;
			} else {
				name = name + nextLetter;
				if (debugEnabled)
					System.out.println(name);
			}
		}
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		if (debugEnabled)
			System.out.println("NameGen: " + name);
		return name;
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
