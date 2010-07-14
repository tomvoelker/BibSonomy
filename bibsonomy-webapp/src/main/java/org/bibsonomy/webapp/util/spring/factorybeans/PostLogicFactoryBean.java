/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.springframework.beans.factory.FactoryBean;

/**
 * lets {@link DBLogicUserInterfaceFactory} appear as a FactoryBean, which itself
 * is customizable in the spring context.
 *  
 * @see FactoryBean
 * @author Jens Illig
 */
public class PostLogicFactoryBean extends DBLogicUserInterfaceFactory implements FactoryBean {

	private User user;
	private PostLogicInterface instance;
	
	@Override
	public Object getObject() throws Exception {
		if (instance == null) {
			instance = this.getLogicAccess(user.getName(), "");
		}
		return instance;
	}
	
	@Override
	protected User getLoggedInUser(String loginName, String password) {
		// can always return true because user object attribute in request is always valid;
		return user;
	}

	@Override
	public Class<?> getObjectType() {
		return PostLogicInterface.class;
	}

	@Override
	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user
	 */
	public void setUser(User user) {
		this.user = user;
	}

}
