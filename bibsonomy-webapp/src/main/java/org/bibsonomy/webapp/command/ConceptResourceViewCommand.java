package org.bibsonomy.webapp.command;

/**
 * Bean for Concept Sites
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class ConceptResourceViewCommand extends TagResourceViewCommand{
	
	/** the user whose resources are requested */
	private String requestedUser = "";

	private ConceptsCommand concepts = new ConceptsCommand();
	/**
	 * @return requestedUser the name of the user whose resources are requested
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * @param requestedUser the name of the user whose resources are requested
	 */
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}
	
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
