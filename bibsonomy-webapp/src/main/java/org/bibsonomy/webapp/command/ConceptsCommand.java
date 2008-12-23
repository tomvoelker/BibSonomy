package org.bibsonomy.webapp.command;

import java.util.ArrayList;
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
	public ConceptsCommand(ContextCommand parentCommand) {
		super(parentCommand);
		// TODO Auto-generated constructor stub
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
	private List<Tag> conceptList = new ArrayList<Tag>();
	
	private int numConcepts = 0;
	
	public int getNumConcepts() {
		return this.numConcepts;
	}

	public void setNumConcepts(int numConcepts) {
		this.numConcepts = numConcepts;
	}

//	public ConceptsCommand() {}

	public List<Tag> getConceptList() {
		return this.conceptList;
	}

	public void setConceptList(List<Tag> concepts) {
		this.conceptList = concepts;
		setList(concepts);
	}
	
}
