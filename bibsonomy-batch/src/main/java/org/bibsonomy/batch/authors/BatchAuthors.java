package org.bibsonomy.batch.authors;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.util.tex.TexDecode;

/**
 * @author claus
 * @version $Id$
 */
public class BatchAuthors {
	private static Logger logger = Logger.getLogger(BatchAuthors.class);


	private final static String PID_FILE = "batch_authors.pid";
	private static final String LAST_ID_FILE = "last_id.properties";
	private static final String TMP_PATH = "/tmp/";

	/**
	 * loads the last id from a property file, fetches all bibtex_authors and authors, stores the new author
	 * hierarchy and writes a property file with the new 'last id' - worked on
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		configLogging();
		
		// don't run twice
		if (amIRunning()) {
			System.err.println("pid file already exists: " + getTmpPath() + PID_FILE);
			System.exit(0);
		}
		
		try {
			final long lastId = getLastId();
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			final AuthorDB database = getDatabase();
			final long currentContentId = database.getLastContentId();
			final Map<String, Author> bibtexAuthorMap = fetchBibtexAuthors(database, lastId, currentContentId);
			logger.info ("Done with fetching bibtex authors");
			final Map<String, Author> authorMap = fetchAuthors(database);
			logger.info ("Done with fetching authors");
			blastData(database, lastId, bibtexAuthorMap, authorMap);
			database.closeDBConnection();

			saveLastId(currentContentId);
		} catch (final Exception ex) {
			logger.error("exception while executing batch", ex);
		}
		
		if (!removePidFile()) {
			System.err.println("can't delete pid file: " + getTmpPath() + PID_FILE);
		}
	}

	private static AuthorDB getDatabase() throws SQLException, Exception {
		final Properties properties = new Properties();
		properties.load(new FileReader(new File("./batchAuthors.properties")));
		final String masterUrl = properties.getProperty("master.url");
		final String masterUsername = properties.getProperty("master.username");
		final String masterPassword = properties.getProperty("master.password");
		final Connection slaveConnection = DriverManager.getConnection(properties.getProperty("slave.url", masterUrl), properties.getProperty("slave.username", masterUsername), properties.getProperty("slave.password", masterPassword));
		final Connection masterConnection = DriverManager.getConnection(masterUrl, masterUsername, masterPassword);
		final AuthorDB database = new AuthorDB(slaveConnection, masterConnection);
		return database;
	}

	private static void configLogging() {
		try {
			final Layout layout = new PatternLayout("[%5p] [%d{dd MMM yyyy HH:mm:ss,SSS}] [%t] [%x] [%c] - %m%n");
			final ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.addAppender(consoleAppender);
			final FileAppender fileAppender = new FileAppender(layout, "batch_authors.log", false);
			logger.addAppender(fileAppender);
			logger.setLevel(Level.INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * returns the first name of an author
	 * 
	 * @param s
	 * @return first name
	 */
	private static String getFirstName(String firstName) {
		if (!present(firstName)) {
			return "";
		}
		String[] firstNames = firstName.split(" ");
		if (firstNames.length >= 1) {
			return firstNames[0].trim();
		}
		return "";
	}
	
	
	/**
	 * returns the middle part(s) of an author
	 * 
	 * @param s
	 * @return middle name
	 */
	private static String getMiddleName(String firstName) {
		if (!present(firstName)) {
			return "";
		}
		String[] firstNames = firstName.split(" ");
		if (firstNames.length >= 2) {
			String mn = "";
			for (int i = 1; i < (firstNames.length); ++i) {
				mn +=  firstNames[i].trim();
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
	private static Map<String, Author> fetchBibtexAuthors(AuthorDB db, final long lastId, final long currentContentId) throws SQLException {
		final ResultSet rs = db.getBibtexAuthors(lastId, currentContentId);
		// memory consumption here: 280 MB (result set)
		
		// last content Id fetched
		long lastContentId = 0;
		
		final Map<String, Author> bibtexAuthorMap = new HashMap<String, Author>(1500000);
		
		int counter = 0;
		while (rs.next()) {
			// clear names
			String authorsAndEditors = "";
			
			final String authorsString = rs.getString(1);
			if (authorsString != null) {
				authorsAndEditors += authorsString;
			}
			final String editorString = rs.getString(3);
			if (editorString != null) {
				authorsAndEditors += " and " + editorString;
			}
			if (!present(authorsAndEditors)) {
				continue;
			}
			counter++;
			
			// split author field
			final List<PersonName> authors = PersonNameUtils.discoverPersonNamesIgnoreExceptions(authorsAndEditors);
			if (!present(authors)) {
				logger.error("no author found for '" + authorsAndEditors + "'");
				continue;
			}
			// get content ID from result
			lastContentId = rs.getLong(2);
						
			// loop over all authors
			for (final PersonName bibtexAuthor : authors) {
				String authorString = bibtexAuthor.getLastName();
				if (present(bibtexAuthor.getFirstName())) {
					authorString += ", " + bibtexAuthor.getFirstName();
				}
				if (authorString.length() > 2) {
					if (!bibtexAuthorMap.containsKey(authorString)) {
						// we split the author name already here - if we do it in the
						// getFirstName... - functions, we consume a lot of temporary 
						// memory
						// create new author object
						final Author author = new Author(getFirstName(bibtexAuthor.getFirstName()), getMiddleName(bibtexAuthor.getFirstName()), bibtexAuthor.getLastName(), authorString);
						// add current content ID to author object
						author.getContentIds().add(Long.valueOf(lastContentId));
						// check if author is already in map
						// not contained -> add to map 
						bibtexAuthorMap.put(authorString, author);
					} else {
						// is contained -> add content id
						final Author author = bibtexAuthorMap.get(authorString);
						author.getContentIds().add(Long.valueOf(lastContentId));
						// check if author already got the requested bibtex representation. if not, add the current one
						if (!author.getBibtexNames().contains(authorString)) {
							author.addBibtexName(authorString);
						}
					}
				}
			}
			
			if (counter % 10000 == 0) {
				logger.info("nr. of bibtex authors: " + bibtexAuthorMap.size());
				long memUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB");
				logger.info("Calling GC...");
				System.gc();
			}
		}
		
		// clean memory
		rs.close();
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
		final ResultSet rs = db.getAuthors();
		final Map<String, Author> authorMap = new HashMap<String, Author>();
		int counter = 0;
		
		while (rs.next()) {
			final String bibtexName = rs.getString(2);
			if (bibtexName == null) continue;
			
			counter++;
			final String authorName = TexDecode.decode(bibtexName);
			
			if (!authorMap.containsKey(authorName)) {
				final List<PersonName> personNames = PersonNameUtils.discoverPersonNamesIgnoreExceptions(authorName);
				if (present(personNames)) {
					final PersonName personName = personNames.get(0);
					final Author author = new Author(getFirstName(personName.getFirstName()), getMiddleName(personName.getFirstName()), personName.getLastName(), bibtexName);
					author.getContentIds().add(Long.valueOf(rs.getLong(3)));
					author.setAuthorId(rs.getLong(1));
					authorMap.put(authorName, author);
				}
				
			} else {
				final Author existingAuthor = authorMap.get(authorName);
				existingAuthor.getContentIds().add(Long.valueOf(rs.getLong(3)));
				// check if author already got the requested bibtex representation. if not, add the current one
				if (!existingAuthor.getBibtexNames().contains(bibtexName)) {
					existingAuthor.addBibtexName(bibtexName);
				}
			}
			
			if (counter % 10000 == 0) {
				logger.info("nr. of authors: " + authorMap.size());
				long memUsed = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024;
				logger.info("memory used: " + memUsed + " KB"); 
				logger.info("Calling GC...");
				System.gc();
			}
		}
		
		// clean memory
		rs.close();
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
	private static void blastData(AuthorDB db, long lastId, Map<String, Author> bibtexAuthorMap, Map<String, Author> authorMap) throws SQLException {
		final List<Author> insertAuthors = new ArrayList<Author>();
		final List<Author> updateAuthors = new ArrayList<Author>();
		
		logger.info("Computing which authors to insert / update...");
		for (final String s : bibtexAuthorMap.keySet()) {
			final Author author = bibtexAuthorMap.get(s);
			
			if (authorMap.containsKey(s)) {
				// set author id
				author.setAuthorId(authorMap.get(s).getAuthorId());
				int ctr = authorMap.get(s).getContentIds().size();
				
				// if there isn't an old id in the update user, add it to delete list
				if (lastId == 0) {
					for (long l : authorMap.get(s).getContentIds()) {
						final Long boxedValue = Long.valueOf(l);
						if (!author.getContentIds().contains(boxedValue)) {
							author.getDeletedContentIds().add(boxedValue);
						}
					}
				}
				
				// remove existing id's for update author
				Iterator<Long> iter = author.getContentIds().iterator();
				while (iter.hasNext()) {
					if (authorMap.get(s).getContentIds().contains(iter.next())) {
						iter.remove();
					}
				}
				
				author.setCtr(ctr + author.getContentIds().size() - author.getDeletedContentIds().size());
				
				final Iterator<String> nameIter = author.getBibtexNames().iterator();
				while (nameIter.hasNext()) {
					if (authorMap.get(s).getBibtexNames().contains(nameIter.next())) {
						nameIter.remove();
					}
				}
				
				// remember author to update
				if (author.getContentIds().size() > 0 
						|| author.getBibtexNames().size() > 0 
						|| author.getDeletedContentIds().size() > 0) {
					updateAuthors.add(author);
				}
				
			} else {
				insertAuthors.add(author);
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
	private static long getLastId() {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(LAST_ID_FILE));
			return Long.parseLong(prop.getProperty("last_id"));
		} catch (IOException ex) {
			logger.error(ex);
		}
		return 0;
	}
	
	
	/**
	 * stores the last id in a property file
	 */
	private static void saveLastId(final long lastId) {
		try {
			final Properties prop = new Properties();
			prop.setProperty("last_id", String.valueOf(lastId));
			prop.store(new FileOutputStream(LAST_ID_FILE), null);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}
	
	
	/**
	 * returns the "TMP" path
	 * 
	 * @return /path/to/tmp ie /tmp
	 */
	private static String getTmpPath() {
		final String tmpPath = System.getenv().get("TMP");
		if (tmpPath != null) {
			return tmpPath + "/";
		}
		
		return TMP_PATH;
	}
	
	
	/**
	 * tests, if there exists a pid file in the tmp-space
	 * 
	 * @return boolean : true, if process is running
	 */
	private static boolean amIRunning() {
		final File pid = new File(getTmpPath() + PID_FILE);
		if (pid.exists()) {
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
		final File pid = new File(getTmpPath() + PID_FILE); 
		
		if(pid.delete()) {
			return true;
		}
		
		return false;
	}
}