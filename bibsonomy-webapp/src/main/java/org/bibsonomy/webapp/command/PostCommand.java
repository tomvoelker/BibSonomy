package org.bibsonomy.webapp.command;

/**
 * @author fba
 * @version $Id$
 */
public class PostCommand extends ResourceViewCommand {
	private ConceptsCommand concepts = new ConceptsCommand();

	/**
	 * @return the concepts
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts the concepts to set
	 */
	public void setConcepts(final ConceptsCommand concepts) {
		this.concepts = concepts;
	}
}
