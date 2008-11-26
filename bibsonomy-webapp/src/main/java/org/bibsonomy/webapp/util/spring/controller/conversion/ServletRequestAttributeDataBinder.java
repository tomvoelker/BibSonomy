/*
 * Created on 02.10.2007
 */
package org.bibsonomy.webapp.util.spring.controller.conversion;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * ServletRequestDataBinder subclass that uses {@link ServletRequestPropertyValues}
 * to also bind attributes and not only parameters.
 * Mostly copied from {@link ServletRequestDataBinder}
 * 
 * @author Jens Illig
 */
public class ServletRequestAttributeDataBinder extends ServletRequestDataBinder {

	/**
	 * @see ServletRequestDataBinder#ServletRequestDataBinder(Object)
	 * @param target target object to bind onto
	 */
	public ServletRequestAttributeDataBinder(Object target) {
		super(target);
	}

	/**
	 * @see ServletRequestDataBinder#ServletRequestDataBinder(Object, String)
	 * @param target target object to bind onto
	 * @param commandName objectName of the target object
	 */
	public ServletRequestAttributeDataBinder(Object target, String commandName) {
		super(target,commandName);
	}

	@Override
	public void bind(ServletRequest request) {
		final MutablePropertyValues mpvs = new ServletRequestPropertyValues(request);
		if (request instanceof MultipartHttpServletRequest) {
			final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			bindMultipartFiles(multipartRequest.getFileMap(), mpvs);
		}
		doBind(mpvs);
	}
	
	
}
