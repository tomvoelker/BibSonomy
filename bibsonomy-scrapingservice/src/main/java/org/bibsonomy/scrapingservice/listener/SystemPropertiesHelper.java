package org.bibsonomy.scrapingservice.listener;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SystemPropertiesHelper implements ServletContextListener {

	private static final String CUSTOM_PREFIX = "systemProperty.";

	@SuppressWarnings("unchecked")
	public void contextInitialized(final ServletContextEvent event) {
		final ServletContext context = event.getServletContext();
		final Enumeration<String> params = context.getInitParameterNames();

		while (params.hasMoreElements()) {
			final String param = params.nextElement();
			final String value = context.getInitParameter(param);
			if (param.startsWith(CUSTOM_PREFIX)) {
				System.setProperty(param, value);
			}
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
	}
}
