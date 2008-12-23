package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/**
 * @author Christian Voigtmann
 * @version $Id$
 */
public class UserRelationCommand extends ResourceViewCommand {

	private User user;

	/** the group whode resources are requested */
	private ConceptsCommand concepts = null;


	/**
	 * 
	 */
	public UserRelationCommand() {
		concepts = new ConceptsCommand(this);
	}
	
	/**
	 * @return the concept
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}
	
	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

}
