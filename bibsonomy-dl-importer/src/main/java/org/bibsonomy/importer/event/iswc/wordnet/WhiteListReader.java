package org.bibsonomy.importer.event.iswc.wordnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * 
 * Reads white list for BibTeX title entries
 * @author: bkr
 * 
 */
public class WhiteListReader {

	private static final Logger LOGGER = Logger
			.getLogger(WhiteListReader.class);
	private String _whiteListFile;
	private HashMap<String, Integer> _whiteList;

	public WhiteListReader(String filename) {
		this._whiteListFile = filename;
		this._whiteList = new HashMap<String, Integer>();
	}

	public void readList() throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(new File(
				_whiteListFile)));
		String line;
		while ((line = buf.readLine()) != null) {
			String[] lineMap = line.split("\t"); 
			for (String token: lineMap){
				_whiteList.put(token, 1); 
			}
		}
		buf.close(); 

	}

	public boolean exists(String term) {
		return _whiteList.containsKey(term);
	}
	

}
