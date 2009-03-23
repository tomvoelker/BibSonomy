package org.bibsonomy.batch;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Does the DB stuff for the batch classes.
 * 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class MySQLHelper {

	private Connection connection;
	private Properties props;

	private static Properties getProperties(final String propertyFileName) throws IOException {
		final Properties props = new Properties();
		props.load(MySQLHelper.class.getClassLoader().getResourceAsStream(propertyFileName));
		return props;
	}

	public MySQLHelper(final String propertyFileName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		this(getProperties(propertyFileName));
	}

	public MySQLHelper(final Properties props) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		this.props = props;
		this.connection = getConnection(
				props.getProperty("db.driver", "com.mysql.jdbc.Driver"),
				props.getProperty("db.url"), 
				props.getProperty("db.user"), 
				props.getProperty("db.pass")
		);
	}
	
	/**
	 * @see Connection#commit()
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		this.connection.commit();
	}
	
	/**
	 * @see Connection#setAutoCommit(boolean)
	 * @param autoCommit
	 * @throws SQLException
	 */
	public void setAutoCommit(final boolean autoCommit) throws SQLException {
		this.connection.setAutoCommit(autoCommit);
	}

	private Connection getConnection (final String driver, final String dbURL, final String dbUser, final String dbPass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		/*
		 * connect to DB
		 */
		Class.forName(driver).newInstance ();
		final Connection conn = DriverManager.getConnection (dbURL, dbUser, dbPass);
		if (conn != null) {
			return conn;
		}
		throw new RuntimeException("Could not get connection.");
	}

	public PreparedStatement getPreparedStatement(final String propertyKey) throws SQLException {
		return connection.prepareStatement(props.getProperty(propertyKey));
	}

	public void close() {
		try {
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			connection.close();
		} catch (SQLException e) {
			// do nothing
		} finally {
			connection = null;
		}
	}
}

