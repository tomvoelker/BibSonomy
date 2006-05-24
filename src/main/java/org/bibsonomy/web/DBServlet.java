/*
 * Created on 17.05.2006
 */
package org.bibsonomy.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import de.innofinity.dbcmd.core.DBConnection;
import de.innofinity.dbcmd.core.Database;

public abstract class DBServlet extends HttpServlet {
	private DBConnection con;
	
	@Override
	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		con = Database.getConnection();
		try {
			super.service(req, resp);
		} finally {
			con.close();
		}
	}

	protected DBConnection getDBConnection() {
		return con;
	}
}
