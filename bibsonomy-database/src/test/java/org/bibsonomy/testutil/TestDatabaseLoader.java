package org.bibsonomy.testutil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
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

	private final static Logger log = Logger.getLogger(TestDatabaseLoader.class);
	/** Holds the SQL script */
	private final String scriptFilename = "database/tables-and-test-data.sql";
	/** Stores the SQL statements from the script */
	private final List<String> statements;

	/**
	 * Loads the SQL statements from the script.
	 */
	public TestDatabaseLoader() {
		final InputStream scriptStream = TestDatabaseLoader.class.getClassLoader().getResourceAsStream(this.scriptFilename);
		if (scriptStream == null) throw new RuntimeException("Can't get SQL script '" + this.scriptFilename + "'");

		this.statements = new ArrayList<String>();

		/*
		 * We read every single line and skip it if it's empty or a comment
		 * (starting with '--'). If the current line doesn't end with a ';'
		 * we'll append the next line to it until we get a ';' - this way
		 * statements can span multiple lines in the SQL script and will be read
		 * as if they were on a single line.
		 */
		final Scanner scan = new Scanner(scriptStream);
		final StringBuilder spanningLineBuf = new StringBuilder();
		while (scan.hasNext()) {
			final String currentLine = scan.nextLine();
			if ("".equals(currentLine.trim())) continue;
			if (currentLine.startsWith("--")) continue;

			spanningLineBuf.append(" " + currentLine);
			final String wholeLine = spanningLineBuf.toString().trim();
			if (wholeLine.endsWith(";") == false) continue;
			log.debug("Read: " + wholeLine);
			this.statements.add(wholeLine);
			spanningLineBuf.delete(0, spanningLineBuf.length());
		}
	}

	/**
	 * Executes all statements from the SQL script.
	 */
	public void load() {
		try {
			final SimpleJDBCHelper jdbc = new SimpleJDBCHelper();
			for (final String statement : this.statements) {
				jdbc.execute(statement);
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
	private final String configFile = "database.properties";
	private Connection connection;

	/**
	 * Holds the config for the database.
	 */
	private interface DatabaseConfig {
		/**
		 * @return url
		 */
		public String getUrl();

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
	 */
	public SimpleJDBCHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final DatabaseConfig cfg = this.getConfig();
			this.connection = DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(), cfg.getPassword());
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private DatabaseConfig getConfig() {
		final InputStream scriptStream = SimpleJDBCHelper.class.getClassLoader().getResourceAsStream(this.configFile);
		if (scriptStream == null) throw new RuntimeException("Can't get config file '" + this.configFile + "'");

		final Map<String, String> params = new HashMap<String, String>();
		final Scanner scan = new Scanner(scriptStream);
		while (scan.hasNext()) {
			final String currentLine = scan.nextLine();
			for (final String param : new String[] { "url", "username", "password" }) {
				if (currentLine.startsWith(param) == false) continue;
				params.put(param, currentLine.substring(currentLine.indexOf('=') + 1).trim());
			}
		}
		if (params.size() != 3) throw new RuntimeException("Couldn't read config file '" + this.configFile + "'");

		return new DatabaseConfig() {
			public String getUrl() {
				return params.get("url");
			}

			public String getUsername() {
				return params.get("username");
			}

			public String getPassword() {
				return params.get("password");
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
}