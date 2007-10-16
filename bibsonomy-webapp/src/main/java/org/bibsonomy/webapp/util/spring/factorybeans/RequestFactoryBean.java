/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestFactoryBean implements FactoryBean {
	private HttpServletRequest request;
	private Holder<HttpServletRequest> holder;

	public Object getObject() throws Exception {
		if (request == null) {
			request = holder.getObj();
			if (request == null) {
				throw new IllegalStateException("request still not set");
			}
		}
		return request;
	}

	public Class<?> getObjectType() {
		return HttpServletRequest.class;
	}

	public boolean isSingleton() {
		return true;   // TODO: check if singleton is really only singleton in the scope of the factorybean
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setRequestHolder(Holder<HttpServletRequest> holder) {
		this.holder = holder;
	}
}
