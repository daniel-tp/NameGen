
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NameGen {
	static HashMap<String, ArrayList<String>> nameBase = new HashMap<String, ArrayList<String>>();
	static ArrayList<String> nameStart = new ArrayList<String>();
	static Random random = new Random();
	private static boolean debugEnabled = false;

	public static void debugMode(boolean debug) {
		debugEnabled = debug;
	}

	public static void parseFile(String file, boolean once) {
		nameStart.clear();
		nameBase.clear();
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				strLine = strLine.toLowerCase();
				char[] c = strLine.toCharArray();

				nameStart.add(c[0] + "" + c[1]);

				if (strLine.length() > 2) {
					for (int i = 0; i < strLine.length() - 2; i++) {
						if (nameBase.get(c[i] + "" + c[i + 1]) == null) {
							ArrayList<String> list = new ArrayList<String>();
							list.add("" + c[i + 2]);
							nameBase.put(c[i] + "" + c[i + 1], list);
						} else {
							ArrayList<String> list = nameBase.get(c[i] + ""
									+ c[i + 1]);

							if (!once
									|| (!list.contains("" + c[i + 2]) && once)) {

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
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getletter(String letters) {
		if (nameBase.get(letters) == null) {
			return null;
		}
		String returned = nameBase.get(letters).get(
				random.nextInt(nameBase.get(letters).size()));
		if (debugEnabled) {
			System.out.println("getting: " + letters + ", got: " + returned
					+ ". (" + letters + returned + ") ");
			for (String s : nameBase.get(letters)) {
				System.out.print(s);
			}
		}
		return returned;
	}

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

	public static void main(String[] args) {
		NameGen.parseFile("names.txt", true);
		for (int i = 0; i < 100; i++) {
			System.out.println(NameGen.generateName(2, 10));
		}
	}
}
