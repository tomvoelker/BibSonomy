/*
 * Created on 02.10.2007
 */
package org.bibsonomy.webapp.util.spring.controller.conversion;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class ServletRequestAttributeDataBinder extends ServletRequestDataBinder {

	public ServletRequestAttributeDataBinder(Object target) {
		super(target);
	}

	public ServletRequestAttributeDataBinder(Object target, String commandName) {
		super(target,commandName);
	}

	@Override
	public void bind(ServletRequest request) {
		MutablePropertyValues mpvs = new ServletRequestPropertyValues(request);
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			bindMultipartFiles(multipartRequest.getFileMap(), mpvs);
		}
		doBind(mpvs);
	}
	
	
}
