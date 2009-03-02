package org.bibsonomy.batch.authors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.bibsonomy.util.tex.TexEncode;

/**
 * @author nmrd
 * @version $Id$
 */
public class BatchAuthors {

	private final static String PID_FILE = "batch_authors.pid";
	private static long lastId;
	private static Logger logger = Logger.getLogger(BatchAuthors.class);



	/**
	 * returns the last name of an author
	 * 
	 * @param s
	 * @return
	 */
	private static String getLastName(String[] subNames) {
		if(subNames.length > 1) {
			return subNames[subNames.length - 1].trim();
		}
		return "";
	}
	
	
	/**
	 * returns the first name of an author
	 * 
	 * @param s
	 * @return first name
	 */
	private static String getFirstName(String[] subNames) {
		if(subNames.length > 1) {
				return subNames[0].trim();
		}
		return "";
	}
	
	
	/**
	 * returns the middle part(s) of an author
	 * 
	 * @param s
	 * @return middle name
	 */
	private static String getMiddleName(String[] subNames) {
		if (subNames.length > 2) {
			String mn = "";
			for(int i = 1; i < (subNames.length - 1); ++i) {
				mn +=  subNames[i].trim();
			}
			return mn;
		}
		return "";
	}
	
	
	/**
	 * fetches the authors, represented by the bibtex table and put them into a map - represented
	 * as Author object. normalizes the author names 
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	private static Map<String, Author> fetchBibtexAuthors(AuthorDB db) throws SQLException {
		ResultSet rs = db.getBibtexAuthors(lastId);		
		// memory consumption here: 280 MB (result set)
		
		// last content Id fetched
		long lastContentId = 0;
		
		TexEncode enc = new TexEncode();
		Map<String, Author> bibtexAuthorMap = new HashMap<String, Author>(1500000);
		
		// rs.getString(1) = author feld vom bibtex eintrag
		// rs.getString(2) = content id feld
		
		int c = 0;
		
		String[] authors;
		String[] subNames;
		
		while(rs.next()) {
			if(rs.getString(1) == null) {
				continue;
			}
			c++;
			
			// split author field
			authors = rs.getString(1).split(" and ");
			
			// get content ID from result
			lastContentId = rs.getLong(2);
						
			// loop over all authors			
			for(int i = 0; i < authors.length; i++) {
				authors[i] = authors[i].trim();
				authors[i] = enc.encode(authors[i]);
				if(authors[i].length() > 2) {
					// we split the author name already here - if we do it in the
					// getFirstName... - functions, we consume a lot of temporary 
					// memory
					subNames = authors[i].split(" ");
					// create new author object
					Author a = new Author(getFirstName(subNames),
							getMiddleName(subNames),
							getLastName(subNames), authors[i]);
					// add current content ID to author object
					a.getContentIds().add(lastContentId);
					// check if author is already in map
					if (bibtexAuthorMap.containsKey(authors[i]) ) {
						// is contained -> add content id
						if(authors[i].length() > 2) {
							bibtexAuthorMap.get(authors[i]).getContentIds().add(lastContentId);
						}
					}
					else {
						// not contained -> add to map 
						bibtexAuthorMap.put(authors[i], a);
					}
				}
			} // end for
			
			if (c % 1000 == 0) {
				logger.info("nr. of bibtex authors: " + bibtexAuthorMap.size());
				long memUsed = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB");
				logger.info("Calling GC...");
				System.gc();
			}
		}
						
		lastId = lastContentId;
		
		// clean memory
		rs = null;
		enc = null;
		System.gc();
		
		return bibtexAuthorMap;
	}
	
	
	/**
	 * fetches the authors from the specific tables and put them as object into
	 * a map
	 * 
	 * @param db
	 * @return Map<String, Author>
	 * @throws SQLException
	 */
	private static Map<String, Author> fetchAuthors(AuthorDB db) throws SQLException {
		ResultSet rs = db.getAuthors();

		TexEncode enc = new TexEncode();
		Map<String, Author> authorMap = new HashMap<String, Author>();
		
		String authorName;
		String[] subNames;
		int c = 0;
		
		while(rs.next()) {
			authorName = rs.getString(2);			
			if (authorName == null) continue;
			c++;
			if(!authorMap.containsKey(authorName)) {
				subNames = authorName.split(" ");
				Author a = new Author(enc.encode(getFirstName(subNames)),
						enc.encode(getMiddleName(subNames)),
						enc.encode(getLastName(subNames)),
						enc.encode(authorName));				
				a.getContentIds().add(rs.getLong(3));
				a.setAuthorId(rs.getLong(1));
				authorMap.put(authorName, a);	
			} else {
				authorMap.get(authorName).getContentIds().add(rs.getLong(3));
			}
			
			if (c % 10000 == 0) {
				logger.info("nr. of bibtex authors: " + authorMap.size());
				long memUsed = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB"); 
				logger.info("Calling GC...");
				System.gc();				
			}			
		}
		
		// clean memory
		rs = null;
		enc = null;
		System.gc();
		
		return authorMap;
	}
	
	
	/**
	 * returns a sorted tree map which represents a hashmap
	 * 
	 * @param map
	 * @return
	 */
	private static TreeMap<String, Author> sortHashMap(Map<String, Author> map) {
		return new TreeMap<String, Author>(map);
	}
	
	
	/**
	 * verifies the authors and updates / inserts those, who are needed to be updated / inserted
	 * 
	 * @param db
	 * @param bibtexAuthorMap
	 * @param authorMap
	 * @throws SQLException
	 */
	private static void blastData(AuthorDB db, Map<String, Author> bibtexAuthorMap, Map<String, Author> authorMap) throws SQLException {
		Map<String, Author> updateAuthorMap = new HashMap<String, Author>();
		Map<String, Author> insertAuthorMap = new HashMap<String, Author>();
		
		ArrayList<Long> removeList = new ArrayList<Long>();
		
		logger.info("Computing which authors to insert / update...");
		for(String s : sortHashMap(bibtexAuthorMap).keySet()) {

			if(authorMap.containsKey(s)) {
				// just a stupid validation for the count of the content id's to increase the performace
				if(authorMap.get(s).getContentIds().size() == bibtexAuthorMap.get(s).getContentIds().size()) {
					authorMap.remove(s);
					bibtexAuthorMap.remove(s);
				} else {
					updateAuthorMap.put(s, bibtexAuthorMap.get(s));
					updateAuthorMap.get(s).setAuthorId(authorMap.get(s).getAuthorId());
					
					removeList.clear();
					for(long l : bibtexAuthorMap.get(s).getContentIds()) {
						if(authorMap.get(s).getContentIds().contains(l)) {
							removeList.add(l);
						}
					}
					
					for(long l : removeList) {
						updateAuthorMap.get(s).getContentIds().remove(l);
					}
					
					authorMap.remove(s);
					bibtexAuthorMap.remove(s);
				}
			} else {
				insertAuthorMap.put(s, bibtexAuthorMap.get(s));
				authorMap.remove(s);
				bibtexAuthorMap.remove(s);
			}
		}
		
		logger.info("Inserting authors...");
		for(String s : sortHashMap(insertAuthorMap).keySet()) {
			try {
				db.insertAuthor(insertAuthorMap.get(s));
			} catch(Exception e) {
				e.printStackTrace();
				logger.fatal(e.getMessage());
				continue;
			}
		}
		
		logger.info("Updating authors...");
		for(String s : sortHashMap(updateAuthorMap).keySet()) {
			try {
				db.updateAuthor(updateAuthorMap.get(s));
			} catch(Exception e) {
				e.printStackTrace();
				logger.fatal(e.getMessage());
				continue;
			}			
		}
	}
	
	
	/**
	 * load the property file with the stored last id
	 * 
	 * @return last id : long
	 */
	private static long loadProperties(String file) {
		Properties prop = new Properties();
		long id = 0;
		
		try {
			prop.load(new FileInputStream(file));
			id = Long.parseLong(prop.getProperty("last_row"));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return id;
	}
	
	
	/**
	 * stores the last id in a property file
	 */
	private static void storeProperties(String file) {
		Properties prop = new Properties();
		prop.setProperty("last_row", String.valueOf(lastId));
		
		try {
			prop.store(new FileOutputStream(file), null);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * returns the "TMP" path
	 * 
	 * @return /path/to/tmp ie /tmp
	 */
	private static String getTmpPath() {
		final String TMP_PATH = "/tmp/";
		
		String tmpPath = System.getenv().get("TMP");
		if(tmpPath != null) {
			return tmpPath;
		}
		
		return TMP_PATH;
	}
	
	
	/**
	 * tests, if there exists a pid file in the tmp-space
	 * 
	 * @return boolean : true, if process is running
	 */
	private static boolean amIRunning() {
		File pid = new File(getTmpPath() + PID_FILE); 
		
		if(pid.exists()) {
			return true;
		}
		
		try {
			pid.createNewFile();
		} catch (IOException ex) {
			System.err.println("Can't create pid file: " + getTmpPath() + PID_FILE);
			System.exit(0);
		}
		
		return false;
	}
	
	
	/**
	 * removes the pid file, if it exists
	 * 
	 * @return boolean : true, if pid was deleted
	 */
	private static boolean removePidFile() {
		File pid = new File(getTmpPath() + PID_FILE); 
		
		if(pid.delete()) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * loads the last id from a property file, fetches all bibtex_authors and authors, stores the new author
	 * hierarchy and writes a property file with the new 'last id' - worked on
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String database		= null;
		
		//property files have to be renamed, if all files are located in their correct packages
		String props		= "lastRow.txt";
		
		try {
			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.addAppender(consoleAppender);
			FileAppender fileAppender = new FileAppender(layout, "batch_authors.log", false);
			logger.addAppender(fileAppender);
			logger.setLevel(Level.INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		//args shiften
		if(args.length != 1) {
			System.err.println("Please enter a database name as first argument\n");
			System.exit(0);
		} 

		if(amIRunning()) {
			System.err.println("pid file already exists: " + getTmpPath() + PID_FILE);
			System.exit(0);
		}
		
		database = args[0];
		
		try {
			lastId = loadProperties(props);
			
			AuthorDB db = new AuthorDB();
			db.initDBConnection(database);
			Map<String, Author> bibtexAuthorMap = fetchBibtexAuthors(db);
			logger.info ("Done with fetching bibtex authors");
			Map<String, Author> authorMap = fetchAuthors(db);
			logger.info ("Done with fetching authors");
			db.closeDBConnection();

			db = null;
			
			db = new AuthorDB();
			db.initMasterDBConnection(database);
			blastData(db, bibtexAuthorMap, authorMap);
			db.closeDBConnection();

			storeProperties(props);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} 
		
		if(!removePidFile()) {
			System.err.println("can't delete pid file: " + getTmpPath() + PID_FILE);
		}
	}
}