package org.bibsonomy.opensocial.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.bibsonomy.util.ValidationUtils;

/**
 * Workaround for passing BibSonomy's project.home configuration to Shindig:
 * write map certain properties to the system.properties 
 */
public class SystemPropertiesContextListener implements ServletContextListener {
	
	private static final String SHINDIG_PORT = "shindig.port";
	private static final String SHINDIG_HOST = "shindig.host";

	private static final String PROJECT_HOME = "projectHome";
	
	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_PORT = "80";
	
	ServletContext context;

	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();
		
		String projectHome = context.getInitParameter(PROJECT_HOME);
		
		String hostName = DEFAULT_HOST;
		String hostPort = DEFAULT_PORT;
		try {
			URL url = new URL(projectHome);
			hostName = url.getHost();
			
			if (url.getPort()>0) {
				hostPort = Integer.toString(url.getPort());
			}
		} catch (MalformedURLException e) {
		}
		
		System.setProperty(SHINDIG_HOST, hostName);
		System.setProperty(SHINDIG_PORT, hostPort);
	}
	

	public void contextDestroyed(ServletContextEvent event) {
	}

}
