package org.bibsonomy.scrapingservice.listener;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemPropertiesHelper implements ServletContextListener {

	private static final Log log = LogFactory.getLog(SystemPropertiesHelper.class);
	
	private static final String CUSTOM_PREFIX = "systemProperty.";

	@SuppressWarnings("unchecked")
	public void contextInitialized(final ServletContextEvent event) {
		
		log.info("initializing system properties");
		
		final ServletContext context = event.getServletContext();
		final Enumeration<String> params = context.getInitParameterNames();
		
		while (params.hasMoreElements()) {
			final String name = params.nextElement();
			final String value = context.getInitParameter(name);
			log.info("handling context param " + name + " = " + value);
			if (name.startsWith(CUSTOM_PREFIX)) {
				System.setProperty(name, value);
			}
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
	}
}
