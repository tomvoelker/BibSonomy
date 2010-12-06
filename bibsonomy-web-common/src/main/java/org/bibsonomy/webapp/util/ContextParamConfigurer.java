package org.bibsonomy.webapp.util;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Constants;
import org.springframework.web.context.ServletContextAware;

/**
 * <p>Spring's {@link PropertyPlaceholderConfigurer} allows us to override 
 * properties from a properties file using system environment entries and
 * system properties. This class extends this functionality to use servlet
 * context parameters to override properties. 
 * </p>
 * 
 * <p>Thus, an entry like 
 * <pre>
 * &lt;Parameter name="projectName" value="Biblicious" type="java.lang.String" override="false"/&gt;
 * </pre>
 * in the <tt>context.xml</tt> of the servlet container will override the 
 * <tt>projectName</tt> property resolved by the {@link PropertyPlaceholderConfigurer}.
 * </p>
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class ContextParamConfigurer extends PropertyPlaceholderConfigurer implements ServletContextAware {

	/**
	 * To resolve context parameters.
	 */
	private ServletContext servletContext;
	
	/**
	 * Context params override everything else (see {@link PropertyPlaceholderConfigurer}.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_CONTEXTPARAM = 3;

	/**
	 * Allows users to use constant string names in bean config instead of 
	 * integers.
	 */
	private static final Constants constants = new Constants(ContextParamConfigurer.class);

	
	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext= servletContext;
	}
	
	/** 
	 * If system properties mode is set to {@link #SYSTEM_PROPERTIES_MODE_CONTEXTPARAM}, 
	 * the placeholder is also looked for in the servlet context 
	 * (using {@link ServletContext#getInitParameter(String)}).
	 * 
	 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#resolvePlaceholder(java.lang.String, java.util.Properties, int)
	 */
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		/*
		 * let Spring resolve the value
		 */
		String propVal = super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_CONTEXTPARAM) {
			final String initParameter = servletContext.getInitParameter(placeholder);
			if (initParameter != null) {
				propVal = initParameter;
			}
		}
		return propVal;
	}
	
	/**
	 * Set the system property mode by the name of the corresponding constant,
	 * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
	 * @param constantName name of the constant
	 * @throws java.lang.IllegalArgumentException if an invalid constant was specified
	 * @see PropertyPlaceholderConfigurer#setSystemPropertiesMode
	 */
	@Override
	public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
		super.setSystemPropertiesMode(constants.asNumber(constantName).intValue());
	}

}
