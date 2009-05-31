package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class RelationsCommand extends ResourceViewCommand{
	
	private List<Tag> tagRelations;

	/**
	 * @return List<Tag>
	 */
	public List<Tag> getTagRelations() {
		return this.tagRelations;
	}

	/**
	 * @param tagRelations
	 */
	public void setTagRelations(final List<Tag> tagRelations) {
		this.tagRelations = tagRelations;
	}
	
	

}
