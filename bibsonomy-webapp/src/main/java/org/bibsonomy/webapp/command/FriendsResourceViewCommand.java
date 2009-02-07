package org.bibsonomy.webapp.command;

/**
 * @author Steffen
 * @version $Id$
 */
public class FriendsResourceViewCommand extends TagResourceViewCommand {
	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();
	
	/**
	 * @return
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
	
}
