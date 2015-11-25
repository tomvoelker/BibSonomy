/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.spring.controller;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Allows to use regular Servlets inside a Spring bean config.
 * 
 * Similar to {@link org.springframework.web.servlet.mvc.ServletWrappingController}
 * but allows to directly provide an instance of the servlet. 
 * 
 * @author rja
 */
public class ServletWrappingController extends AbstractController implements BeanNameAware, InitializingBean, DisposableBean {

	private String servletName;

	private Properties initParameters = new Properties();

	private String beanName;

	private Servlet servletInstance;


	/**
	 * Set the name of the servlet to wrap.
	 * Default is the bean name of this controller.
	 * @param servletName 
	 */
	public void setServletName(final String servletName) {
		this.servletName = servletName;
	}

	/**
	 * Specify init parameters for the servlet to wrap,
	 * as name-value pairs.
	 * @param initParameters 
	 */
	public void setInitParameters(final Properties initParameters) {
		this.initParameters = initParameters;
	}

	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(final String name) {
		this.beanName = name;
	}


	/**
	 * Initialize the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.servletName == null) {
			this.servletName = this.beanName;
		}
		if (this.servletInstance == null) {
			throw new IllegalArgumentException("servletInstance is required");
		}
		this.servletInstance.init(new DelegatingServletConfig());
	}


	/**
	 * Invoke the the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
		throws Exception {

		this.servletInstance.service(request, response);
		return null;
	}


	/**
	 * Destroy the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		this.servletInstance.destroy();
	}


	/**
	 * Internal implementation of the ServletConfig interface, to be passed
	 * to the wrapped servlet. Delegates to ServletWrappingController fields
	 * and methods to provide init parameters and other environment info.
	 */
	private class DelegatingServletConfig implements ServletConfig {

		@Override
		public String getServletName() {
			return servletName;
		}

		@Override
		public ServletContext getServletContext() {
			return ServletWrappingController.this.getServletContext();
		}

		@Override
		public String getInitParameter(final String paramName) {
			return initParameters.getProperty(paramName);
		}
		
		@SuppressWarnings( { "unchecked", "rawtypes" }) // XXX: to be compatible with servlet-api 2 and 3
		@Override
		public Enumeration getInitParameterNames() {
			return initParameters.keys();
		}
	}

	/**
	 * Provides an instance of the servlet this class should dispatch to.
	 * @param servletInstance
	 */
	@Required
	public void setServletInstance(final Servlet servletInstance) {
		this.servletInstance = servletInstance;
	}

}
