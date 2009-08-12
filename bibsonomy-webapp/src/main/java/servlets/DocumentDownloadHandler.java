/*
 * This class is used by processBook.jsp to insert the  
 * bookmark data retrieved from BookBean into the bibsonomy database.
 * If transaction is successful the user gets forwarded back to 
 * UserSiteAfterLogin.
 */
package servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
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
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;

import beans.UserBean;
import filters.SessionSettingsFilter;

public class DocumentDownloadHandler extends HttpServlet{ 
	private static final Log log = LogFactory.getLog(DocumentDownloadHandler.class);
	
	private static final long serialVersionUID = 3839748679655351876L;
	private DataSource dataSource;
	private String documentPath = null;
	private static boolean publicDocuments = false;

	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
		try {
			dataSource   = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
			documentPath = getServletContext().getInitParameter("rootPath") + "bibsonomy_docs/";
			publicDocuments = "true".equals(getServletContext().getInitParameter("publicDocuments")); 
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doPost(request,response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login");
			return;
		}

		/* Establish all connections, result sets and statements */
		Connection conn    		= null;
		ResultSet rst 			= null;
		PreparedStatement stmtP = null;

		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}

			String hash   = request.getParameter("requHash");
			String action = request.getParameter("action");
			File document = new File(documentPath + hash.substring(0,2) + "/" + hash);

			if ("delete".equals(action)) {
				/* 
				 * delete entry in database
				 */
				stmtP = conn.prepareStatement("DELETE FROM document WHERE user_name = ? AND hash = ?");
				stmtP.setString(1, currUser);
				stmtP.setString(2, hash);
				if (stmtP.executeUpdate() > 0) {
					/*
					 * delete file from filesystem
					 */
					document.delete();
				}
				response.sendRedirect(request.getHeader("referer"));
				
			} else {
				/*
				 * download document
				 */
				if (publicDocuments) {
					stmtP = conn.prepareStatement("SELECT name FROM document WHERE hash = ?");
				} else {
					stmtP = conn.prepareStatement("	SELECT name FROM document d " +					// own documents
												  "	WHERE hash = ? AND user_name = ? " +
												  "	UNION " +										// public and viewable docs of group members
												  " SELECT name FROM document d JOIN bibtex b USING (content_id) " + 
												  "	WHERE hash = ? " +
												  " AND b.group IN " + // only groups that are allowed to share documents
												  " (SELECT 0 UNION SELECT g.group FROM groups g JOIN groupids i ON (g.group = i.group) WHERE user_name = ? AND i.sharedDocuments = 1) " + 
												  " AND d.user_name IN " +
												  " ( " +
												  " 	SELECT DISTINCT user_name " +
												  "		FROM groups g " + 
												  "		WHERE g.group IN " +
												  "		( " +
												  "			SELECT g.group " +
												  "			FROM groups g " +
												  "			WHERE user_name = ? " +
												  "		)" +	 
												  " )");
					
					stmtP.setString(2, currUser);
					stmtP.setString(3, hash);
					stmtP.setString(4, currUser);
					stmtP.setString(5, currUser);					
				}
				stmtP.setString(1, hash);
				rst = stmtP.executeQuery();
				if (rst.next()) {
					// we got a filename for this user+hashcombination from the database --> get document
					String filename = rst.getString("name");

					// set response
					response.setHeader("Content-Disposition","inline; filename*='utf-8'" + URLEncoder.encode(filename, "UTF-8"));
					response.setContentType(getContentType(filename));
					response.setContentLength((int) document.length());

					// do streaming
					ServletOutputStream output = response.getOutputStream();
					BufferedInputStream buf = null;	
					try {
						buf = new BufferedInputStream(new FileInputStream(document));
						int readBytes = 0;
						// read from the file; write to the ServletOutputStream
						while ((readBytes = buf.read()) != -1) output.write(readBytes);
					} catch (IOException ioe) {
						throw new ServletException(ioe.getMessage());
					} finally {
						if (output != null) output.close();
						if (buf    != null)	buf.close();
					}

				}
				else {
					// no document found - there doesn't exist one, or the requesting user does not have the 
					// rights to see it
					String error_msg = "The document you requested does either not exist, or you are not authorized to " +
									   "download it.";
					request.setAttribute("error", "error_msg");
					getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
					return;					
				}
			}


		} catch (SQLException e) {
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} finally {
			if(rst  != null) {try {rst.close();  } catch (SQLException e) { } rst   = null;}
			if(stmtP!= null) {try {stmtP.close();} catch (SQLException e) { } stmtP = null;}
			if(conn != null) {try {conn.close(); } catch (SQLException e) {	}conn  = null;}
		}
	}
	
	/** Depending on the extension of the file, returns the correct MIME content type.
	 * NOTE: the method looks only at the name of the file not at the content!
	 *  
	 * @param filename - name of the file.
	 * @return - the MIME content type of the file.
	 */
	private String getContentType (String filename) {
		if (StringUtils.matchExtension(filename, "ps")) {
			return "application/postscript";
		} else if (StringUtils.matchExtension(filename, "pdf")) {
			return "application/pdf";
		} else if (StringUtils.matchExtension(filename, "txt")) {
			return "text/plain";			
		} else if (StringUtils.matchExtension(filename, "djv", "djvu")) {
			return "image/vnd.djvu";
		} else {
			return "application/octet-stream";
		}
	}
}