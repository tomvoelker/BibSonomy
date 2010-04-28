package org.bibsonomy.webapp.command;

/**
 * Bean for Concept Sites
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class ConceptResourceViewCommand extends TagResourceViewCommand {
	
	/** the user whose resources are requested */
	// TODO: duplicate field @see ResourceViewCommand
	private String requestedUser = "";
	
	/** the group which resources are requested */
	private String requestedGroup = "";

	/** bean for concepts */
	private ConceptsCommand concepts = new ConceptsCommand();
	
	/**
	 * @return requestedUser the name of the user whose resources are requested
	 */
	@Override
	public String getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * @param requestedUser the name of the user whose resources are requested
	 */
	@Override
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}
	
	/**	
	 * @return the name of the group that resources are requested
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the group
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the concepts
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