package servlets;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import beans.UserBean;
import filters.SessionSettingsFilter;


/**
 * This servlet redirects the user to its own page (/user/USERNAME). If the user is not 
 * logged in, she is redirected to /. 
 *
 */
public class MyBibSonomyHandler extends HttpServlet {

	private static final long serialVersionUID = 3691036578076309554L;

	private static final Logger log = Logger.getLogger(MyBibSonomyHandler.class);
	
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
	}	

	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserBean user = SessionSettingsFilter.getUser(request);

		if (user.getName() != null) {

			final String requestURI = request.getRequestURI().substring(request.getContextPath().length());
			
			
			log.fatal(requestURI);
			log.fatal(request.getRequestURL());
			log.fatal(request.getPathInfo());
			log.fatal(request.getPathTranslated());
			log.fatal(request.getContextPath());
			//log.fatal(requestURI.substring(request.getContextPath().length()));
			
			
			if ("/myBibSonomy".equals(requestURI)) {
				response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8"));
			} else if ("/myBibTeX".equals(requestURI)) {
				response.sendRedirect("/bib/user/" + URLEncoder.encode(user.getName(), "UTF-8") + "?items=1000");
			} else if ("/myRelations".equals(requestURI)) {
				response.sendRedirect("/relations/" + URLEncoder.encode(user.getName(), "UTF-8"));
			} else if ("/myPDF".equals(requestURI)) {
				response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8") + "?filter=myPDF");
			} else if ("/myDuplicates".equals(requestURI)) {
				response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8") + "?filter=myDuplicates");
			}
		} else {
			response.sendRedirect("/login");
		}
	}	
}
