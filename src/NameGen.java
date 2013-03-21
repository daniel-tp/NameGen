import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	// set debugEnabled bool to what boolean is given
	public static void debugMode(boolean debug) {
		debugEnabled = debug;
	}

	// Parse the given file into the hashmap nameBase, also putting the start of
	// the names into nameStart.
	// "once" is whether the letters will be put into the hashmap once or
	// multiple, with multiple weighting the generated names towards the
	// pattern that comes up more in the given input
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

	// Gets a letter from nameBase according to the given letters (of which
	// there should be two)
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

	// Generates the name, with minsize and maxsize of the name, with the name
	// being random between them. It is not an even distribution of size and
	// entirely depends on the input.
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

	// An example of it being run, with names.txt, only adding once and printing
	// 100 names between size 2 and 10.
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
