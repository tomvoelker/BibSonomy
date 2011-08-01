package servlets.utils;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

/**
 * TODO: remove after all sites are ported to the spring mvc
 * 
 * @author dzo
 * @version $Id$
 */
@Deprecated
public class ProperiesAttributeSetter implements ServletContextAware {
	
	private ServletContext context;
	private Properties properties;

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.context = servletContext;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(final Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * sets the properties as context attribute
	 */
	public void init() {
		this.context.setAttribute("properties", this.properties);
	}
}
