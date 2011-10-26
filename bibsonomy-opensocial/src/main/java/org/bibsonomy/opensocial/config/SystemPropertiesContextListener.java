package org.bibsonomy.opensocial.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Workaround for passing BibSonomy's project.home configuration to Shindig:
 * write map certain properties to the system.properties 
 * @author fmi
 * @version $Id$
 */
public class SystemPropertiesContextListener implements ServletContextListener {
	
	private static final String SHINDIG_PORT = "shindig.port";
	private static final String SHINDIG_HOST = "shindig.host";

	private static final String PROJECT_HOME = "projectHome";
	
	private static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/project.properties";
	private static final String CONFIG_LOCATION = "config.location";
	
	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_PORT = "80";
	
	ServletContext context;

	public void contextInitialized(final ServletContextEvent event) {
		this.context = event.getServletContext();
		
		
		final String configLocation = context.getInitParameter(CONFIG_LOCATION);
		PropertiesFactoryBean propertyFactory = new PropertiesFactoryBean();
		
		String projectHome = null;
		try {
			URL defaultConfig = context.getResource(DEFAULT_CONFIG_LOCATION);
			Resource[] locations = {
					new UrlResource(defaultConfig),
					new FileSystemResource(configLocation)
					};
			
			propertyFactory.setLocations(locations);
			
			propertyFactory.afterPropertiesSet();
			projectHome = propertyFactory.getObject().getProperty("project.home");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String hostName = DEFAULT_HOST;
		String hostPort = DEFAULT_PORT;
		try {
			final URL url = new URL(projectHome);
			hostName = url.getHost();
			
			if (url.getPort()>0) {
				hostPort = Integer.toString(url.getPort());
			}
		} catch (final MalformedURLException e) {
		}
		
		System.setProperty(SHINDIG_HOST, hostName);
		System.setProperty(SHINDIG_PORT, hostPort);
	}
	

	public void contextDestroyed(final ServletContextEvent event) {
	}

}
