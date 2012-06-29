package servlets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

/**
 * @author dzo
 * @version $Id$
 */
@Deprecated
public abstract class AbstractServlet extends HttpServlet {
	protected DataSource dataSource;

	@Override
	public void init(final ServletConfig config) throws ServletException{	
		super.init(config); 
		
		try {
			final Context initContext = new InitialContext();
			final Context envContext = (Context) initContext.lookup("java:/comp/env");
			this.dataSource = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} catch (final NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}
	
	protected Connection getConnection() throws SQLException {
		synchronized(this.dataSource) {
			if (this.dataSource != null){
				return this.dataSource.getConnection();
			}
			throw new SQLException("Could not get datasources");
		}
	}

	protected void closeAll(final Connection connection, final PreparedStatement statement) {
		if (connection != null) {
			try {
				connection.close();
			} catch (final SQLException ex) {
				// ignore
			}
		}
		
		if (statement != null) {
			try {
				statement.close();
			} catch (final SQLException ex) {
				// ignore
			}
		}
	}
}
