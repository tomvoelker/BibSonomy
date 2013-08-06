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
