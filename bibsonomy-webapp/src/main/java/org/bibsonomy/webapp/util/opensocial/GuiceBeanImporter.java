package org.bibsonomy.webapp.util.opensocial;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.ServletContext;

import org.apache.shindig.common.servlet.GuiceServletContextListener;
import org.springframework.web.context.ServletContextAware;

import com.google.inject.Injector;

/**
 * Class for making shindig beans available to the spring configuration
 * 
 * @author fei
 * @version $Id$
 */
public class GuiceBeanImporter implements ServletContextAware{
	
	/** the servlet context */
	private ServletContext servletContext;
	
	/** guice injector */
	private Injector injector;

	/** 
	 * called when all properties are configured
	 */
	public void init() {
		this.injector  = (Injector) this.servletContext.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);
	}
	
	/**
	 * try to get the referenced bean
	 * @throws ClassNotFoundException 
	 */
	public Object getGuiceBean(String className) throws ClassNotFoundException {
		if (present(injector)) {
			return (injector.getInstance(this.getClass().getClassLoader().loadClass(className)));
		} else {
			throw new RuntimeException("No guice injector found.");
		}
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
