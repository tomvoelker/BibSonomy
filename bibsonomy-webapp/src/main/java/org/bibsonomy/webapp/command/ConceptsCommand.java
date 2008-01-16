package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Tag;

/**
 * Bean for tag relations
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class ConceptsCommand extends BaseCommand {
		
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

	public ConceptsCommand() {}

	public List<Tag> getConceptList() {
		return this.conceptList;
	}

	public void setConceptList(List<Tag> concepts) {
		this.conceptList = concepts;
	}
	
}
