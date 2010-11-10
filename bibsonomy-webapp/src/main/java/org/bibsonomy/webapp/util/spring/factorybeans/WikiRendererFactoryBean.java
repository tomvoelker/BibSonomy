package org.bibsonomy.webapp.util.spring.factorybeans;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.wiki.WikiUtil;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author philipp
 * @version $Id$
 */
public class WikiRendererFactoryBean implements FactoryBean {

	private User user;
	private LogicInterface logic;
	private WikiUtil wikiRenderer;
	
	@Override
	public Object getObject() throws Exception {
		if(!present(wikiRenderer)) {
			wikiRenderer = new WikiUtil();
			wikiRenderer.setLogic(logic);
			wikiRenderer.setUser(user);
		}
		return wikiRenderer;
	}

	@Override
	public Class<?> getObjectType() {
		return WikiUtil.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the logic
	 */
	public LogicInterface getLogic() {
		return logic;
	}

}
