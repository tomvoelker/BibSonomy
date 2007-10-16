/*
 * Created on 27.08.2007
 */
package org.bibsonomy.webapp.util.spring.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

public class HTTPMethodHandlerMapping implements HandlerMapping {
	private Object onGet;
	private Object onPost;
	private Object onPut;
	private Object onDelete;
	
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

	public Object getOnDelete() {
		return this.onDelete;
	}

	public void setOnDelete(Object onDelete) {
		this.onDelete = onDelete;
	}

	public Object getOnGet() {
		return this.onGet;
	}

	public void setOnGet(Object onGet) {
		this.onGet = onGet;
	}

	public Object getOnPost() {
		return this.onPost;
	}

	public void setOnPost(Object onPost) {
		this.onPost = onPost;
	}

	public Object getOnPut() {
		return this.onPut;
	}

	public void setOnPut(Object onPut) {
		this.onPut = onPut;
	}

	
	
}
