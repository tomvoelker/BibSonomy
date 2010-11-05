package org.bibsonomy.webapp.util.spring.factorybeans;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.springframework.beans.factory.FactoryBean;

/**
 * Lets {@link DBLogicUserInterfaceFactory} appear as a FactoryBean, which itself
 * is customizable in the spring context.
 * 
 * This bean returns an implementation of the LogicInterface which has admin
 * access enabled. This is done by giving it a user which has the Role "admin".
 *  
 * @see FactoryBean
 * @see Role
 * @author rja
 * @version $Id$
 */
public class AdminLogicFactoryBean extends DBLogicUserInterfaceFactory implements FactoryBean<LogicInterface> {

	private final User user;
	private LogicInterface instance = null;
	
	/**
	 * Creates a new instance of the AdminLogicFactoryBean.
	 * 
	 */
	public AdminLogicFactoryBean() {
		this.user = new User();
		user.setRole(Role.ADMIN);
	}
	
	@Override
	public LogicInterface getObject() throws Exception {
		if (instance == null) {
			instance = this.getLogicAccess(user.getName(), "");
		}
		return instance;
	}
	
	@Override
	protected User getLoggedInUser(String loginName, String password) {
		// return the admin user
		return user;
	}

	@Override
	public Class<?> getObjectType() {
		return LogicInterface.class;
	}

	@Override
	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}
	
	/** Set the name the admin user will have.
	 * 
	 * @param adminUserName
	 */
	public void setAdminUserName(final String adminUserName) {
		this.user.setName(adminUserName);
	}
	
	/** Get the name of the admin user.
	 * 
	 * @return The name of the admin user.
	 */
	public String getAdminUserName() {
		return this.user.getName();
	}
	
	
}
