package org.bibsonomy.webapp.command;

/**
 * Bean for User-Sites
 *
 * @author  Dominik Benz
 * @version $Id$
 */
public class UserResourceViewCommand extends TagResourceViewCommand {

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