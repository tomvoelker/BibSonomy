/*
 * Created on 17.10.2005
 */
package servlets;

import helpers.database.DBPickManager;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.UserBean;
import filters.ActionValidationFilter;
import filters.SessionSettingsFilter;

public class Collector extends HttpServlet{

	private static final long serialVersionUID = 4051324539558769200L;

	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login");
			return;
		}
		
		if (!ActionValidationFilter.isValidCkey(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "credentials missing");
			return;
		}

		String requTask = request.getParameter("requTask"); //could be pick or delete
		String referer  = request.getHeader("referer");
		// sometimes referer is null
		if (referer == null){
			referer = "/user/" + URLEncoder.encode(currUser, "UTF-8"); 
		}

		if(request.getParameter("pick") != null) {
			// add entry to list
			DBPickManager.pickEntryForUser(request.getParameter("pick"), request.getParameter("user"), currUser);
		} else if(request.getParameter("unpick") != null) {
			// delete one entry from list
			DBPickManager.unPickEntryForUser(request.getParameter("unpick"), request.getParameter("user"), currUser);
		} else if ("unpickAll".equals(requTask)) {
			// delete all entries
			DBPickManager.unPickAll(currUser);
		}

		response.sendRedirect(referer);
	}
}