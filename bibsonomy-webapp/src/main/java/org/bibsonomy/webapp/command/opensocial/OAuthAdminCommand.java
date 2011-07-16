package org.bibsonomy.webapp.command.opensocial;

import java.util.List;

import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;


/**
 * @author fei
 * @version $Id$
 */
public class OAuthAdminCommand extends OAuthCommand {
	public enum AdminAction { List, Register, Remove };
	
	private String adminAction;
	
	private List<OAuthConsumerInfo> consumers;
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	
	public void setAdminAction(String authorizeAction) {
		this.adminAction = authorizeAction;
	}

	public String getAdminAction() {
		return adminAction;
	}
	
	/**
	 * tmp getter until spring's enum binding works again
	 * @return
	 */
	public AdminAction getAdminAction_() {
		return this.adminAction == null ? null : AdminAction.valueOf(this.adminAction);
	}

	public void setConsumers(List<OAuthConsumerInfo> consumers) {
		this.consumers = consumers;
	}

	public List<OAuthConsumerInfo> getConsumers() {
		return consumers;
	}

}
