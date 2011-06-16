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
	private static ServletContext servletContext = null;
	
	/** This method is called first! 
	 * Looks up the configuration variables, configures proxy.
	 * 
	 *	init parameters and puts them into the servlet context.
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) {
		servletContext = event.getServletContext();
		@SuppressWarnings("unchecked")
		final Enumeration<String> e = servletContext.getInitParameterNames();
		while (e.hasMoreElements()) {
			final String initParamName = e.nextElement();
			servletContext.setAttribute(initParamName, servletContext.getInitParameter(initParamName));
		}
	}
	
	@Override
	public void contextDestroyed(final ServletContextEvent event) {}

	public static String getInitParam(final String name) {
		return (String) servletContext.getAttribute(name);
	}
}