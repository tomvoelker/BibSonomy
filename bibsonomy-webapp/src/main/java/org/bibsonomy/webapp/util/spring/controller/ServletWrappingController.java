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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Allows to use regular Servlets inside a Spring bean config.
 * 
 * Similar to {@link org.springframework.web.servlet.mvc.ServletWrappingController}
 * but allows to directly provide an instance of the servlet. 
 * 
 * @author rja
 *
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
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	/**
	 * Specify init parameters for the servlet to wrap,
	 * as name-value pairs.
	 * @param initParameters 
	 */
	public void setInitParameters(Properties initParameters) {
		this.initParameters = initParameters;
	}

	public void setBeanName(String name) {
		this.beanName = name;
	}


	/**
	 * Initialize the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
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
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		this.servletInstance.service(request, response);
		return null;
	}


	/**
	 * Destroy the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		this.servletInstance.destroy();
	}


	/**
	 * Internal implementation of the ServletConfig interface, to be passed
	 * to the wrapped servlet. Delegates to ServletWrappingController fields
	 * and methods to provide init parameters and other environment info.
	 */
	private class DelegatingServletConfig implements ServletConfig {

		public String getServletName() {
			return servletName;
		}

		public ServletContext getServletContext() {
			return ServletWrappingController.this.getServletContext();
		}

		public String getInitParameter(String paramName) {
			return initParameters.getProperty(paramName);
		}

		public Enumeration<?> getInitParameterNames() {
			return initParameters.keys();
		}
	}


	public void setServletInstance(Servlet servletInstance) {
		this.servletInstance = servletInstance;
	}

}
