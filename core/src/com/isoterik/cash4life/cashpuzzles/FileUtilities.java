package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.isoterik.cash4life.GlobalConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtilities {
	
	public static String getDataDirectoryFullPath(String dataDirectory) throws IOException {
		// This is how you get access to a folder that is internal to your application
		// You can't access it the way you access external files
		String currentPath = Gdx.files.internal(GlobalConstants.CASH_PUZZLES_ASSETS_HOME).path(); //new java.io.File(".").getCanonicalPath(); // Get current path
		return currentPath + File.separatorChar + dataDirectory;  // Add on name of data directory
	}
	
	public static ArrayList<String> getListOfLinesFromFile(String dataDirectory, String inputFile) {
		FileHandle fileHandle = null;
		String dataPath = "";
		ArrayList<String> result = new ArrayList<>();
		
		try {
			dataPath =  getDataDirectoryFullPath(dataDirectory);

			fileHandle = Gdx.files.internal(dataPath + File.separatorChar + inputFile);
		} 	catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Unable to load file: " + dataPath + File.separatorChar + inputFile);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to get path: " + dataPath + File.separatorChar + inputFile);
			System.exit(1);
		}

		// You can use the FileHandle to read the file automatically without a FileReader
		Scanner in = new Scanner(fileHandle.readString());
		
		String line = " ";
		while (in.hasNext()) {
			line = in.nextLine();
			result.add(line.toLowerCase());
		}
		
		return result;		
	}
}
