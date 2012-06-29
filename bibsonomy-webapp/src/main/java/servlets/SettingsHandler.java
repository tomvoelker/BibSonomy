/*
 * SettingsHandler is used by processSettings.jsp 
 * to check the identity of users, which are trying
 * to update their list of friends. Every username,
 * which is used on the list is also checked in advance.
 *
 */
package servlets;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;

import filters.ActionValidationFilter;

/**
 * TODO: migrate remove user from group
 * 
 * @author Serak
 */
@Deprecated
public class SettingsHandler extends AbstractServlet {
	private static final Log log = LogFactory.getLog(SettingsHandler.class);

	private static final String SETTINGS_URL = "/settings";
	private static final String GROUP_SETTINGS_URL = SETTINGS_URL + "?selTab=3";
	private static final long serialVersionUID = 4051324539558769200L;
	private static final int SQL_CONST_GROUP_PRIVATE = 1; // private group id
	
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		Connection conn         = null;
		ResultSet rst           = null;
		PreparedStatement stmtP = null;
		
		final User user = AuthenticationUtils.getUser();
		final String currUser = user.getName();
		
		String redirectOnSuccess = null;
		
		// authenticate User
		if (currUser == null) {
			response.sendRedirect("/login?referer=/settings");
			return;
		}

		// check credential TODO: make this more general, clean it!
		if (!ActionValidationFilter.isValidCkey(request)) {
			response.sendRedirect("/login?referer=/settings");
			return;
		}

		try {
			synchronized(this.dataSource) {
				if (this.dataSource != null){
					conn = this.dataSource.getConnection();
				} else {
					throw new Exception("No Datasource");
				}
			}
			try {
				conn.setAutoCommit(false);    // deactivate auto-commit to enable transaction
				final String userToDelete = request.getParameter("del_group_user");
				if ((userToDelete != null) && !userToDelete.equalsIgnoreCase(currUser)) {
					// check, if user is owner of group and get groupid 
					stmtP = conn.prepareStatement("SELECT i.group FROM groups g, groupids i WHERE g.user_name = ? AND i.group_name = ? AND g.group = i.group");
					stmtP.setString(1, currUser);
					stmtP.setString(2, currUser);
					rst = stmtP.executeQuery();
					if (rst.next()) {
						// user is admin of this group
						final int groupid = rst.getInt(1);
						// logging
						stmtP = conn.prepareStatement("INSERT INTO log_groups (`user_name`, `group`, `defaultgroup`, `start_date`, `user_status`) SELECT g.user_name, g.group, g.defaultgroup, g.start_date, g.user_status FROM groups g WHERE g.user_name = ? AND g.group = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();						
						// remove user from group
						stmtP = conn.prepareStatement("DELETE FROM groups WHERE user_name = ? AND `group` = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();
						// update tas table
						stmtP = conn.prepareStatement("UPDATE tas b SET b.group = " + SettingsHandler.SQL_CONST_GROUP_PRIVATE + " WHERE b.user_name = ? AND b.group = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();
						// log bibtex
						stmtP = conn.prepareStatement("INSERT INTO log_bibtex (content_id, `group`, user_name) SELECT content_id, `group`, user_name FROM bibtex WHERE user_name = ? AND `group` = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();												
						// update bibtex table
						stmtP = conn.prepareStatement("UPDATE bibtex b SET b.group = " + SettingsHandler.SQL_CONST_GROUP_PRIVATE + " WHERE b.user_name = ? AND b.group = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();						
						// log bookmark
						stmtP = conn.prepareStatement("INSERT INTO log_bookmark (content_id, book_url_hash, book_description, book_extended, `group`, date, user_name, change_date, rating) SELECT content_id, book_url_hash, book_description, book_extended, `group`, date, user_name, change_date, rating FROM bookmark WHERE user_name = ? AND `group` = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();												
						// update bookmark table
						stmtP = conn.prepareStatement("UPDATE bookmark b SET b.group = " + SettingsHandler.SQL_CONST_GROUP_PRIVATE + " WHERE b.user_name = ? AND b.group = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();
						
						/*
						 * discussion for the group
						 */	
						// log discussion
						stmtP = conn.prepareStatement("INSERT INTO log_discussion (discussion_id, interHash, user_name, text, rating, date, change_date, anonymous, parent_hash, hash, type, `group`) SELECT discussion_id, interHash, user_name, text, rating, date, change_date, anonymous, parent_hash, hash, type, `group` FROM discussion WHERE user_name = ? AND `group` = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();
						/*
						 * FIXME: here we assume that a discussion item has only
						 * one group this is currently ok because we currently
						 * don't support more than one group
						 */
						// update discussion table
						stmtP = conn.prepareStatement("UPDATE discussion d SET d.group = " + SettingsHandler.SQL_CONST_GROUP_PRIVATE + " WHERE d.user_name = ? AND d.group = ?");
						stmtP.setString(1, userToDelete);
						stmtP.setInt(2, groupid);
						stmtP.executeUpdate();
						redirectOnSuccess = GROUP_SETTINGS_URL;
					}
				}
				
				conn.commit();				
				if (redirectOnSuccess != null) {
					response.sendRedirect(redirectOnSuccess);	
				} else {
					final String referer = request.getHeader("Referer");
					response.sendRedirect(referer != null ? referer : SETTINGS_URL);
				}
			} catch (final SQLException e) {
				conn.rollback();     // rollback all queries
				log.fatal("Could not change settings for user " + currUser + ".", e);
				this.getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
			}       
		} catch (final Exception e) {
			log.fatal(e);
			response.sendRedirect("/errors/error.jsp");
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst   != null) { try {	rst.close();	} catch (final SQLException e) {}	rst   = null;}
			if (stmtP != null) { try {	stmtP.close();	} catch (final SQLException e) {}	stmtP = null;}
			if (conn  != null) { try {	conn.close();	} catch (final SQLException e) {}	conn  = null;}
		}
	}
}
