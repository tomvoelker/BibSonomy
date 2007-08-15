/*
 * This class is used by GeoTagger.exe to upload geotagging data.
 * Few modifications should provide capabilities to upload image without
 * geotagging information.
 */
package servlets;

import helpers.database.DBBookmarkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import resources.Bookmark;
import resources.Resource;
import beans.UploadBean;
import beans.UserBean;
import filters.SessionSettingsFilter;


public class PictureUploadHandler extends HttpServlet{ 
	
	private static final long serialVersionUID = 3839743674625357876L;
	private DataSource dataSource;
	private static String projectHome  = null;
	
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			if (projectHome == null) projectHome = config.getServletContext().getInitParameter("projectHome");
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
			response.sendRedirect("login");
			return;
		}
		
		/* Establish all connections*/
		Connection conn    		= null;
		
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
			
			//check if a picture will be received
			if (! "image/jpeg".equals(request.getContentType())){
				throw new Exception("Sent file has to be an image(.jpg)");
			}
			
			String rootPath = session.getServletContext().getInitParameter("rootPath");
			
			//extract parameters
			String title = request.getParameter("title");
			String description = request.getParameter("description");
			String tags = request.getParameter("tags");;
			String latitude = request.getParameter("lat");
			String longitude = request.getParameter("long");
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			Date created = df.parse(request.getParameter("date"));
			int groupID = new Integer(request.getParameter("group"));
			
			//get stream from request
			ServletInputStream inputStream = request.getInputStream();
			//prepare buffer and read stream
			byte[] buffer = new byte[request.getContentLength()];
			int position = 0;
			int available = 0;
			int alreadyRead = 0;
			
			while (available <= request.getContentLength() && alreadyRead != -1){
				available = inputStream.available();
				alreadyRead = inputStream.read(buffer, position, available);
				position = position + alreadyRead;
			}
			//stream is no longer requiered
			inputStream.close();
			
			/* *************************************************
			 * create hash for filename
			 * *************************************************/
			MessageDigest md = MessageDigest.getInstance("MD5");
			String hashedName = Resource.toHexString(md.digest(buffer));
			//create URL to save
			String url = projectHome + "geotagging/" + hashedName + "/" + currUser;
			// build path from first two letters of file name hash
			String docPath = rootPath + "bibsonomy_docs/" + hashedName.substring(0, 2).toLowerCase() + "/" + hashedName;
			
			
			/* *************************************************
			 * save file and insert data into database
			 * *************************************************/
			FileOutputStream out = new FileOutputStream(new File(docPath));
			out.write(buffer, 0, buffer.length);
			out.close();
			
			//prepare DBBookmarkManager
			DBBookmarkManager bManager = new DBBookmarkManager();
			bManager.prepareStatements(conn);
			//create list of bookmarks
			LinkedList<Bookmark> bookmarks = new LinkedList<Bookmark>();
			Bookmark bookmark = new Bookmark();
			bookmark.setUser(currUser);
			bookmark.setDocHash(hashedName);
			//add latitude, longitude and system:geo to tags
			tags = tags + " lat:" + latitude + " lon:" + longitude;
			bookmark.setTags( "system:geo " + tags);
			bookmark.setExtended(description);
			bookmark.setTitle(title);
			bookmark.setUrl(url);
			bookmark.setDate(created);
			bookmark.setGroupid(groupID);
			bookmark.setToIns(true);
			bookmarks.add(bookmark);
			//force DBBookmarkManager to insert bookmark
			bManager.updateBookmarks(bookmarks, currUser, conn, true, false);
			//force DBBookmarkManager to close statements
			bManager.closeStatements();
			
			// redirect to geotagging_entry
			//this is not used yet!
			response.sendRedirect("/geotagging/" + hashedName + "/" + URLEncoder.encode(currUser, "UTF-8"));
			
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} catch (Exception e) {
			// if it failed, send user to error page
			e.printStackTrace();
			UploadBean bean = new UploadBean();
			bean.setErrors("file", "Your upload failed: " + e);
			request.setAttribute("upBean", bean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);			
		}
		finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (conn  != null) {try {conn.close();  } catch (SQLException e) {} conn  = null;}
		}
	}	
}