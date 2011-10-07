package org.bibsonomy.webapp.command.ajax;

import java.util.List;


/**
 * @author Christian Kramer, Folke Mitzlaff
 * @version $Id$
 */
public class UserRelationAjaxCommand extends AjaxCommand {
	/**
	 * name of the requested user
	 */
	private String requestedUserName;
	
	/**
	 * list of requested relation names
	 */
	private List<String> relationTags;

	/**
	 * 
	 * @return requested username
	 */
	public String getRequestedUserName() {
		return this.requestedUserName;
	}

	/**
	 * @param userName
	 */
	public void setRequestedUserName(String userName) {
		this.requestedUserName = userName;
	}

	/**
	 * @param relationTags
	 */
	public void setRelationTags(List<String> relationTags) {
		this.relationTags = relationTags;
	}

	/**
	 * @return relation tags
	 */
	public List<String> getRelationTags() {
		return relationTags;
	}
}
