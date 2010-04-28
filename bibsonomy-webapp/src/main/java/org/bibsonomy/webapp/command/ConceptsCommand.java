package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * Bean for tag relations
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class ConceptsCommand extends ListCommand<Tag> {
	/**
	 * a list of concepts
	 */
	private int numConcepts = 0;

	/**
	 * constructor used by the UserRelationCommand
	 * @param parentCommand
	 */
	public ConceptsCommand(final ContextCommand parentCommand) {
		super(parentCommand);
	}
	
	/**
	 * default constructor
	 */
	public ConceptsCommand() {
		super(new BaseCommand());
	}

	/**
	 * @return the numConcepts
	 */
	public int getNumConcepts() {
		return this.numConcepts;
	}

	/**
	 * @param numConcepts the numConcepts to set
	 */
	public void setNumConcepts(int numConcepts) {
		this.numConcepts = numConcepts;
	}

	/**
	 * @return the list of concepts
	 */
	public List<Tag> getConceptList() {
		return this.getList();
	}

	/**
	 * @param concepts the list of concepts to set
	 */
	public void setConceptList(final List<Tag> concepts) {
		this.setList(concepts);
	}
	
}
