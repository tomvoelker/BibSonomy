package org.bibsonomy.testutil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.testutil.JNDIBinder;
import org.junit.Ignore;

/**
 * This class loads the SQL script for the test database. This should be
 * executed right before the start of all database tests.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class TestDatabaseLoader {
	private final static Log log = LogFactory.getLog(TestDatabaseLoader.class);
	
	/** Holds the database schema (script is at /src/main/resources) */
	private static final String SCHEMA_FILENAME = "bibsonomy-db-schema.sql";
	/** Holds the test data (script is found at /src/test/resources) */
	private static final String DATA_FILENAME   = "database/insert-test-data.sql";
	/** Holds the commands to empty all tables (script is found at /src/test/resources) */
	private static final String DELETE_FILENAME   = "database/empty-tables.sql";
	
	private static final TestDatabaseLoader INSTANCE = new TestDatabaseLoader();

	/**
	 * @return the @{link:TestDatabaseLoader} instance
	 */
	public static TestDatabaseLoader getInstance() {
		return INSTANCE;
	}
	
	/** Stores the create table statements  */
	private final List<String> createStatements;
	/** Stores the insert statements  */
	private final List<String> insertStatements;
	/** Stores the delete statements  */
	private final List<String> deleteStatements;	

	/**
	 * Loads the SQL statements from the script.
	 */
	private TestDatabaseLoader() {
		// parse all sql scripts
		long start = System.currentTimeMillis();
		log.debug("parsing create statements");
		this.createStatements = this.parseInputStream(DBLogic.class.getClassLoader().getResourceAsStream(SCHEMA_FILENAME));
		
		log.debug("parsing insert statements");
		this.insertStatements = this.parseInputStream(TestDatabaseLoader.class.getClassLoader().getResourceAsStream(DATA_FILENAME));
		
		log.debug("parsing delete statements");
		this.deleteStatements = this.parseInputStream(TestDatabaseLoader.class.getClassLoader().getResourceAsStream(DELETE_FILENAME));
		long elapsed = (System.currentTimeMillis() - start ) / 1000;
		log.debug("Done; took " + elapsed + " seconds.");
	}
	
	
	/**
	 * Parse input stream of an sql file into an array of sql commands
	 * 
	 * @param scriptStream
	 * @return
	 */
	private List<String> parseInputStream(InputStream scriptStream) {
		final List<String> statements = new ArrayList<String>();
		if (scriptStream == null) throw new RuntimeException("Can't get SQL script.");
				
		/*
		 * We read every single line and skip it if it's empty or a comment
		 * (starting with '--'). If the current line doesn't end with a ';'
		 * we'll append the next line to it until we get a ';' - this way
		 * statements can span multiple lines in the SQL script and will be read
		 * as if they were on a single line.
		 * 
		 * Furthermore, re skip trigger-related lines, as we don't need them in
		 * our test database
		 */
		final Scanner scan = new Scanner(scriptStream);
		final StringBuilder spanningLineBuf = new StringBuilder();
		while (scan.hasNext()) {
			final String currentLine = scan.nextLine();
			if ("".equals(currentLine.trim())) continue;       // skip empty lines
			if (currentLine.startsWith("--")) continue;        // skip comments				
			if (currentLine.startsWith("DELIMITER")) continue; // exclude trigger-related statements
			if (currentLine.startsWith("/*!50003")) continue;  
			

			spanningLineBuf.append(" " + currentLine);
			final String wholeLine = spanningLineBuf.toString().trim();
			if (!wholeLine.endsWith(";")) continue;
			log.debug("Read: " + wholeLine);
			statements.add(wholeLine);
			spanningLineBuf.delete(0, spanningLineBuf.length());
		}		
		return statements;
	}

	/**
	 * Executes all statements from the SQL scripts.
	 */
	public void load() {
		long start, elapsed;
		try {
			log.debug("Starting to load test database.");
			final SimpleJDBCHelper jdbc = new SimpleJDBCHelper();
			/*
			 * do initialization: drop database, create it, use it
			 */
			final String database = jdbc.getDatabaseConfig().getDatabase();
			if (jdbc.getDatabaseConfig().createDatabaseBeforeLoading()) {
				log.debug("Starting to drop + create database" + database);
				start = System.currentTimeMillis();
				jdbc.execute("DROP DATABASE IF EXISTS `" + database + "`;");
				jdbc.execute("CREATE DATABASE `" + database + "`;");
				elapsed = (System.currentTimeMillis() - start ) / 1000;
				log.debug("Done; took " + elapsed + " seconds.");
			}
	
			log.debug("Switch to database " + database);
			start = System.currentTimeMillis();
			jdbc.execute("USE `" + database + "`;");
			elapsed = (System.currentTimeMillis() - start ) / 1000;
			log.debug("Done; took " + elapsed + " seconds.");
			/*
			 * execute statements from script
			 */
			final List<String> statements = new ArrayList<String>();
			if (jdbc.getDatabaseConfig().createDatabaseBeforeLoading()) {
				statements.addAll(this.createStatements);
			} else {
				statements.addAll(this.deleteStatements);
			}
			statements.addAll(this.insertStatements);

			for (final String statement : statements) {
				start = System.currentTimeMillis();
				log.debug("executing SQL statement: " + statement);
				jdbc.execute(statement);
				elapsed = (System.currentTimeMillis() - start ) / 1000;
				log.debug("Done; took " + elapsed + " seconds.");				
			}			
			
			jdbc.close();
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}

/**
 * Very simple JDBC abstraction.
 * @author Christian Schenk
 */
final class SimpleJDBCHelper implements Closeable {
	private final String configFile = "bibsonomy_database.properties";
	private Connection connection;
	private final DatabaseConfig cfg;
	
	/**
	 * Holds the config for the database.
	 */
	public static interface DatabaseConfig {
		/**
		 * @return url
		 */
		public String getUrl();

		/**
		 * @return database name
		 */
		public String getDatabase();
		
		/**
		 * @return username
		 */
		public String getUsername();

		/**
		 * @return password
		 */
		public String getPassword();
		/**
		 * @return whether to create DB before loading the data 
		 */		
		public boolean createDatabaseBeforeLoading();
	}

	/**
	 * Loads the MySQL JDBC driver and sets up a connection.
	 */
	public SimpleJDBCHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.cfg = this.getConfig();
			this.connection = DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(), cfg.getPassword());
			
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * get datase configuration
	 * 
	 * @return DatabaseConfig - the database configuration
	 */
	private DatabaseConfig getConfig() {
	    final Properties prop = new Properties();
	    try {
	        prop.load(SimpleJDBCHelper.class.getClassLoader().getResourceAsStream(this.configFile));
	    } catch (IOException e) {
	    	throw new RuntimeException("Can't get config file '" + this.configFile + "'");
	    }		
	    // six properties need to be set
	    if (prop.keySet().size() != 6) {
	    	throw new RuntimeException("Error while reading config file '" + this.configFile + "'; expected 6 values, found " + prop.keySet().size()); 
	    }

		return new DatabaseConfig() {
			public String getUrl() {
				return JNDIBinder.JDBC_URL_START + prop.getProperty(JNDIBinder.HOST_KEY) + prop.getProperty(JNDIBinder.OPTIONS_KEY);
			}
			
			/** Extracts the name of the database from the URL. 
			 * The name is the string between the last "/" and before
			 * the first "?".
			 * 
			 * @return The name of the database.
			 */
			public String getDatabase() {
				return prop.getProperty(JNDIBinder.DATABASE_KEY);
			}

			public String getUsername() {
				return prop.getProperty(JNDIBinder.USERNAME_KEY);
			}

			public String getPassword() {
				return prop.getProperty(JNDIBinder.PASSWORD_KEY);
			}
			
			public boolean createDatabaseBeforeLoading() {
				return prop.getProperty("createDatabaseBeforeLoading", "true").equals("true");
			}
		};
	}

	/**
	 * Executes the given SQL.
	 * 
	 * @param sql
	 */
	public void execute(final String sql) {
		try {
			
			final Statement stmt = this.connection.createStatement();
			stmt.execute(sql);
			stmt.close();
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() throws IOException {
		if (this.connection == null) return;
		try {
			if (this.connection.isClosed()) return;
			this.connection.close();
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public DatabaseConfig getDatabaseConfig() {
		return this.cfg;
	}
}