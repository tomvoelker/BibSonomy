package org.bibsonomy.opensocial.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * XXX: Workaround for passing BibSonomy's project.home configuration to
 * Shindig: write map certain properties to the system.properties
 * 
 * @author fmi
 */
public class SystemPropertiesContextListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(SystemPropertiesContextListener.class);

	private static final String SHINDIG_PORT = "shindig.port";
	private static final String SHINDIG_HOST = "shindig.host";

	private static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/project.properties";
	private static final String CONFIG_LOCATION = "config.location";

	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_PORT = "80";

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		String hostName = DEFAULT_HOST;
		String hostPort = DEFAULT_PORT;
		try {
			/*
			 * here we build the properties from the properties file in the
			 * webapp module and the (optional) override file by hand (the
			 * webapp is doing this later again @see bibsonomy2-servlet.xml)
			 */

			/*
			 * first get the location of the optional override file
			 */
			final ServletContext context = event.getServletContext();
			final String configLocation = context.getInitParameter(CONFIG_LOCATION);

			/*
			 * and build the properties by hand
			 */
			final PropertiesFactoryBean propertyFactory = new PropertiesFactoryBean();
			final URL defaultConfig = context.getResource(DEFAULT_CONFIG_LOCATION);
			final FileSystemResource overrideFile = new FileSystemResource(configLocation);
			final List<Resource> locations = new LinkedList<Resource>();
			locations.add(new UrlResource(defaultConfig));

			/*
			 * only use override file if it exists
			 */
			if (overrideFile.exists()) {
				locations.add(overrideFile);
			}

			propertyFactory.setLocations(locations.toArray(new Resource[locations.size()]));
			propertyFactory.afterPropertiesSet();

			Properties p = propertyFactory.getObject();
			// TODO: This looks not very nice.
			addSystemProperties(p, "database.opensocial.url", "database.opensocial.driverClassName", "database.opensocial.maxIdle", "database.opensocial.maxActive", "database.opensocial.maxWait", "database.opensocial.removeAbandoned", "database.opensocial.removeAbandonedTimeout", "database.opensocial.username", "database.opensocial.password");
			
			final String projectHome = p.getProperty("project.home");
			/*
			 * get host and port name from projectHome
			 */
			try {
				final URL url = new URL(projectHome);
				hostName = url.getHost();

				if (url.getPort() > 0) {
					hostPort = Integer.toString(url.getPort());
				}
			} catch (final MalformedURLException e) {
				throw new RuntimeException(e);
			}
		} catch (final IOException e1) {
			log.warn("error while config of opensocial; using default values", e1);
		}

		System.setProperty(SHINDIG_HOST, hostName);
		System.setProperty(SHINDIG_PORT, hostPort);
	}

	/**
	 * Adds the given keys to the system`s properties. This method also resolves
	 * level one references.
	 * 
	 * @param p
	 * @param string
	 */
	private static void addSystemProperties(Properties p, String... keys) {
		// loop through the given keys
		for (String string : keys) {
			String value = p.getProperty(string);
			if (value == null) return;
			// check for placeholder
			if (value.startsWith(PropertySourcesPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX) && value.endsWith(PropertySourcesPlaceholderConfigurer.DEFAULT_PLACEHOLDER_SUFFIX)) {
				// try and resolve the reference
				String reference = value.replaceAll("(\\$\\{|\\})", "");
				System.setProperty(string, p.getProperty(reference));
			} else {
				System.setProperty(string, p.getProperty(string));
			}
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		// noop
	}

}
