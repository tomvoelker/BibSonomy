package org.bibsonomy.opensocial.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.shindig.common.util.ResourceLoader;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

/**
 * Injects everything from the shindig property file as well as BibSonomy's project.properties
 */
public class BibSonomyPropertiesModule extends AbstractModule {

	private final static String SHINDIG_PROPERTIES   = "shindig.properties";
	
	private final Properties shindigProperties;

	public BibSonomyPropertiesModule() {
		super();
		this.shindigProperties   = readPropertyFile(SHINDIG_PROPERTIES);
	}

	public BibSonomyPropertiesModule(String shindigPropertyFile) {
		this.shindigProperties   = readPropertyFile(shindigPropertyFile);
	}

	public BibSonomyPropertiesModule(Properties shindigProperties) {
		this.shindigProperties   = shindigProperties;
	}

	@Override
	protected void configure() {
		Names.bindProperties(this.binder(), this.shindigProperties);
		String hostname = getServerHostname();
	}
	  protected String getServerHostname() {
		    return System.getProperty("shindig.host") != null ? System.getProperty("shindig.host") :
		           System.getProperty("jetty.host") != null ? System.getProperty("jetty.host") :
		           "localhost";
		  }
	private Properties readPropertyFile(String propertyFile) {
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = ResourceLoader.openResource(propertyFile);
			properties.load(is);
		} catch (IOException e) {
			throw new CreationException(Arrays.asList(
					new Message("Unable to load properties: " + propertyFile)));
		} finally {
			IOUtils.closeQuietly( is );
		}

		return properties;
	}

}
