package com.tpps.technicalServices.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;

/***
 * basic file operations
 * 
 * @author Steffen Jacobs
 */
public class FileParser {
	/**
	 * 
	 * loads UTF-8-Text from a file (line by line)
	 * 
	 * @param file
	 *            the file to load the lines from
	 * @return the loaded lines
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
		GameLog.log(MsgType.INFO ,res.size() + " entries loaded from " + file);
		return res;
	}

	/**
	 * creates a directory-tree safely
	 * 
	 * @param f
	 *            the recursive directory-tree to create
	 */
	public static void createDirectoryTree(File f) {
		File parent = f.getParentFile();
		if (parent == null || !parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Could not create dir: " + parent);
		}
	}
}