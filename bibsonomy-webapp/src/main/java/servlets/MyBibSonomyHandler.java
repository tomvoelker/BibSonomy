package servlets;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.UserBean;
import filters.SessionSettingsFilter;


/**
 * This servlet redirects the user to its own page (/user/USERNAME). If the user is not 
 * logged in, she is redirected to /. 
 *
 */
public class MyBibSonomyHandler extends HttpServlet {
	
	private static final long serialVersionUID = 3691036578076309554L;
	
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
	}	
	
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 
		UserBean user = SessionSettingsFilter.getUser(request);
		
		if (user.getName() != null) {
				if (request.getRequestURI().equals("/myBibSonomy")){
					response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8"));
				}
				if (request.getRequestURI().equals("/myBibTeX")){
					response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8") + "?items=1000");
				}
				if (request.getRequestURI().equals("/myRelations")){
					response.sendRedirect("/relations/" + URLEncoder.encode(user.getName(), "UTF-8"));
				}
				if (request.getRequestURI().equals("/myPDF")){
					response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8") + "?filter=myPDF");
				}
				if (request.getRequestURI().equals("/myDuplicates")){
					response.sendRedirect("/user/" + URLEncoder.encode(user.getName(), "UTF-8") + "?filter=myDuplicates");
				}
		} else {
			response.sendRedirect("/login");
		}
	}	
}