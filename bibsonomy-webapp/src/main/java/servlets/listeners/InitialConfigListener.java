package servlets.listeners;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/** Listener that looks up some configuration variables when
 * the Web application is first loaded. Stores this
 * name in some servlet context attributes.
 * Various servlets and JSP pages will extract them
 * from that location.
 * 
 * by using this listener to set certain static (in the sense of the project)
 * variables, these variables can be accessed in every JSP by just writing
 * ${projectName}
 * for example
 * 
 */
public class InitialConfigListener implements ServletContextListener {
  
	private static final String DEFAULT_PROJECT_NAME = "BibSonomy";

	private static ServletContext servletContext = null;
	
	/** This method is called first! 
	 * Looks up the configuration variables, configures proxy.
	 * 
	 *	init parameters and puts them into the servlet context.
	 */
	public void contextInitialized(ServletContextEvent event) {
		servletContext = event.getServletContext();
		Enumeration<String> e = servletContext.getInitParameterNames();
		while (e.hasMoreElements()) {
			String initParamName = e.nextElement();
			servletContext.setAttribute(initParamName, servletContext.getInitParameter(initParamName));
		}
	}
	
	public void contextDestroyed(ServletContextEvent event) {}

	public static String getInitParam(String name) {
		return (String)servletContext.getAttribute(name);
	}

	
	/** Static method that returns the servlet context
	 * attribute named "projectName" if it is available.
	 * Returns a default value if the attribute is unavailable.
	 */
	public static String getProjectName () {
		String name = getInitParam("projectName");
		if (name == null) {
			name = DEFAULT_PROJECT_NAME;
		}
		return(name);
	}

	
	/** Static method that returns the servlet context
	 * attribute named "projectHome" if it is available.
	 * Returns an empty string if the attribute is
	 * unavailable.
	 */

	public static String getProjectHome () {
		String name = getInitParam("projectHome");
		if (name == null) {
			name = "";
		}
		return(name);
	}
	
}