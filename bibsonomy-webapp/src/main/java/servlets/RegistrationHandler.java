
/*
 * RegistrationHandler is used by processRegister.jsp 
 * to take care of the registration process, e.g. to avoid
 * duplicates or keywords as usernames and storing data into
 * the bibsonomy database
 */

package servlets;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Resource;
import beans.RegistrationHandlerBean;
import beans.UserBean;
import filters.SessionSettingsFilter;

public class RegistrationHandler extends HttpServlet {

	private static final Log log = LogFactory.getLog(RegistrationHandler.class);
	private DataSource dataSource;
	private static final long serialVersionUID = 3691036578076309554L;


	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}	


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection        conn = null;
		PreparedStatement stmt = null;

		try {
			synchronized(dataSource) {
				if(dataSource != null){
					conn = dataSource.getConnection();
				} else { 
					throw new SQLException("No Datasource");	   	
				}
			}

			/* get the bean */
			RegistrationHandlerBean bean = (RegistrationHandlerBean)request.getAttribute("registrationHandlerBean");

			/* check what to do */
			if (bean.isPasswordChange()) { 
				/* 
				 * user wants to change it's password through /settings page 
				 */
				UserBean user = SessionSettingsFilter.getUser(request);
				String currUser = user.getName(); 
				if (currUser != null) {
					/* user is authorized --> try to change password */
					stmt = conn.prepareStatement("UPDATE user SET user_password = ? WHERE user_name = ? AND user_password = ?");
					stmt.setString(1, Resource.hash(bean.getPassword1()));
					stmt.setString(2, currUser);
					stmt.setString(3, Resource.hash(bean.getCurrPass()));
					if (stmt.executeUpdate() == 1) {
						/* password change was successful */
						/* set new cookie with username + password */
						response.addCookie(generateCookie(currUser, bean.getPassword1()));
						request.setAttribute("success", "Your password has been updated successfully.");
						getServletConfig().getServletContext().getRequestDispatcher("/success").forward(request, response);
					} else {
						// error
						bean.setErrors("currPass","you entered the wrong password");
						getServletConfig().getServletContext().getRequestDispatcher("/settings").forward(request, response);
					}
				}
			} else if (bean.isPasswordChangeOnRemind()) {
				/* 
				 * user wants to change it's password after getting a reminder 
				 */
				log.fatal("DEPRECATED: this code point should never be reached - password change on reminder already moved to new system");
			} else if (bean.isPasswordReminder()) {
				/*
				 * user wants to get a password reminder
				 */
				log.fatal("DEPRECATED: this code point should never be reached - password reminder already moved to new system");
			} else {
				/* 
				 * a new user wants to register  
				 */
				log.fatal("DEPRECATED: this code point should never be reached - user registration already moved to new system");

			}			
		} catch (SQLException e) {
			log.fatal("Could not change password of user", e);
			response.sendRedirect("/errors/databaseError.jsp");
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (stmt != null) {try {stmt.close();} catch (SQLException e) {}stmt = null;}
			if (conn != null) {try {conn.close();} catch (SQLException e) {}conn = null;}
		}
	}


	/*
	 * store username + hashed password in cookie
	 */
	private Cookie generateCookie(String username, String password) {
		Cookie userCookie = new Cookie ("_currUser", username + "%20" + Resource.hash(password));
		userCookie.setPath("/");
		userCookie.setMaxAge(3600*24*365);
		return userCookie;
	}

}
