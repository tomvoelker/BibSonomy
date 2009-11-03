package org.bibsonomy.lucene.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.DelegatingResultSet;
import org.apache.tomcat.dbcp.dbcp.DelegatingStatement;

import com.mysql.jdbc.ResultSet; //import java.sql.ResultSet;

//import java.sql.Statement;

//import folk2tax.util.SchemaIdentifier;

/**
 * little util to access database
 * 
 * @author Stefan Stuetzer, Sven Stefani
 */
public class DBToolJDNIResource {

	/** the logger */
	Logger log = LogManager.getLogger(DBToolJDNIResource.class);

	/** MySQL database driver */
	final String DRIVER_NAME = "com.mysql.jdbc.Driver";
	final String DRIVER_PREFIX = "jdbc:mysql://";
	
	/** the database connection */
	private Connection conn;

	/**
	 * @return a DB connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */


	/**
	 * releases open database connection
	 * 
	 * @param conn
	 *            the db connection
	 * @throws SQLException
	 */
	public void releaseDBConnection(Connection conn) throws SQLException {
		conn.close();
	}

	/**
	 * sets the transaction level to READ UNCOMMITED
	 */
	public void setTransactionIsolationLevel(Connection conn) {
		DelegatingStatement stmt;
		try {
			stmt = (DelegatingStatement) conn.createStatement();
			stmt
					.executeQuery("SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED");
			log.debug("set transaction level");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * checks the isolation level of a specified connection
	 * 
	 * @param conn
	 * @param ownerOfConnection
	 * @return
	 */
	public String checkIsolationLevel(Connection conn,
			String ownerOfConnection) {
		String level = "";
		try {
			DelegatingStatement stmt = (DelegatingStatement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt
					.executeQuery("SELECT @@tx_isolation");

			while (rs.next()) {
				level = ownerOfConnection + " -> " + rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return level;
	}

	/**
	 * Raw database query function
	 * 
	 * @param query
	 *            an SQL query
	 * @return ResultSet
	 * @throws SQLException
	 */
	public DelegatingResultSet rawQuery(final String query, final Connection conn, boolean streamed) {
		try {
			DelegatingStatement stmt = this.getStatement(streamed);
			DelegatingResultSet rset = (DelegatingResultSet) stmt.executeQuery(query);
			// System.out.println(query);
			return rset;
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
		return null;
	}
	
	public DelegatingResultSet rawQuery(final String query, final Connection conn) {
		return this.rawQuery(query, conn, false);
	}

	public DelegatingStatement getStatement(boolean streamed) {
		DelegatingStatement stmt = null;
		try {
			if (!streamed) {
				stmt = (DelegatingStatement) conn.createStatement();
			} else {
				stmt = (DelegatingStatement) conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
				stmt.setFetchSize(Integer.MIN_VALUE);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stmt;
	}

	
	
	/**
	 */
	public Integer rawUpdate(final String query, final Connection conn)
			throws SQLException {
		try {
			DelegatingStatement stmt = (DelegatingStatement) conn.createStatement();
			return stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
			return null;
		}
	}


	/**
	 * set connection 
	 * 
	 * @param Connection connection   
	 */
	public void setConnection(Connection connection) {
		conn = connection;
	}

	/**
	 * @return Connection connection
	 */
	public Connection getConnection() {
		return conn;
	}

}
