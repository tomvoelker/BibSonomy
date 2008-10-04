/*
 * This class is used by processBook.jsp to insert the  
 * bookmark data retrieved from BookBean into the bibsonomy database.
 * If transaction is successful the user gets forwarded back to 
 * UserSiteAfterLogin.
 */
package servlets;

import helpers.MultiPartRequestParser;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.bibsonomy.util.HashUtils;

import resources.Bibtex;
import resources.Resource;
import beans.UploadBean;
import beans.UserBean;
import filters.SessionSettingsFilter;


public class DocumentUploadHandler extends HttpServlet{ 

	private static final Logger log = Logger.getLogger(DocumentUploadHandler.class);
	private static final long serialVersionUID = 3839748679655351876L;
	private DataSource dataSource;
	private String rootPath = null;

	private static final String SQL_TEST_UPLOAD                 = "SELECT d.content_id FROM document d, bibtex b WHERE b.simhash" + Bibtex.INTRA_HASH + " = ? AND b.user_name = ? AND d.content_id=b.content_id";
	private static final String SQL_INSERT_DOCUMENT             = "INSERT INTO document (hash, content_id, name, user_name, date, md5hash) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SQL_GET_CONTENT_ID_FOR_BIBTEX   = "SELECT content_id FROM bibtex WHERE simhash" + Bibtex.INTRA_HASH + " = ? AND user_name = ?";  
	// TODO: get_content_id statement stolen from DBContentManager --> should be done by a DB manager


	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
			rootPath = getServletContext().getInitParameter("rootPath");
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		/*
		 * this servlet accepts only POST requests
		 */
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
			/*
			 * Use MultiPartRequestParser to retrieve all fields 
			 * from uploaded form. You'll get Map of 
			 * Hier den MultiPartRequestParser(request) aufrufen, damit 
			 * man eine Map zurück erhält, die FileItems enthält!
			 * Map<String, FileItem> fieldMap = MultiPartRequestParser.getFields(request,rootPath);
			 */			
			MultiPartRequestParser parser = new MultiPartRequestParser(); 
			Map<String, FileItem> fieldMap = parser.getFields(request, rootPath);

			// retrieve form field "file"
			FileItem upFile = (FileItem) fieldMap.get("file");
			String fileName = upFile.getName();

			/*
			 * check file extensions which we accept
			 */
			if (fileName.equals("") || !matchExtension(fileName, "pdf", "ps", "djv", "djvu", "txt")) {
				throw new Exception ("Please check your file. Only PDF, PS, TXT or DJVU files are accepted.");
			}

			// format date
			Date currDate = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df.setTimeZone(TimeZone.getDefault());
			String currDateFormatted = df.format(currDate);
			String redirectUrl = "/errors/upload_error.jsp";

			// retrieve form field "hash" if doc (pdf, ps) was uploaded, otherwise we don`t need one			
			String hash = ((FileItem) fieldMap.get("hash")).getString();

			// test for some errors (duplicate, wrong file) 
			stmtP = conn.prepareStatement(SQL_TEST_UPLOAD);
			stmtP.setString(1, hash);
			stmtP.setString(2, currUser);
			rst = stmtP.executeQuery();
			if (rst.next()) {
				// user already uploaded a file --> not allowed to overwrite
				throw new Exception ("For this entry a file already exists.");
			}

			// create hash for filename
			String hashedName = Resource.hash(fileName + currUser + currDateFormatted);

			// check, if content_id exists for current hash
			stmtP = conn.prepareStatement(SQL_GET_CONTENT_ID_FOR_BIBTEX);
			stmtP.setString(1, hash);
			stmtP.setString(2, currUser);
			rst = stmtP.executeQuery();
			if (rst.next()) {
				/* *************************************************
				 * save file and insert data into database
				 * *************************************************/

				// write file; build path from first two letters of file name hash
				upFile.write(new File((rootPath + "bibsonomy_docs/" + hashedName.substring(0, 2).toLowerCase()), hashedName)); // if it fails, Exception is catched below 				

				// save document details in database
				stmtP = conn.prepareStatement(SQL_INSERT_DOCUMENT);
				stmtP.setString(1, hashedName);
				stmtP.setInt(2, rst.getInt("content_id"));
				stmtP.setString(3, fileName);
				stmtP.setString(4, currUser);
				stmtP.setTimestamp(5, new Timestamp(currDate.getTime()));
				stmtP.setString(6, HashUtils.getMD5Hash(upFile.get()));
				stmtP.executeUpdate();

				redirectUrl = "/bibtex/" + Bibtex.INTRA_HASH + hash + "/" + URLEncoder.encode(currUser, "UTF-8");
			}
			// writing into file was ok -> delete fileitem upfile
			upFile.delete();

			response.sendRedirect(redirectUrl);

		} catch (SQLException e) {
			log.fatal(e);
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} catch (Exception e) {
			// if it failed, send user to error page
			UploadBean bean = new UploadBean();
			bean.setErrors("file", "Your upload failed: " + e);
			request.setAttribute("upBean", bean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);			
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst   != null) {try {rst.close();   } catch (SQLException e) {} rst   = null;}
			if (stmtP != null) {try {stmtP.close(); } catch (SQLException e) {} stmtP = null;}
			if (conn  != null) {try {conn.close();  } catch (SQLException e) {} conn  = null;}
		}
	}

	/**
	 * Checks if a given string has one of the chosen extensions.
	 * @param s String to test
	 * @param extensions Extensions to match.
	 * @return true if String matches with extension 
	 */
	public static boolean matchExtension(String s, String... extensions){
		for (String ext : extensions) {
			if (s.length() >= ext.length() && s.substring(s.length() - ext.length(), s.length()).equalsIgnoreCase(ext))
				return true;
		}  
		return false;
	}
}