/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.webapp.util.spring.controller.MinimalisticControllerSpringWrapper;
import org.springframework.beans.factory.FactoryBean;

/**
 * FactoryBean that fishes out the request, which is somehow concealed by spring.
 * This works by configuring a request scoped Holder in the dispatcher-servlet's
 * ApplicationContext and initializing the holders value at
 * {@link MinimalisticControllerSpringWrapper} runtime. So this only works with
 * lazy initializing.
 * 
 * @see FactoryBean
 * @author Jens Illig
 */
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

	/**
	 * @param holder the holder, where the request will be put in when
	 *               {@link MinimalisticControllerSpringWrapper} runs
	 */
	public void setRequestHolder(Holder<HttpServletRequest> holder) {
		this.holder = holder;
	}
}
