/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.springframework.beans.factory.FactoryBean;

import beans.UserBean;
import filters.InitUserFilter;

/**
 * lets {@link DBLogicUserInterfaceFactory} appear as a FactoryBean, which itself
 * is customizable in the spring context.
 *  
 * @see FactoryBean
 * @author Jens Illig
 */
public class PostLogicFactoryBean extends DBLogicUserInterfaceFactory implements FactoryBean {
	private HttpServletRequest request;
	private PostLogicInterface instance;
	
	/**
	 * @param request
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	public Object getObject() throws Exception {
		if (instance == null) {
			final UserBean userKeyData = getLoginUser(request);
			instance = this.getLogicAccess(userKeyData.getName(), "");
		}
		return instance;
	}
	
	@Override
	protected boolean isValidLogin(String loginName, String password) {
		// can always return true because user object attribute in request is always valid;
		return true;
	}

	/**
	 * @param req
	 * @return the user that is logged in
	 */
	protected UserBean getLoginUser(@SuppressWarnings("unused")	final HttpServletRequest req) {
		// FIXME: IoC break: use user object instead of accessing request
		// FIXME: use bibsonomy2 user object and check password again
		return (UserBean) this.request.getAttribute(InitUserFilter.REQ_ATTRIB_USER); 
	}

	public Class<?> getObjectType() {
		return PostLogicInterface.class;
	}

	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

}
