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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Logger logger = Logger.getRootLogger();



	/**
	 * returns the last name of an author
	 * 
	 * @param s
	 * @return
	 */
	private static String getLastName(String s) {	
		if(s != null) {
			String[] subNames = s.split(" ");
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
	private static String getFirstName(String s) {
		if(s != null) {
			String[] subNames = s.split(" ");
			
			if(subNames.length > 1) {
				return subNames[0].trim();
			}
		}
		
		return "";
	}
	
	
	/**
	 * returns the middle part(s) of an author
	 * 
	 * @param s
	 * @return middle name
	 */
	private static String getMiddleName(String s) {
		StringBuffer buf = new StringBuffer();
		buf.append("");

		if(s != null) {
			String[] subNames = s.split(" ");
			
			for(int i = 1; i < (subNames.length - 1); ++i) {
				buf.append(subNames[1].trim());
			}
		}
		
		return new String(buf);
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
		long last = 0;
		
		TexEncode enc = new TexEncode();
		Map<String, Author> bibtexAuthorMap = new HashMap<String, Author>();
		
		while(rs.next()) {
			if(rs.getString(1) != null && !bibtexAuthorMap.containsKey(rs.getString(1))) {
				if(rs.getString(1).contains(" and ")) {
					String[] authors = rs.getString(1).split(" and ");
					
					for(String s : authors) {
						s = s.trim();
						s = enc.encode(s);
						if(s.length() > 2) {
							Author a = new Author(getFirstName(s),
									getMiddleName(s),
									getLastName(s), s);
							
							a.getContentIds().add(rs.getLong(2));
							bibtexAuthorMap.put(s, a);
						}
					}	
				} else {
					String s = rs.getString(1).trim();
					s = enc.encode(s);
					if(s.length() > 2) {
						Author a = new Author(getFirstName(s),
								getMiddleName(s),
								getLastName(s), s);
						
						a.getContentIds().add(rs.getLong(2));
						bibtexAuthorMap.put(s, a);
					}
				}
			} else if(rs.getString(1) != null){
				String encName = enc.encode(rs.getString(1));
				if(encName.contains(" and ")) {
					String[] authors = encName.split("and");
					
					for(String s : authors) {
						s = s.trim();
						if(s.length() > 2) {
							bibtexAuthorMap.get(s).getContentIds().add(rs.getLong(2));
						}
					}	
				} else {
					String s = rs.getString(1).trim();
					if(s.length() > 2) {
						bibtexAuthorMap.get(s).getContentIds().add(rs.getLong(2));
					}
				}
			}
			
			last = rs.getLong(2);
		}

		lastId = last;
		rs = null;
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
		
		while(rs.next()) {
			if(!authorMap.containsKey(rs.getString(2))) {
				Author a = new Author(enc.encode(getFirstName(rs.getString(2))),
						enc.encode(getMiddleName(rs.getString(2))),
						enc.encode(getLastName(rs.getString(2))),
						enc.encode(rs.getString(2)));
				
				a.getContentIds().add(rs.getLong(3));
				a.setAuthorId(rs.getLong(1));
				authorMap.put(rs.getString(2), a);	
			} else {
				authorMap.get(rs.getString(2)).getContentIds().add(rs.getLong(3));
			}
		}
		
		rs = null;
		enc = null;
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
		
		for(String s : sortHashMap(bibtexAuthorMap).keySet()) {

			if(authorMap.containsKey(s)) {
				// just a stupid validation for the count of the content id's to increase the performace
				if(authorMap.get(s).getContentIds().size() == bibtexAuthorMap.get(s).getContentIds().size()) {
					authorMap.remove(s);
					bibtexAuthorMap.remove(s);
				} else {
					updateAuthorMap.put(s, bibtexAuthorMap.get(s));
					updateAuthorMap.get(s).setAuthorId(authorMap.get(s).getAuthorId());
					
					ArrayList<Long> removeList = new ArrayList<Long>();
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
		
		for(String s : sortHashMap(insertAuthorMap).keySet()) {
			try {
				db.insertAuthor(insertAuthorMap.get(s));
			} catch(Exception e) {
				e.printStackTrace();
				logger.fatal(e.getMessage());
				continue;
			}
		}
		
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
			logger.setLevel(Level.WARN);
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
			Map<String, Author> authorMap = fetchAuthors(db);
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