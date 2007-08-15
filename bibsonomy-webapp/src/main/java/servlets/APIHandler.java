package servlets;

import java.beans.XMLEncoder;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import beans.UserBean;
import filters.SessionSettingsFilter;

public class APIHandler extends HttpServlet{ 

	private static final Logger log = Logger.getLogger(BookmarkHandler.class);
	private static final long serialVersionUID = 3839748679655351876L;
	private DataSource dataSource;

	private static final int DIM_TAG = 0;
	private static final int DIM_USR = 1;
	private static final int DIM_RES = 2;

	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource   = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} catch (NamingException ex){
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		UserBean currUser = SessionSettingsFilter.getUser(request);
		if (currUser.getName() == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication required.");
			return;
		}

		/* Establish all connections, result sets and statements */
		Connection conn    		= null;
		ResultSet rst 			= null;
		PreparedStatement stmt = null;

		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}

			/*
			 * debug
			 */
			System.out.println("QUERY = " + request.getQueryString());

			String entity = request.getParameter("entity");

			if ("community".equals(entity)) {
				String user = request.getParameter("user");
				int items;
				try {
					items = Integer.parseInt(request.getParameter("items"));	
				} catch (NumberFormatException e) {
					items = Integer.MAX_VALUE;
				}


				stmt = conn.prepareStatement("select w.* from weights w JOIN rankings r USING (id) WHERE r.item = ? AND r.dim = " + DIM_USR + " AND w.dim = ? ORDER BY weight DESC LIMIT ?");

				/*
				 * get users
				 */
				stmt.setString(1, user);
				stmt.setInt(2, DIM_USR);
				stmt.setInt(3, items);
				rst = stmt.executeQuery();

				List<String> community = new LinkedList<String>();
				while (rst.next()) {
					community.add("http://www.bibsonomy.org/user/" + URLEncoder.encode(rst.getString("item"), "UTF-8"));
				}

				/*
				 * return result
				 */
				response.setContentType("application/xml");
				response.setCharacterEncoding("UTF-8");
				XMLEncoder encoder = new XMLEncoder(response.getOutputStream());
				encoder.writeObject(community);
				encoder.close();
				return;

			}

			response.sendError(HttpServletResponse.SC_NO_CONTENT);

		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error accessing the database.");
		} finally {
			if(rst  != null) {try {rst.close();  } catch (SQLException e) { } rst   = null;}
			if(stmt!= null) {try {stmt.close();} catch (SQLException e) { } stmt = null;}
			if(conn != null) {try {conn.close(); } catch (SQLException e) {	}conn  = null;}
		}

	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}





}