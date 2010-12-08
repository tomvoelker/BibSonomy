package org.bibsonomy.webapp.util.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.spring.controller.MinimalisticControllerSpringWrapper;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * Intercepts the request and creates a {@link RequestWrapperContext}. 
 * The context acts as a proxy to the request and is put into the request.
 * {@link MinimalisticControllerSpringWrapper} then can extract the 
 * context from the request and put it into the command.
 * </p>
 * <p>
 * TODO: it would be nice, if this wrapping wouldn't be neccessary.
 * </p>
 * 
 * @author rja
 * @version $Id$
 */
public class RequestWrapperContextHandlerInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// nothing to do
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// nothing to do
	}

	/** Puts a {@link RequestWrapperContext} as attribute into the request. 
	 * The context acts as a proxy for the request. The name of the Attribute
	 * is {@link org.bibsonomy.webapp.util.RequestWrapperContext}.
	 * 
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		/*
		 * create context and populate it with the request
		 */
		final RequestWrapperContext context = new RequestWrapperContext();
		context.setRequest(request);
		/*
		 * put context into request
		 */
		request.setAttribute(RequestWrapperContext.class.getName(), context);
		/*
		 * always return true - otherwise request handling would be aborted.
		 */
		return true;
	}

}
