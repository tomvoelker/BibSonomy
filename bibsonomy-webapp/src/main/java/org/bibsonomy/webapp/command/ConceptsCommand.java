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
	 * a list of concepts
	 */
	private int numConcepts = 0;
	
	public int getNumConcepts() {
		return this.numConcepts;
	}

	public void setNumConcepts(final int numConcepts) {
		this.numConcepts = numConcepts;
	}


	public List<Tag> getConceptList() {
		return this.getList();
	}

	public void setConceptList(final List<Tag> concepts) {
		this.setList(concepts);
	}
	
}
