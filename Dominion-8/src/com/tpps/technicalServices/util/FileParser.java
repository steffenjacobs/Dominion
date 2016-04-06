package com.tpps.technicalServices.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*** @author Steffen Jacobs */
public class FileParser {
	/**
	 * 
	 * loads UTF-8-Text from a file (line by line)
	 * 
	 * @author Steffen Jacobs
	 */
	public static ArrayList<String> loadLines(String file) {
		ArrayList<String> res = new ArrayList<>();
		String line;
		File f = new File(file);
		try {
			FileParser.createDirectoryTree(f);
			f.createNewFile();

			BufferedReader br = new BufferedReader(new FileReader(f));

			line = br.readLine();
			while (line != null) {
				res.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage() + " - " + f.getAbsolutePath());
		}
		System.out.println(res.size() + " entries loaded from " + file);
		return res;
	}

	/**
	 * creates a direcory-tree safely
	 * 
	 * @author Steffen Jacobs
	 */
	public static void createDirectoryTree(File f) {
		File parent = f.getParentFile();
		if (parent == null || !parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Could not create dir: " + parent);
		}
	}
}