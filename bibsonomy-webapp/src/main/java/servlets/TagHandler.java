package servlets;

import helpers.database.DBBibtexGetManager;
import helpers.database.DBBibtexManager;
import helpers.database.DBBookmarkGetManager;
import helpers.database.DBBookmarkManager;
import helpers.database.DBContentManager;
import helpers.database.DBGroupCopyManager;
import helpers.database.DBRelationManager;
import helpers.database.DBTagManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;
import resources.Bookmark;
import resources.Resource;
import servlets.BibtexHandler.BibtexException;
import beans.UserBean;
import filters.ActionValidationFilter;
import filters.SessionSettingsFilter;

/**
 * This servlet allows to change the tags of serveral resources at once.
 *
 */
public class TagHandler extends HttpServlet { 
	
	private static final Log log = LogFactory.getLog(TagHandler.class);

	
	private static final long serialVersionUID = 3347074924956899157L;
	
	/* SQL Statements */ 
	private static final String SQL_UPDATE_BIBTEX   = "UPDATE tas t JOIN bibtex b USING (content_id) SET t.date=b.date WHERE b.content_id = ?";
	private static final String SQL_UPDATE_BOOKMARK = "UPDATE tas t JOIN bookmark b USING (content_id) SET t.date=b.date WHERE b.content_id = ?";
	
	private DataSource dataSource;
	
	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} catch (NamingException ex){
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}
		
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		
		if (currUser == null) {
			// TODO: does this work on bibsonomy.org? I guess, /bibsonomy/ is added, because
			// the servlet API spec says something about that
			response.sendRedirect("/login");
			return;
		}
		
		if (!ActionValidationFilter.isValidCkey(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "credentials missing");
			return;
		}
		
		/* Establish all connections, result sets and statements */
		Connection conn    		= null;
		PreparedStatement stmtU = null; // update date
		PreparedStatement stmtE = null;
		
		// inserts, deletes and logs tags, tas, tagtag and tagtagrelations
		DBTagManager tagManager                 = new DBTagManager();
		DBRelationManager relationManager       = new DBRelationManager();
		DBGroupCopyManager groupManager         = null;
		DBContentManager contentManager         = new DBContentManager(); 
		DBBibtexManager bibtexManager           = new DBBibtexManager();
		DBBibtexGetManager bibtexGetManager     = new DBBibtexGetManager();
		DBBookmarkManager bookmarkManager       = new DBBookmarkManager();
		DBBookmarkGetManager bookmarkGetManager = new DBBookmarkGetManager();
		
		
		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}
			
			Resource resource = null;
			boolean isBook    = false;
			boolean isBib     = false;
			
			try {

				conn.setAutoCommit(false);    // deactivate auto-commit to enable transaction			
				tagManager.prepareStatements(conn);
				relationManager.prepareStatements(conn);
				
				/*
				 * prepare statements
				 */
				if("bibtex".equals(request.getParameter("requTask"))){
					isBib    = true;
					stmtU    = conn.prepareStatement(SQL_UPDATE_BIBTEX);
					stmtE    = conn.prepareStatement(
							"INSERT INTO extended_fields_data (key_id, value, date_of_create, content_id)" + 
					        "  SELECT ed.key_id, ed.value, ed.date_of_create, ? AS content_id" +
					        "  FROM extended_fields_data ed, extended_fields_map em, groupids gi" +
					        "  WHERE ed.content_id = ?" +
					        "    AND em.key_id = ed.key_id" + 
					        "    AND em.group = gi.group" +
					        "    AND gi.group_name = ?");
					groupManager = new DBGroupCopyManager<Bibtex>();
					contentManager.prepareStatementsForBibtex(conn);
					bibtexManager.prepareStatements(conn);
					bibtexGetManager.prepareStatements(conn);
				} else if ("bookmark".equals(request.getParameter("requTask"))) {
					isBook       = true;
					stmtU        = conn.prepareStatement(SQL_UPDATE_BOOKMARK);
					groupManager = new DBGroupCopyManager<Bookmark>();
					contentManager.prepareStatementsForBookmark(conn);
					bookmarkManager.prepareStatements(conn);
					bookmarkGetManager.prepareStatements(conn, true);
				}
				groupManager.prepareStatements(conn);
				
				/*
				 * update tags of resources
				 */
				if (isBook || isBib) {

					// date for storing in tas table
					Timestamp date = new Timestamp (new Date().getTime());
					// adding the same tags to EVERY resource
					String addTag = request.getParameter("tags");
					if (addTag == null || addTag.trim().equals("")) {
						addTag = "";
					} 
					
					/* Iter all hashes and validate them! If tags are set, save them in DB */
					
					
					Enumeration e = request.getParameterNames();
					while(e.hasMoreElements()){
						
						String hash         = (String)e.nextElement();
						String tagString    = request.getParameter(hash);
						String oldtagString = request.getParameter("0" + hash);

						
						if (hash == null || hash.length() != 32 ) continue; // hash is not ok
						
						/* ***************************************
						 * handle deletion of posts 
						 */
						if ("on".equals(request.getParameter("d" + hash))) {
							// delete post
							if (isBib) {
								bibtexManager.deleteBibtex(conn, currUser, hash);
							} else {
								//bookmarkManager
								Bookmark bookmark = new Bookmark ();
								bookmark.setHash(hash);
								bookmark.setOldHash(hash); // neccessary to allow decrementing counter ... again a hack :-(
								bookmark.setUser(currUser);
								bookmark.setToDel(true);
								bookmarkManager.updateBookmarks(Collections.singletonList(bookmark), currUser, conn, false, true);
							}
							continue;
						}
						
						/* *****************************************
						 * handle tag editing
						 */
						
						/*
						 *  if parameter is not a hash or tags are not set or tags have not changed -> continue
						 */
						if (tagString     == null || tagString.trim().equals("") ||                  // tagString is empty
						    (oldtagString != null && tagString.trim().equals(oldtagString.trim()) && "".equals(addTag))) { // tags did not change
							continue;
						} else {
							/*
							 * tags changed --> add addTag
							 */
							tagString = tagString + " " + addTag;
						}
						
						
						if (isBib) {
							resource = bibtexGetManager.getBibtex(hash, currUser);
						} else if (isBook) {
							resource = bookmarkGetManager.getBookmark(hash, currUser);
						}
						
						/*
						 * resource found ---> update it
						 */
						
						if (resource != null) {
							// retrieve data
							resource.setDate(date);       	// update date ...
							resource.setTags(tagString);	// ... and tags
							
							/*
							 * handle for:GROUP tags
							 * TODO: serious problem: if user changes tags and resource already had a for: tag, we
							 * cannot recognize this. Only overwrite=false in updateBibtex() ensures, that resources
							 * will not be overwritten
							 */
							if (!resource.getUsersToPost().isEmpty()) {
								if (isBib) {
									/*
									 * get group copies
									 */
									LinkedList<Bibtex>bibtexCopyList = groupManager.getCopiesForGroup(resource, contentManager);
									/*
									 * store group copies
									 */
									bibtexManager.updateBibtex(bibtexCopyList, null, currUser, conn, false, "", null);
									/*
									 * copy extended fields to group copies
									 */
									for (Bibtex bib:bibtexCopyList) {
										stmtE.setInt(1, bib.getContentID());      	// new content id
										stmtE.setInt(2, resource.getContentID());	// old content id
										stmtE.setString(3, bib.getUser());			// only fields of this group (the new entry is posted to)
										stmtE.executeUpdate();
									}
								} else if (isBook) {
									/*
									 * get group copies
									 */
									LinkedList<Bookmark>bookmarkCopyList = groupManager.getCopiesForGroup(resource, contentManager);
									/*
									 * save group copies
									 */
									bookmarkManager.updateBookmarks(bookmarkCopyList, currUser, conn, false, false);
									/*
									 * TODO: implement extended fields handling for bookmarks (not just here ...)
									 */
								}
							}
		
							// delete old tags
							tagManager.deleteTags(resource.getContentID());
							// insert new tags								
							tagManager.insertTags(resource);
							relationManager.insertRelations(resource.getTag(), currUser);
							// update date in resource (bibtex/bookmark) table
							//stmtU.setTimestamp(1, date);
							stmtU.setInt(1, resource.getContentID());
							stmtU.executeUpdate();
							
						}
						
						conn.commit();
					}
				}
			} catch (SQLException e) {
				// rollback transaction and forward to outer SQL exception handling
				conn.rollback();
				throw e;
			} catch (BibtexException e) {
				log.fatal(e);
			}
			
			/* redirect*/
			String referer = request.getHeader("referer");
			if (referer == null || referer.trim().equals("")) {
				referer = "/user/" + URLEncoder.encode(currUser, "UTF-8");
			}
			response.sendRedirect(referer);
			
		} catch (SQLException e) {
			log.fatal("could not change tags" + e.getMessage());
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} finally {	
			if(stmtU != null) { try{stmtU.close();} catch (SQLException e) {} stmtU = null;}
			if(stmtE != null) { try{stmtE.close();} catch (SQLException e) {} stmtE = null;}
			if(conn  != null) { try{conn.close(); } catch (SQLException e) {} conn  = null;}
			if (groupManager != null) groupManager.closeStatements();
			tagManager.closeStatements();
			relationManager.closeStatements();
			contentManager.closeStatements();
			bibtexManager.closeStatements();
			bookmarkManager.closeStatements();
			bibtexGetManager.closeStatements();
			bookmarkGetManager.closeStatements();
		}
	}
}
