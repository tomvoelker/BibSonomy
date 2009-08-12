package org.bibsonomy.importer.event.iswc.wordnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Reads white list for BibTeX title entries
 * @author: bkr
 * 
 */
public class WhiteListReader {

	private static final Log LOGGER = LogFactory.getLog(WhiteListReader.class);
	private String _whiteListFile;
	private HashMap<String, String> _whiteList;

	public WhiteListReader(String filename) {
		this._whiteListFile = filename;
		this._whiteList = new HashMap<String, String>();
	}

	public void readList() throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(new File(_whiteListFile)));
		String line;
		while ((line = buf.readLine()) != null) {
			String[] lineMap = line.split("\\s+"); 
			System.out.println(lineMap[1] + " maps to " + lineMap[0]);
			_whiteList.put(lineMap[1], lineMap[0]); 
		}
		buf.close(); 

	}

	public String getNormalizedTag(String term) {
		if (_whiteList.containsKey(term)) {
			return _whiteList.get(term);
		}
		System.err.println(term);
		return null;
	}
	

}
