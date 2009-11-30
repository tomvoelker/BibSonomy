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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.bibsonomy.util.tex.TexDecode;

/**
 * @author claus
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
		
		TexDecode enc = new TexDecode();
		Map<String, Author> bibtexAuthorMap = new HashMap<String, Author>(1500000);
		
		// rs.getString(1) = author feld vom bibtex eintrag
		// rs.getString(2) = content id feld
		
		int c = 0;
		
		String authorsAndEditors = "";
		String[] authors;
		String[] subNames;
		String bibtexAuthor;
		
		while(rs.next()) {
			// clear names
			authorsAndEditors = "";
			bibtexAuthor = "";
			
			if(rs.getString(1) != null) {
				authorsAndEditors += rs.getString(1);
			}
			if(rs.getString(3) != null) {
				authorsAndEditors += " and " + rs.getString(3);
			}
			if(authorsAndEditors == "") {
				continue;
			}
			c++;
			
			// split author field
			authors = authorsAndEditors.split(" and ");
			
			// get content ID from result
			lastContentId = rs.getLong(2);
						
			// loop over all authors			
			for(int i = 0; i < authors.length; i++) {
				bibtexAuthor = authors[i];
				authors[i] = authors[i].trim();
				authors[i] = enc.decode(authors[i]);
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
							// check if author already got the requested bibtex representation. if not, add the current one
							if(!bibtexAuthorMap.get(authors[i]).getBibtexNames().contains(bibtexAuthor)) {
								bibtexAuthorMap.get(authors[i]).addBibtexName(bibtexAuthor);
							}
						}
					}
					else {
						// not contained -> add to map 
						bibtexAuthorMap.put(authors[i], a);
					}
				}
			} // end for
			
			if (c % 10000 == 0) {
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
		Map<String, Author> authorMap = new HashMap<String, Author>();
		String authorName;
		String bibtexName;
		String[] subNames;
		int c = 0;
		TexDecode enc = new TexDecode();
		
		while(rs.next()) {
			authorName = rs.getString(2);
			bibtexName = authorName;
			
			if (authorName == null) continue;
			
			c++;
			authorName = enc.decode(authorName);
			
			if (!authorMap.containsKey(authorName)) {
				subNames = authorName.split(" ");
				Author a = new Author(getFirstName(subNames),
						getMiddleName(subNames),
						getLastName(subNames),
						authorName);				
				a.getContentIds().add(rs.getLong(3));
				a.setAuthorId(rs.getLong(1));
				authorMap.put(authorName, a);	
			} else {
				authorMap.get(authorName).getContentIds().add(rs.getLong(3));
				// check if author already got the requested bibtex representation. if not, add the current one
				if(!authorMap.get(authorName).getBibtexNames().contains(bibtexName)) {
					authorMap.get(authorName).addBibtexName(bibtexName);
				}
			}
			
			if (c % 10000 == 0) {
				logger.info("nr. of authors: " + authorMap.size());
				long memUsed = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB"); 
				logger.info("Calling GC...");
				System.gc();				
			}			
		}
		
		// clean memory
		rs  = null;
		enc = null;
		System.gc();
		
		return authorMap;
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
		ArrayList<Author> insertAuthors = new ArrayList<Author>();
		ArrayList<Author> updateAuthors = new ArrayList<Author>();
		
		String s;		
		Author a;
		Iterator<String> it = bibtexAuthorMap.keySet().iterator();

		logger.info("Computing which authors to insert / update...");		
		
		while (it.hasNext()) {
			s = it.next();
			a = bibtexAuthorMap.get(s);
			
			if(authorMap.containsKey(s)) {
				// set author id
				a.setAuthorId(authorMap.get(s).getAuthorId());
				int ctr = authorMap.get(s).getContentIds().size();
				
				// if there isn't an old id in the update user, add it to delete list
				if (lastId == 0) {
					for (long l : authorMap.get(s).getContentIds()) {
						if (!a.getContentIds().contains(l)) {
							a.getDeletedContentIds().add(l);
						}
					}
				}
				
				// remove existing id's for update author
				Iterator<Long> iter = a.getContentIds().iterator();
				while (iter.hasNext()) {
					if (authorMap.get(s).getContentIds().contains(iter.next())) {
						iter.remove();
					}
				}
				
				a.setCtr(ctr + a.getContentIds().size() - a.getDeletedContentIds().size());
				
				Iterator<String> nameIter = a.getBibtexNames().iterator();
				while (nameIter.hasNext()) {
					if (authorMap.get(s).getBibtexNames().contains(nameIter.next())) {
						nameIter.remove();
					}
				}
				
				// remember author to update
				if (a.getContentIds().size() > 0 
						|| a.getBibtexNames().size() > 0 
						|| a.getDeletedContentIds().size() > 0) {
					updateAuthors.add(a);
				}
				
			} else {
				insertAuthors.add(a);
			}
		}
		
		logger.info("Calling GC...");
		System.gc();
		
		
		logger.info("Inserting authors...");
		for (int i = 0; i < insertAuthors.size(); i++) { 
			if (i % 10000 == 0) {
				logger.info("nr. of bibtex authors inserted: " + i);
				long memUsed = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB");
				logger.info("calling GC...");
				System.gc();
			}
			try {
				db.insertAuthor(insertAuthors.get(i));
			} catch(Exception e) {
				e.printStackTrace();
				logger.fatal(e.getMessage());
				continue;
			}
		}
		logger.info("Done.");
		
		logger.info("Updating authors...");
		for (int j = 0; j < updateAuthors.size(); j++) {
			if (j % 10000 == 0) {
				logger.info("nr. of bibtex authors updated: " + j);
				long memUsed = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB");
				logger.info("calling GC...");
				System.gc();				
			}			
			try {
				db.updateAuthor(updateAuthors.get(j));
			} catch(Exception e) {
				e.printStackTrace();
				logger.fatal(e.getMessage());
				continue;
			}			
		}
		logger.info("Done.");
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