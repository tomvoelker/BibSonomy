package org.bibsonomy.lucene.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.mysql.jdbc.ResultSet; //import java.sql.ResultSet;
import com.mysql.jdbc.Statement;

//import java.sql.Statement;

//import folk2tax.util.SchemaIdentifier;

/**
 * little util to access database
 * 
 * @author Stefan Stuetzer, Sven Stefani
 */
public class DBTool {

	/** the logger */
	Logger log = LogManager.getLogger(DBTool.class);

	/** MySQL database driver */
	final String DRIVER_NAME = "com.mysql.jdbc.Driver";
	final String DRIVER_PREFIX = "jdbc:mysql://";

	private String host="";		//  = "Rechnername";
	private String user="";		// = "DeinNutzername";
	private String passwd="";	// = "DeinPasswort";
	private String database="";	// = "DeineDatenbank";
	private int port = 3306;
	
	
	/** the database connection */
	private Connection conn;

	/**
	 * @return a DB connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	public Connection getDBConnection()
			throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER_NAME);
		conn = DriverManager.getConnection(DRIVER_PREFIX + host + ":" + port
				+ "/" + database, user, passwd);
		return conn;
	}

	/**
	 * @return a DB connection with the specified connection data
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getDBConnection(String host, String userName,
			String password) throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER_NAME);
		conn = DriverManager.getConnection(DRIVER_PREFIX + host, userName,
				password);
		return conn;
	}

	/**
	 * @return a DB connection with the specified connection data
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getDBConnection(String host,
			int port, String userName, String password)
			throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER_NAME);
		conn = DriverManager.getConnection(DRIVER_PREFIX + host + ":" + port
				+ "/" + database, userName, password);
		return conn;
	}

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
		Statement stmt;
		try {
			stmt = (Statement) conn.createStatement();
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
			Statement stmt = (Statement) conn.createStatement();
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
	public ResultSet rawQuery(final String query, final Connection conn, boolean streamed) {
		try {
			Statement stmt = this.getStatement(streamed);
			ResultSet rset = (ResultSet) stmt.executeQuery(query);
			// System.out.println(query);
			return rset;
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
			System.out.println("SQL Query: " + query);
		}
		return null;
	}
	
	public ResultSet rawQuery(final String query, final Connection conn) {
		return this.rawQuery(query, conn, false);
	}

	public Statement getStatement(boolean streamed) {
		Statement stmt = null;
		try {
			if (!streamed) {
				stmt = (Statement) conn.createStatement();
			} else {
				stmt = (Statement) conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
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
			Statement stmt = (Statement) conn.createStatement();
			return stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
			System.out.println("SQL Query: " + query);
			return null;
		}
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}



}
