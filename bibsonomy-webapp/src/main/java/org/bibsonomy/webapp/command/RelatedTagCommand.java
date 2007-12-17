package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Tag;

/**
 * Bean for related tags of a single tag or a list
 * of tags
 * 
 * @version: $Id$
 * @author:  Stefan Stuetzer
 * $Author$
 *
 */
public class RelatedTagCommand extends BaseCommand {
	private static final Logger LOGGER = Logger.getLogger(RelatedTagCommand.class);
		
	/** the requested tag(s) for whose to find related tags*/
	String requestedTags;	
	
	/**  the related tags of the requested tag(s) */
	List<Tag> relatedTags = new ArrayList<Tag>();
	
	public RelatedTagCommand() {}
	
	RelatedTagCommand(List<Tag> relatedTags) {
		this.relatedTags = relatedTags;
	}	

	public String getRequestedTags() {
		return this.requestedTags;
	}

	public void setRequestedTags(String requestedTags) {
		this.requestedTags = requestedTags;
	}

	public List<Tag> getRelatedTags() {
		return this.relatedTags;
	}

	public void setRelatedTags(List<Tag> relatedTags) {
		this.relatedTags = relatedTags;
	}	
}