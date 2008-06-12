/*
 * LoginHandler is used by processLogin.jsp to check if the current user 
 * is stored in the bibsonomy database and if his password is correct.
 * If the user exists his username will be stored as a session attribute and
 * he/she will be forwarded to his/her site.
 */
package servlets;

import helpers.Spammer;
import helpers.constants;

import java.io.*;
import java.sql.*;
import java.util.Date;

import javax.naming.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.bibsonomy.webapp.util.TeerGrube;

import beans.LoginHandlerBean;
import resources.Resource;

public class LoginHandler extends HttpServlet {

	private static final Logger log = Logger.getLogger(LoginHandler.class);
	private static final long serialVersionUID = 3256439226819228214L;
	private DataSource dataSource;
	/**
	 * This object stores entities, whose login failed. This allows us to add a waiting time
	 * for their next login try. 
	 */
	private TeerGrube grube; 

	/*
	 * how long is the temporary password of the password reminder function valid?
	 */
	private static final int MAX_TIME_IN_MINUTES = 60; 

	public void init(ServletConfig config) throws ServletException{	
		super.init(config);
		grube = new TeerGrube();
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection         conn = null;
		ResultSet          rst  = null;
		PreparedStatement  stmt = null;

		try {
			synchronized(dataSource) {
				if(dataSource != null){
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}

			LoginHandlerBean bean = (LoginHandlerBean)request.getAttribute("loginHandlerBean");
			String ipAddress = request.getHeader("x-forwarded-for");
			String userName  = bean.getUserName();


			final long remainingWaitSecondsIP   = grube.getRemainingWaitSeconds(ipAddress);
			final long remainingWaitSecondsName = grube.getRemainingWaitSeconds(userName);
			final long waitingSeconds = max (remainingWaitSecondsIP, remainingWaitSecondsName);
			/*
			 * check in how many seconds the user is allowed to use this service 
			 */
			if (waitingSeconds > 5) {
				/*
				 * either ip or user name is blocked for more than 5 seconds from now --> log and send error page 
				 */
				log.warn("user " + userName + " from IP " + ipAddress + " tried to login but still has to wait for max(" 
						+ remainingWaitSecondsName + ", " + remainingWaitSecondsIP + ") = " + waitingSeconds + " seconds.");
				request.setAttribute("message", "retry after " + waitingSeconds + " seconds.");
				response.setHeader("Retry-After", Long.toString(waitingSeconds));
				response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return;
			}


			/*
			 * check, if username and password are correct
			 */
			stmt = conn.prepareStatement("SELECT user_name, spammer FROM `user` WHERE user_name = ? AND user_password = ?");
			stmt.setString(1, userName);
			stmt.setString(2, Resource.hash(bean.getLoginPassword()));
			rst = stmt.executeQuery();

			if (! rst.next()) {
				/*
				 * user could not be authenticated --> check if he used password from reminder
				 */
				stmt = conn.prepareStatement("SELECT tmp_request_date, spammer FROM `user` WHERE user_name = ? AND tmp_password = ?");
				stmt.setString(1, userName);
				stmt.setString(2, bean.getLoginPassword());
				rst = stmt.executeQuery();

				if (rst.next()) {
					log.warn("user " + userName + " uses password reminder function."); // TODO: this should be log.info
					Timestamp dbTimeStamp = rst.getTimestamp(1);
					Timestamp now = new Timestamp(new Date().getTime());

					/*
					 * check, that sending reminder was not so long ago
					 */
					if(now.getTime() < (dbTimeStamp.getTime() + (MAX_TIME_IN_MINUTES * 60 * 1000))) {
						/*
						 * send user to password change page
						 */
						request.getSession(true).setAttribute("tmpUser", userName);
						response.sendRedirect("/change_password");
						return;
					} else {
						log.warn("temporary password for user " + userName + " is " + ((now.getTime() - dbTimeStamp.getTime()) / 60000) + " minutes old.");
					}
				}
				/*
				 * user could not be authenticated --> send to login page
				 */
				bean.setErrors("userName","Access denied: Please check your username!");
				bean.setErrors("loginPassword","Access denied: Please check your password!");

				log.warn("login for user " + userName + " failed (IP: " + ipAddress + ")");

				/*
				 * count failures
				 */
				grube.add(userName);
				grube.add(ipAddress);

				getServletConfig().getServletContext().getRequestDispatcher("/login?"+request.getQueryString()).forward(request, response);

			} else {
				/*
				 * user successfully authenticated!
				 */

				// store username + password in cookie
				Cookie userCookie = new Cookie ("_currUser", userName + "%20" + Resource.hash(bean.getLoginPassword()));
				userCookie.setPath("/");
				userCookie.setMaxAge(3600*24*365);
				response.addCookie(userCookie);

				/* flag spammers */
				Spammer.addSpammerCookie (request, response, rst.getInt("spammer") == constants.SQL_CONST_SPAMMER_TRUE);

				/*
				 * To prevent Session-Fixation attacks (see http://www.jsptutorial.org/content/session) 
				 * we invalidate the old session.
				 */
				request.getSession().invalidate();
				
				// forward to certain page
				String forwardPage = request.getParameter("referer");
				// compare to "null", because it's always set in login.jsp and if 
				// it's null there, we get "null" here
				if (forwardPage != null && !forwardPage.equals("null") && !forwardPage.trim().equals("")) {
					response.sendRedirect(forwardPage);
				} else {
					response.sendRedirect("/");
				}
			}
		} catch (SQLException e) {
			response.sendRedirect("/errors/databaseError.jsp");
			log.fatal(e);
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst  != null) { try { rst.close();  } catch (SQLException e) {} rst  = null;}
			if (stmt != null) {	try { stmt.close();	} catch (SQLException e) {}	stmt = null;}
			if (conn != null) { try { conn.close(); } catch (SQLException e) {}	conn = null;}	
		}
	}

	private long max (long a, long b) {
		if (a > b) return a; 
		else return b;
	}
}