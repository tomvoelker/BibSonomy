package org.bibsonomy.webapp.util.spring.factorybeans;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.RequestLogic;
import org.springframework.beans.factory.FactoryBean;

/**
 * fishes the {@link User} out of the request
 *  
 * @see FactoryBean
 * @author Dominik Benz
 * @version $Id$
 */
public class UserFactoryBean implements FactoryBean<User> {
	private RequestLogic requestLogic;
	private User instance;
	
	/**
	 * The logic to acces the HTTP servlet request.
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public User getObject() throws Exception {
		if (instance == null) {
			instance = requestLogic.getLoginUser();
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return User.class;
	}
	
	@Override
	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

}
