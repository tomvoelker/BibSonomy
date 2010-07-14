/*
 * Created on 27.08.2007
 */
package org.bibsonomy.webapp.util.spring.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * extends spring-mvc with the possibility to map request to controllers not
 * only based upon URIs but also HTTP-Methods. 
 * 
 * @author Jens Illig
 */
public class HTTPMethodHandlerMapping implements HandlerMapping {
	private Object onGet;
	private Object onPost;
	private Object onPut;
	private Object onDelete;
	
	@Override
	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		final String method = request.getMethod();
		final Object handler;
		if ("get".equals(method) == true) {
			handler = getOnGet();
		} else if ("post".equals(method) == true) {
			handler = getOnPost();
		} else if ("put".equals(method) == true) {
			handler = getOnPut();
		} else if ("delete".equals(method) == true) {
			handler = getOnDelete();
		} else {
			throw new UnsupportedOperationException(method);
		}
		return new HandlerExecutionChain(handler,null);
	}
	
	/**
	 * @return object (typically controller) that is mapped to delete requests
	 */
	public Object getOnDelete() {
		return this.onDelete;
	}

	/** 
	 * @param onDelete object (typically controller) that shall be mapped to delete requests
	 */
	public void setOnDelete(Object onDelete) {
		this.onDelete = onDelete;
	}

	/** 
	 * @return object (typically controller) that is mapped to get requests
	 */
	public Object getOnGet() {
		return this.onGet;
	}

	/**
	 * @param onGet object (typically controller) that shall be mapped to get request
	 */
	public void setOnGet(Object onGet) {
		this.onGet = onGet;
	}

	/** 
	 * @return object (typically controller) that is mapped to post requests
	 */
	public Object getOnPost() {
		return this.onPost;
	}

	/**
	 * @param onPost object (typically controller) that shall be mapped to post request
	 */
	public void setOnPost(Object onPost) {
		this.onPost = onPost;
	}

	/** 
	 * @return object (typically controller) that is mapped to put request
	 */
	public Object getOnPut() {
		return this.onPut;
	}

	/**
	 * @param onPut object (typically controller) that shall be mapped to put request
	 */
	public void setOnPut(Object onPut) {
		this.onPut = onPut;
	}
}
