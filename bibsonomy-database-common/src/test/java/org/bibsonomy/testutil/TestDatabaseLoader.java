/**
 * BibSonomy-Database-Common - Helper classes for database interaction
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.testutil;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;

/**
 * This class loads the SQL script for the test database. This should be
 * executed right before the start of all database tests.
 * 
 * @author Christian Schenk
 */
@Ignore
public class TestDatabaseLoader {
	private final static Log log = LogFactory.getLog(TestDatabaseLoader.class);
	
	private boolean firstRun = true;
	
	/** Stores the create table statements  */
	private final List<String> createStatements;
	/** Stores the insert statements  */
	private final List<String> insertStatements;
	
	private final List<String> tableNames;
	
	/**
	 * loads the sql schema from the script
	 * @param schemaFile
	 */
	public TestDatabaseLoader(final String schemaFile) {
		this(schemaFile, (String[]) null);
	}

	/**
	 * Loads the SQL statements from the script.
	 * @param schemaFile 
	 * @param dataFiles 
	 */
	public TestDatabaseLoader(final String schemaFile, final String ... dataFiles) {
		// parse all sql scripts
		final long start = System.currentTimeMillis();
		log.debug("parsing create statements");
		this.tableNames = new LinkedList<String>();
		this.createStatements = this.parseInputStream(TestDatabaseLoader.class.getClassLoader().getResourceAsStream(schemaFile));
		
		log.debug("parsing insert statements");
		this.insertStatements = new LinkedList<String>();
		if (present(dataFiles)) {
			for (String dataFile : dataFiles) {
				this.insertStatements.addAll(this.parseInputStream(TestDatabaseLoader.class.getClassLoader().getResourceAsStream(dataFile)));
			}
		}
		
		final long elapsed = (System.currentTimeMillis() - start ) / 1000;
		log.debug("Done; took " + elapsed + " seconds.");
	}
	
	
	/**
	 * Parse input stream of an sql file into an array of sql commands
	 * 
	 * @param scriptStream
	 * @return
	 */
	private List<String> parseInputStream(final InputStream scriptStream) {
		final List<String> statements = new LinkedList<String>();
		if (scriptStream == null) {
			throw new RuntimeException("Can't get SQL script.");
		}
		
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
			if ("".equals(currentLine.trim())) {
				continue;       // skip empty lines
			}
			if (currentLine.startsWith("--")) {
				continue;        // skip comments
			}
			if (currentLine.startsWith("DELIMITER")) {
				continue; // exclude trigger-related statements
			}
			if (currentLine.startsWith("/*!50003")) {
				continue;
			}  
			if (currentLine.startsWith("CREATE TABLE")) {
				final String[] split = currentLine.split("`");
				if (split.length != 3) {
					log.error(currentLine);
				} else {

					this.tableNames.add(split[1]);
				}
			}

			spanningLineBuf.append(" " + currentLine);
			final String wholeLine = spanningLineBuf.toString().trim();
			if (!wholeLine.endsWith(";")) {
				continue;
			}
			log.debug("Read: " + wholeLine);
			statements.add(wholeLine);
			spanningLineBuf.delete(0, spanningLineBuf.length());
		}
		scan.close();
		return statements;
	}

	/**
	 * Executes all statements from the SQL scripts.
	 * @param configFileName 
	 * @param databaseId 
	 */
	public void load(String configFileName, String databaseId) {
		long start, elapsed;
		try {
			log.debug("Starting to load test database.");
			final SimpleJDBCHelper jdbc = new SimpleJDBCHelper(configFileName, databaseId);
			
			/*
			 * mysql >= 5.5 doesn't truncate tables with foreigen keys
			 */
			jdbc.execute("SET FOREIGN_KEY_CHECKS = 0;");
			
			/*
			 * do initialization: drop database, create it, use it
			 */
			final String database = jdbc.getDatabaseConfig().getDatabase();
			
			if (this.firstRun) {
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
			if (this.firstRun) {
				final List<String> statements = new LinkedList<String>();
				statements.addAll(this.createStatements);
				
				start = System.currentTimeMillis();

				for (final String statement : statements) {
					log.debug("executing SQL statement: " + statement);
					jdbc.execute(statement);
				}
				elapsed = (System.currentTimeMillis() - start );
				log.debug("Done; took " + elapsed + " mseconds.");
			} else {
				// loop through all databases and delete contents
				start = System.currentTimeMillis();

				for (final String tableName : this.tableNames) {
					jdbc.execute("TRUNCATE " + tableName);
				}
				elapsed = (System.currentTimeMillis() - start );
				log.debug("Done; took " + elapsed + " mseconds.");
			}
			
			start = System.currentTimeMillis();
			for (final String statement : this.insertStatements) {
				jdbc.execute(statement);
			}
			elapsed = (System.currentTimeMillis() - start );
			log.debug(">>> Done; took " + elapsed + " mseconds.");
			
			/*
			 * reset to normal
			 */
			jdbc.execute("SET FOREIGN_KEY_CHECKS = 1;");
			
			jdbc.close();
			this.firstRun = false;
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
	private String configFile;
	private String databaseId;
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
	}

	/**
	 * Loads the MySQL JDBC driver and sets up a connection.
	 * @param configFile 
	 * @param databaseId 
	 */
	public SimpleJDBCHelper(final String configFile, final String databaseId) {
		this.configFile = configFile;
		this.databaseId = databaseId;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // TODO: use the settings of the properties file
			this.cfg = this.getConfig();
			this.connection = DriverManager.getConnection(this.cfg.getUrl(), this.cfg.getUsername(), this.cfg.getPassword());
			
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
		} catch (final IOException e) {
			throw new RuntimeException("Can't get config file '" + this.configFile + "'");
		}
		
		return new DatabaseConfig() {
			@Override
			public String getUrl() {
				return prop.getProperty("database." + SimpleJDBCHelper.this.databaseId + ".url");
			}
			
			/**
			 * Extracts the name of the database from the URL. 
			 * The name is the string between the last "/" and before
			 * the first "?".
			 * 
			 * @return The name of the database.
			 */
			@Override
			public String getDatabase() {
				String url = this.getUrl();
				// remove everything behind first '?'
				url = url.substring(0, url.indexOf('?'));
				// remove everything before last '/'
				return url.substring(url.lastIndexOf('/') + 1);
			}

			@Override
			public String getUsername() {
				return prop.getProperty("database." + SimpleJDBCHelper.this.databaseId + ".username");
			}

			@Override
			public String getPassword() {
				return prop.getProperty("database." + SimpleJDBCHelper.this.databaseId + ".password");
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

	@Override
	public void close() throws IOException {
		if (this.connection == null) {
			return;
		}
		try {
			if (this.connection.isClosed()) {
				return;
			}
			this.connection.close();
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public DatabaseConfig getDatabaseConfig() {
		return this.cfg;
	}
}