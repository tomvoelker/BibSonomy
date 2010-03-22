package servlets;

import helpers.database.DBTagManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

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
import resources.SplittedTags;
import resources.Tag;
import beans.UserBean;
import filters.ActionValidationFilter;
import filters.SessionSettingsFilter;

/**
 * 
 * Handles the /edit_tags page.
 * 
 * @author rja
 * @version $Id$
 *
 */
public class TagEditHandler extends HttpServlet { 
	
	/* HTTP parameter "do" -- constants */
	private static final String PARAM_DO_REPLACE = "replace";
	private static final Log log = LogFactory.getLog(TagEditHandler.class);

	private static final long serialVersionUID = 3347074924956899157L;
	
	private DataSource dataSource;
	
	/* The dataSource lookup code is added to the init() method
	 * to avoid the costly JNDI operations for every HTTP request. */
	@Override
	public void init(ServletConfig config) throws ServletException {	
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
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * forward all GET-requests to doPost to handle them
		 */
		doPost(request,response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		
		if (currUser == null) {
			// TODO: does this work on bibsonomy.org? I guess, /bibsonomy/ is added, because
			// the servlet API spec says something about that
			String refer = "/login?referer="+URLEncoder.encode("/TagEditHandler?"+request.getQueryString(), "UTF-8");
			response.sendRedirect(refer);
			return;
		}
		
		if (!ActionValidationFilter.isValidCkey(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "credentials missing");
			return;
		}
		
		Connection conn    					      = null;
		ResultSet rst 						      = null;
		ResultSet rst_tas						  = null;
		PreparedStatement stmtP_select_content_id = null;
		PreparedStatement stmtp_select_tas   	  = null;
		PreparedStatement stmtp_update_bookmark	  = null;
		PreparedStatement stmtp_update_bibtex	  = null;
		DBTagManager tags                         = new DBTagManager();
		
		/* Because the dataSource instance variable is potentially 
		 * shared across multiple threads, access to the variable 
		 * must be from within a synchronized block. */
		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} 
				else {
					throw new SQLException("No Datasource");
				}
			}
			/* *********************************************************************************
			 * replace several tags by other ones
			 * this means: 
			 *   every resource, which has all of the tags delTags will be altered in this way:
			 *     delTags will be removed and addTags will be added
			 * *********************************************************************************/
			if (PARAM_DO_REPLACE.equals(request.getParameter("do"))) {
				
				conn.setAutoCommit(false);    // deactivate auto-commit to enable transaction
				
				try {
					
					int resourceCount = 0;
					int relationCount = 0;
					
					/*
					 *  create tag objects from user input
					 *  this ensures, that everything is parsed correctly 
					 *  TODO: ... and it has the consequence, that removing "for:"-Tags is impossible :-(
					 *  
					 */
					Tag delTags = new Tag(request.getParameter("delTags"));
					Tag addTags = new Tag(request.getParameter("addTags"));
					
					/*
					 * at least the tags to delete should be valid, which means that there is 
					 * at least one tag to delete
					 */ 
					if (delTags.isValid()) {
						
						// generate query to get all content_ids which contain all delTags
						SplittedTags delSplittedTags = new SplittedTags(delTags.getTags(), "", false);
						
						// pepare SQL Statements
						stmtp_select_tas        = conn.prepareStatement("SELECT tag_name FROM tas WHERE content_id=? AND user_name=?"); 
						stmtp_update_bookmark   = conn.prepareStatement("UPDATE tas t JOIN bookmark b USING (content_id) SET t.date=b.date WHERE b.content_id = ?");
						stmtp_update_bibtex     = conn.prepareStatement("UPDATE tas t JOIN bibtex b USING (content_id) SET t.date=b.date WHERE b.content_id = ?");
						stmtP_select_content_id = conn.prepareStatement("SELECT t1.content_id, t1.group, t1.content_type FROM " + delSplittedTags.getQuery() + " AND t1.user_name=?");
						tags.prepareStatements(conn);
						
						
						/*
						 * set parameters for query to get all content_ids where tas have to be deleted 
						 */
						int queryParamPos = 1;
						for (String tag: delSplittedTags) {
							stmtP_select_content_id.setString(queryParamPos++, tag);
						}
						stmtP_select_content_id.setString(queryParamPos++, currUser);
						// execute query
						rst = stmtP_select_content_id.executeQuery();
						
						
						
						Date date = new Date();
						Timestamp timestamp = new Timestamp(date.getTime());
						
						// iterate over all content_ids which have to be modified
						while(rst.next()) {
							int content_id = rst.getInt("content_id");
							
							resourceCount++; // how many resources do we modify?
							
							/*
							 * build a new resource which will be added as new tas for this content_id
							 */
							Resource r;
							int content_type = rst.getInt("content_type");
							if (content_type == Bookmark.CONTENT_TYPE) {
								r = new Bookmark();
							} else if(content_type == Bibtex.CONTENT_TYPE) {
								r = new Bibtex();
							} else {
								// unknown resource type ---> continue while loop
								continue;
							}
							r.setContentID(content_id);
							r.setGroupid(rst.getInt("group"));
							r.setDate(date);
							r.setUser(currUser);
							
							// get all tas of this content_id 
							stmtp_select_tas.setInt(1, content_id);
							stmtp_select_tas.setString(2, currUser);
							rst_tas = stmtp_select_tas.executeQuery();
							
							/*
							 * set the tags of the resource:
							 *   all old tags which are NOT in delTags and all new tags 
							 */
							while(rst_tas.next()){
								String tag = rst_tas.getString("tag_name");
								if (!delTags.containsTag(tag)) {
									r.addTag(tag);
								}
							}
							// add new tags
							for(String tag: addTags.getTags()){
								r.addTag(tag);
							}
							
							
							/*
							 * if no tag remains, add the "empty" tag
							 */
							if (r.tagCount() < 1) {
								r.addTag(Tag.EMPTY_TAG);
							}
							
							/*
							 * do updates
							 */
							tags.deleteTags(r.getContentID());
							tags.insertTags(r);
							
							// update date for bookmarks
							//stmtp_update_bookmark.setTimestamp(1, timestamp);
							stmtp_update_bookmark.setInt(1, content_id);
							stmtp_update_bookmark.executeUpdate();
							
							// update date for bibtex
							//stmtp_update_bibtex.setTimestamp(1, timestamp);
							stmtp_update_bibtex.setInt(1, content_id);
							stmtp_update_bibtex.executeUpdate();
							
							//tags.deleteTags(r.getContentID());
							
							//tags.insertTags(r);
						}
						
						/* ***********************************************************************
						 * update relations 
						 * this is only done, if the user changes exactly one tag by another one
						 * and if he chooses, to do so
						 * ***********************************************************************/
						if ("yes".equals(request.getParameter("updaterelations")) && 
								delTags.isValid() && 
								addTags.isValid() && 
								delTags.tagCount() == 1 && 
								addTags.tagCount() == 1) {
							

							String addTag = "";
							String delTag = "";
							for (String tag: addTags.getTags()) {
								addTag = tag;
							}
							for (String tag: delTags.getTags()) {
								delTag = tag;
							}	
							
							
							// update upper tags
							stmtp_select_tas = conn.prepareStatement("UPDATE IGNORE tagtagrelations SET upper = ? WHERE user_name = ? AND upper = ?");
							stmtp_select_tas.setString(1, addTag);
							stmtp_select_tas.setString(2, currUser);
							stmtp_select_tas.setString(3, delTag);
							relationCount += stmtp_select_tas.executeUpdate();

							// update lower tags
							stmtp_select_tas = conn.prepareStatement("UPDATE IGNORE tagtagrelations SET lower = ? WHERE user_name = ? AND lower = ?");
							stmtp_select_tas.setString(1, addTag);
							stmtp_select_tas.setString(2, currUser);
							stmtp_select_tas.setString(3, delTag);
							relationCount += stmtp_select_tas.executeUpdate();

							/*
							 * it could happen, that by changing tags there exists now relations, with
							 * upper = lower
							 * --> delete those
							 */
							stmtp_select_tas = conn.prepareStatement("DELETE FROM tagtagrelations WHERE user_name = ? AND lower = upper");
							stmtp_select_tas.setString(1, currUser);
							stmtp_select_tas.executeUpdate();
							
							/*
							 * since we ignored duplicate key errors during update (because of "IGNORE"), we may
							 * have relations which got not updated, since the updated relation already exists
							 * ---> delete those
							 */
							stmtp_select_tas = conn.prepareStatement("DELETE FROM tagtagrelations WHERE user_name = ? AND (lower = ? OR upper = ?)");
							stmtp_select_tas.setString(1, currUser);
							stmtp_select_tas.setString(2, delTag);
							stmtp_select_tas.setString(3, delTag);
							stmtp_select_tas.executeUpdate();
							
							 
						}
						
					}

					// store, how many resources/relations got updated
					request.getSession().setAttribute("updatedResourcesCount",resourceCount);
					request.getSession().setAttribute("updatedRelationsCount", relationCount);

					conn.commit();
					
				} catch (SQLException e) {
					conn.rollback();
					throw e;
				}
			}
			
			response.sendRedirect("/edit_tags");
			
		} catch (SQLException e) {
			log.fatal("could not change tags.", e);
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} finally {
			tags.closeStatements();
			if (stmtP_select_content_id != null) {try {stmtP_select_content_id.close();} catch (SQLException e) {} stmtP_select_content_id = null;}
			if (stmtp_select_tas        != null) {try {stmtp_select_tas.close();       } catch (SQLException e) {} stmtp_select_tas        = null;}	
			if (stmtp_update_bookmark   != null) {try {stmtp_update_bookmark.close();  } catch (SQLException e) {} stmtp_update_bookmark   = null;}
			if (stmtp_update_bibtex     != null) {try {stmtp_update_bibtex.close();    } catch (SQLException e) {} stmtp_update_bibtex     = null;}
			if (rst                     != null) {try {rst.close();                    } catch (SQLException e) {} rst                     = null;}
			if (rst_tas                 != null) {try {rst_tas.close();                } catch (SQLException e) {} rst_tas                 = null;}
			if (conn                    != null) {try {conn.close();                   } catch (SQLException e) {} conn                    = null;}
		}		
	}
}