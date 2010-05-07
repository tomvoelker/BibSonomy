package servlets;
import helpers.database.DBStatisticsManager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;

import filters.InitUserFilter;


/**
 * This servlet is used to handle update requests for DBLP and to 
 * gather statistics for the admin_statistics.jsp page
 *
 */
public class AdminHandler extends HttpServlet {
	
	private static final long serialVersionUID = 3691036578076309554L;
	private static Set<String> allowedUsers = null;
	private static final Log log = LogFactory.getLog(AdminHandler.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException{
	
		
		super.init(config);
		allowedUsers = new HashSet<String>();
		allowedUsers.add("jaeschke");
		allowedUsers.add("hotho");
		allowedUsers.add("stumme");
		allowedUsers.add("schmitz");
		allowedUsers.add("grahl");
		allowedUsers.add("dbenz");
		allowedUsers.add("beate");
	}	
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * check user name (only admins are allowed)
		 */
		User user = InitUserFilter.getUser(request);
		String userName = user.getName();

		if (!(allowedUsers.contains(userName))) {
			response.sendRedirect("/login");
			return;
		}

		
		if ("ajax".equals(request.getParameter("action"))) {
			/*
			 * extract method name
			 */
			final String var = request.getParameter("var");
			final String stats = "stats.";
			final int length = stats.length();

			if (var.startsWith(stats)) {
				/*
				 * construct method name
				 */
				String op = "get" + var.substring(length, length + 1).toUpperCase() + var.substring(length + 1);
				try {
					/*
					 * get and invoke method
					 */
					Method meth = DBStatisticsManager.class.getMethod(op, new Class[]{String.class});
					final Integer result = (Integer)meth.invoke(DBStatisticsManager.class, request.getParameter("spammer"));
					/*
					 * write result
					 */
					OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
					writer.write(result.toString());
					writer.close();
				} catch (SecurityException e) {
					log.fatal(e);
				} catch (NoSuchMethodException e) {
					log.fatal(e);
				} catch (IllegalArgumentException e) {
					log.fatal(e);
				} catch (IllegalAccessException e) {
					log.fatal(e);
				} catch (InvocationTargetException e) {
					log.fatal(e);
				}
			}
		}
	}

}