/*
 * This class is used by geotagging_entry.jsp to insert the  
 * picture. Image files are not available from outside that is
 * why this class provides a stream of a requested image.
 */
package servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import beans.UserBean;
import filters.SessionSettingsFilter;

public class PictureDownloadHandler extends HttpServlet{ 

	private static final long serialVersionUID = 3829448698945551876L;
	private DataSource dataSource;

	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} 
		catch (NamingException ex){
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doPost(request,response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* Get the session attribute of current user  */
		HttpSession session = request.getSession(true);

		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			// TODO: does this work on bibsonomy.org? I guess, /bibsonomy/ is added, because
			// the servlet API spec says something about that
			response.sendRedirect("/login");
			return;
		}


		/* Establish all connections, result sets and statements */
		Connection conn    		= null;
		ResultSet rst 			= null;
		PreparedStatement stmtP = null;
		/* Because the dataSource instance variable is potentially 
		 * shared across multiple threads, access to the variable 
		 * must be from within a synchronized block. */
		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}

			//extract owner and filename from url and check if this file exists
			String filehash = request.getParameter("requHash");
			stmtP = conn.prepareStatement("SELECT name FROM document WHERE user_name = ? AND hash = ?");
			stmtP.setString(1, request.getParameter("requUser"));
			stmtP.setString(2, filehash);
			rst = stmtP.executeQuery();
			if (rst.next()) {
				// we got a filename for this user+hashcombination from the database --> get document
				String rootPath = session.getServletContext().getInitParameter("rootPath");
				File document   = new File(rootPath + "bibsonomy_docs/" + filehash.substring(0,2) + "/" + filehash);
				// set response
				response.setContentType("image/jpeg");
				response.setContentLength((int) document.length());

				// do streaming
				ServletOutputStream output = response.getOutputStream();
				FileInputStream input = null;
				BufferedInputStream buf = null;	
				try {
					input = new FileInputStream(document);
					buf   = new BufferedInputStream(input);
					int readBytes = 0;
					// read from the file; write to the ServletOutputStream
					while ((readBytes = buf.read()) != -1) output.write(readBytes);
				} catch (IOException ioe) {
					throw new ServletException(ioe.getMessage());
				} finally {
					if (output != null) output.close();
					if (buf    != null)	buf.close();
					if (input  != null) input.close();
				}

			}


		} catch (SQLException e) {
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} 
		finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if(rst  != null) {try {rst.close();  } catch (SQLException e) { } rst   = null;}
			if(stmtP!= null) {try {stmtP.close();} catch (SQLException e) { } stmtP = null;}
			if(conn != null) {try {conn.close(); } catch (SQLException e) {	}conn  = null;}
		}
	}	
}