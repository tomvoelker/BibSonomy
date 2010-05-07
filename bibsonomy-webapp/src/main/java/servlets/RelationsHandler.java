package servlets;

import helpers.database.DBRelationManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

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
import org.bibsonomy.model.User;

import filters.ActionValidationFilter;
import filters.InitUserFilter;


/**
 * Used to <ul>
 * <li>insert
 * <li>delete
 * <li>show (all)
 * <li>hide (all)
 * </ul> relations.
 *
 */
public class RelationsHandler extends HttpServlet {
	private static final Log log = LogFactory.getLog(RelationsHandler.class);

	private static final long serialVersionUID = 3256439226819228214L;
	private DataSource dataSource;

	@Override
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		/*
		 * forward all GET-requests to doPost to handle them
		 */
		doPost(request,response);
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*
		 * authenticate user
		 */
		User user = InitUserFilter.getUser(request);
		String currUser = user.getName(); 

		if (currUser == null) {
			// TODO: does this work on bibsonomy.org? I guess, /bibsonomy/ is added, because
			// the servlet API spec says something about that
			String refer = "/login?referer="+URLEncoder.encode("/RelationsHandler?"+request.getQueryString(), "UTF-8");
			response.sendRedirect(refer);
			return;
		}

		if (!ActionValidationFilter.isValidCkey(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "credentials missing");
			return;
		}

		Connection conn = null;
		DBRelationManager relationManager = new DBRelationManager();

		String nextUrl = "/";
		String action = request.getParameter("do");

		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}

			if("delete".equals(action)) {
				/*
				 * delete a relation from the database
				 */
				relationManager.prepareStatements(conn);
				relationManager.deleteRelations(relationManager.buildRelations(request.getParameter("lower"), request.getParameter("upper")), currUser);
				nextUrl = "/edit_tags";
			} else if ("insert".equals(action)) {
				/*
				 * insert a relation into the database
				 */
				relationManager.prepareStatements(conn);
				relationManager.insertRelations(relationManager.buildRelations(request.getParameter("lower"), request.getParameter("upper")), currUser);
				nextUrl = "/edit_tags";			
			}
		} catch(SQLException e) {
			log.fatal("could not update database ", e);
		} finally {
			if (conn != null) {try {conn.close(); } catch (SQLException e) {} conn = null;}
			relationManager.closeStatements();
		}

		response.sendRedirect(nextUrl);
	}

}
