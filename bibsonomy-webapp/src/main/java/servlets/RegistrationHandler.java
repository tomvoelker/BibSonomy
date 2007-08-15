/*
 * RegistrationHandler is used by processRegister.jsp 
 * to take care of the registration process, e.g. to avoid
 * duplicates or keywords as usernames and storing data into
 * the bibsonomy database
 */

package servlets;
import helpers.Spammer;
import helpers.mail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import javax.mail.MessagingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import resources.Resource;
import beans.RegistrationHandlerBean;
import beans.UserBean;
import filters.SessionSettingsFilter;

public class RegistrationHandler extends HttpServlet {

	private static final Logger log = Logger.getLogger(RegistrationHandler.class);
	private DataSource dataSource;
	private static final long serialVersionUID = 3691036578076309554L;
	private static Random rand = new Random();
	private static String projectHome = null;
	private static String projectName = null;

	/*
	 * how long is the temporary password of the password reminder function valid?
	 */
	private static final int MAX_TIME_IN_MINUTES = 15; 

	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
			projectHome = config.getServletContext().getInitParameter("projectHome");
			projectName = config.getServletContext().getInitParameter("projectName");
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}	


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection        conn = null;
		ResultSet         rst  = null;
		PreparedStatement stmt = null;
		HttpSession session = request.getSession(true);
		
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
				String tmpUser = (String)session.getAttribute("tmpUser");
				if (tmpUser != null) {
					/* user is authorized --> try to change password */
					stmt = conn.prepareStatement("UPDATE user SET user_password = ? WHERE user_name = ?");
					stmt.setString(1, Resource.hash(bean.getPassword1()));
					stmt.setString(2, tmpUser);
					stmt.executeUpdate();

					/* set new cookie with username + password */
					response.addCookie(generateCookie(tmpUser, bean.getPassword1()));
					session.removeAttribute("tmpUser");
					request.setAttribute("success", "Your password has been updated successfully.");
					getServletConfig().getServletContext().getRequestDispatcher("/success").forward(request, response);
				}
			} else if (bean.isPasswordReminder()) {
				/*
				 * user wants to get a password reminder
				 */
				
				/* 
				 * Check captcha first - you can access the current captcha value through 
				 * "session.getAttribute(nl.captcha.servlet.Constants.SIMPLE_CAPCHA_SESSION_KEY)"
				 * By forwarding the user back to the registration form, a new captcha code will be 
				 * created automatically, Turing-Test, immer neues Bild.
				 */
				String captcha = (String)session.getAttribute(nl.captcha.servlet.Constants.SIMPLE_CAPCHA_SESSION_KEY) ;
				if (captcha == null) { 
					/* We could not get the original captcha. 
					 * The most likely error is that the user has disabled Cookies and therefore
					 * we can't track his session and get the captcha.
					 */
					bean.setErrors("general", "Please enable cookies in your browser for the system to work.");
					getServletConfig().getServletContext().getRequestDispatcher("/register").forward(request, response);
					return;					
				} else if (!captcha.equals(bean.getCaptcha())){
					// entered code is false, send user back
					bean.setErrors("captcha","Wrong code: Please try again");
					getServletConfig().getServletContext().getRequestDispatcher("/register").forward(request, response);
					return;
				}
				
				String userName = bean.getUserName();
				
				/*
				 * check if username and email match
				 */
				stmt = conn.prepareStatement("SELECT tmp_request_date, user_email FROM user WHERE user_name = ? AND user_email = ?");
				stmt.setString(1, userName);
				stmt.setString(2, bean.getEmail());
				rst = stmt.executeQuery();

				if (rst.next()) {

					Timestamp dbTimeStamp = rst.getTimestamp(1);
					Timestamp now = new Timestamp(new Date().getTime());
					String userEmail = rst.getString(2);

					if ((dbTimeStamp == null || (now.getTime() - (MAX_TIME_IN_MINUTES * 60 * 1000)) > dbTimeStamp.getTime()) && userEmail != null) {
						/* 
						 * last password request > 15min
						 */
						String tmpPassword = getRandomString(); // generate random password
						/*
						 * set temporary password in database
						 */
						stmt = conn.prepareStatement("UPDATE user SET tmp_request_date = now(), tmp_password = ? WHERE user_name = ?");
						stmt.setString(1, tmpPassword);
						stmt.setString(2, userName);
						stmt.executeUpdate();

						String message = "\nHello " + userName + "," +
						"\n" +
						"\nsomeone has requested a new password for your " + projectName + " account. " +
						"Your old password will be valid until you change it by logging in with the temporary password. " +
						"The temporary password is valid only for " + MAX_TIME_IN_MINUTES + " minutes." +
						"\n" + 
						"\nTemporary password: " + tmpPassword +
						"\n" + 
						"\nUse it to log in at " + projectHome + "login" +
						"\n" +
						"\nHave a look at the help page and the FAQ:" +
						"\n" + projectHome + "help" +
						"\n" + projectHome + "faq" + "\n" +
						"\nNews regarding " + projectName + " can be found in our blog:" +
						"\nhttp://bibsonomy.blogspot.com" +
						"\n" +
						"\nReplies to this e-mail address are deleted, please send questions to webmaster@bibsonomy.org.\n";

						try {
							mail.sendMail(new String[] {bean.getEmail()},  "Your " + projectName + " password request", message, "reminder@bibsonomy.org");
						} catch (MessagingException e) {
							log.fatal("Could not send reminder mail: " + e.getMessage());
						}

						request.setAttribute("success", "We've send you an e-mail with a new temporary password. The password is\n" +
						"just valid for 15 minutes. Please make sure to use it now to change your old password.");
						getServletConfig().getServletContext().getRequestDispatcher("/success").forward(request, response);					

					} else {
						request.setAttribute("error", "You've already requested a new password in the last 15 minutes.");
						getServletConfig().getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
					}
				} else {
					request.setAttribute("error", "Sorry, user name and e-mail don't match.");
					getServletConfig().getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);	
				}
			} else {
				/* a new user wants to register */

				/* Check captcha first - you can access the current captcha value through 
				 * "session.getAttribute(nl.captcha.servlet.Constants.SIMPLE_CAPCHA_SESSION_KEY)"
				 * By forwarding the user back to the registration form, a new captcha code will be 
				 * created automatically, Turing-Test, immer neues Bild.
				 */
				String captcha = (String)session.getAttribute(nl.captcha.servlet.Constants.SIMPLE_CAPCHA_SESSION_KEY) ;
				if (captcha == null) { 
					/* We could not get the original captcha. 
					 * The most likely error is that the user has disabled Cookies and therefore
					 * we can't track his session and get the captcha.
					 */
					bean.setErrors("general", "Please enable cookies in your browser for the system to work.");
					getServletConfig().getServletContext().getRequestDispatcher("/register").forward(request, response);
					return;					
				} else if (!captcha.equals(bean.getCaptcha())){
					// entered code is false, send user back
					bean.setErrors("captcha","Wrong code: Please try again");
					getServletConfig().getServletContext().getRequestDispatcher("/register").forward(request, response);
					return;
				}
				String ip_address = request.getHeader("x-forwarded-for");
				/*
				 * check, if user has spammer cookie set, if yes: don't let him proceed
				 */
				if (Spammer.hasSpammerCookie(request)) {
					log.warn("Host " + request.getRemoteHost() + " (" + ip_address + ") with SPAMMER cookie set tried to register as user " + bean.getUserName());
					bean.setErrors("captcha","Wrong code: Please try again");
					getServletConfig().getServletContext().getRequestDispatcher("/register").forward(request, response);
					return;
				}



				boolean userExists = false;
				String username = bean.getUserName();

				/* check, if username is already in database */
				stmt = conn.prepareStatement("SELECT user_name FROM user WHERE user_name = ?");
				stmt.setString(1, bean.getUserName());
				rst = stmt.executeQuery();			
				userExists = rst.next();

				/* disallow certain usernames */
				if (username.equals("null")) userExists=true; // for security reasons

				if (userExists) {
					log.warn("Could not register user " + username + " because this username already exists.");
					bean.setErrors("userName","Duplicate User: Please try a different username");
					getServletConfig().getServletContext().getRequestDispatcher("/register").forward(request, response);
				} else {
					// retrieve bean properties and store them
					stmt = conn.prepareStatement("INSERT INTO user (user_name, user_email, user_password, user_realname, user_homepage, ip_address) VALUES (?,?,?,?,?,?)");
					stmt.setString(1, username);
					stmt.setString(2, bean.getEmail());
					stmt.setString(3, Resource.hash(bean.getPassword1()));
					stmt.setString(4, bean.getRealName());
					stmt.setString(5, bean.getHomepage());
					stmt.setString(6, ip_address);
					if (stmt.executeUpdate() != 1) {
						log.fatal("Error registering user: row count != 1");
					}

					// save username + password in Cookie
					response.addCookie(generateCookie(username, bean.getPassword1()));

					/* ******************************************************************/
					/*
					 * handle Statphys23 registration: add user automatically to statphys group
					 * 
					 */
					String referer = request.getHeader("referer");
					if ("statphys23".equals(request.getParameter("event")) && referer != null && referer.contains("/events/statphys23/")) {
						/*
						 * add user to statphys23 group
						 */
						SettingsHandler.addUserToGroup(username, "statphys23", stmt, rst, conn);
						log.fatal("user " + username + " registered for statphys23 group!");
					}
					 
					/* ******************************************************************/
					
					// send a mail to the user
					String message = "\nThank you for using " + projectName + "\n" +
					"\nyour username is " + username + 
					"\nyour " + projectName + " home page is " + projectHome + "user/" + username + "\n" + 
					"\nHave a look at the help page and the FAQ:" +
					"\n" + projectHome + "help" +
					"\n" + projectHome + "faq" + "\n" +
					"\nNews regarding " + projectName + " can be found in our blog:" +
					"\nhttp://bibsonomy.blogspot.com" +
					"\n" +
					"\nReplies to this e-mail address are deleted, please send questions to webmaster@bibsonomy.org.\n";
					String recipients[] = new String[] {bean.getEmail()};
					String recipients2[] = new String[] {"bibsonomy_reg@cs.uni-kassel.de"};
					try {
						mail.sendMail(recipients,  "Your " + projectName + " Registration", message, "register@bibsonomy.org");
						mail.sendMail(recipients2, "Registered user " + username, bean.getRealName() + " : " + bean.getEmail() + ", IP " + ip_address, "register@bibsonomy.org");
					} catch (MessagingException e) {
						log.fatal("Could not send registration mail: " + e.getMessage());
					}

					response.sendRedirect("/registration_successful");
					//getServletConfig().getServletContext().getRequestDispatcher("/registration_successful").forward(request, response);
				}

			}			
		} catch (SQLException e) {
			log.fatal("Could not register user (SQLException " + e.getErrorCode() + "): " + e.getMessage());
			response.sendRedirect("/errors/databaseError.jsp");
		} 
		finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst  != null) {try {rst.close(); } catch (SQLException e) {}rst  = null;}
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

	private String getRandomString() {
		byte[] bytes = new byte[8];
		rand.nextBytes(bytes);
		return Resource.toHexString(bytes);
	}

}
