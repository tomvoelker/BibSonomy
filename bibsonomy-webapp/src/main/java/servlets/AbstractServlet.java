/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
