package org.bibsonomy.testutil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.StatementType;
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
			final String wholeLine = spanningLineBuf.toString();
			if (wholeLine.endsWith(";") == false) continue;
			log.debug("Read: " + wholeLine);
			this.statements.add(wholeLine);
			spanningLineBuf.delete(0, spanningLineBuf.length());
		}
	}

	/**
	 * Executes all statements from the SQL script.
	 * 
	 * @param session
	 *            database session
	 */
	public void load(final DBSession session) {
		session.beginTransaction();
		for (final String statement : this.statements) {
			session.transactionWrapper(statement, null, StatementType.INSERT, null, false);
		}
		session.commitTransaction();
		session.endTransaction();
	}
}