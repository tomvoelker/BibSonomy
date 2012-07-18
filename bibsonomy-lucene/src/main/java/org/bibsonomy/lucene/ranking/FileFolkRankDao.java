package org.bibsonomy.lucene.ranking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileFolkRankDao implements FolkRankDao {

	private BufferedReader bufferedReader;
	private String currentLine;
	
	private String currentHash;
	private String nextHash;
	private List<FolkRankInfo> currentFolkRanks;
	
	private File file;
	
	public FileFolkRankDao(String fileName)  {
		
		//File file = new File(fileName);
		file = new File(fileName);
		
		if (!file.exists()) {
			System.out.println("ERROR: FolkRank file does not exist: " + fileName);
		}
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		currentFolkRanks = new ArrayList<FolkRankInfo>();
		
		try {
			currentLine = bufferedReader.readLine();
			readNextFolkRanks();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	@Override
//	public List<FolkRankInfo> getTagUserFolkRanks(String hash) {
//		
//		List<FolkRankInfo> folkRanks = new ArrayList<FolkRankInfo>();
//		
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
//			
//			boolean hashFound = false;
//			
//			String line;
//			while ((line = br.readLine()) != null) {
//				
//				String[] items = line.split("\t");
//				
//				if (items[0].equals(hash)) {
//					hashFound = true;
//					addFolkRankOfLine(items, folkRanks);
//				}
//				
//				if (!items[0].equals(hash) && hashFound) {
//					br.close();
//					break;
//				}
//			}
//			
//			br.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return folkRanks;
//	}
	
	@Override
	public List<FolkRankInfo> getTagUserFolkRanks(String hash) {

		if (hash.equals(currentHash)) {
			return currentFolkRanks;
		} else if (hash.equals(nextHash)) {
			try {
				readNextFolkRanks();
				return currentFolkRanks;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
		} else if (hash.compareTo(nextHash) > 0 && !nextHash.equals("")) {
			try {
				readNextFolkRanks();
				return getTagUserFolkRanks(hash);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return new ArrayList<FolkRankInfo>();
		}
	}

	private void readNextFolkRanks() throws IOException {
		
		String[] items = currentLine.split("\t");
		currentHash = items[0];
		currentFolkRanks.clear();
		addFolkRankOfLine(items, currentFolkRanks);
		
		while (/*(currentLine = bufferedReader.readLine()) != null*/ bufferedReader.ready()) {
			currentLine = bufferedReader.readLine();
			items = currentLine.split("\t");
			if (!currentHash.equals(items[0])) {
				nextHash = items[0];
//				System.out.println("Next hash: " + nextHash);
				return;
			}
			
			addFolkRankOfLine(items, currentFolkRanks);
		}
		
		nextHash = ""; // reached end of file
	}
	
	private void addFolkRankOfLine(String[] items, List<FolkRankInfo> folkRanks) {
		
		String weight = items[1];
		String dim = items[2];
		String item = items[3];
		
//		if (item.length() == 0 || item.length() > 50 || item.contains(" ")) {
//			return;
//		}
		
		folkRanks.add(new FolkRankInfo(Float.parseFloat(weight), Integer.parseInt(dim), item));
	}
}
