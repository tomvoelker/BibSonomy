package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * Bean for related tags of a single tag or a list
 * of tags
 * 
 * @author Stefan Stuetzer
 * @version $Id$
 */
public class RelatedTagCommand extends BaseCommand {

	/** the requested tag(s) for whose to find related tags*/
	private String requestedTags;	
	
	/**  the related tags of the requested tag(s) */
	private List<Tag> relatedTags = new ArrayList<Tag>();
	
	/** the global count of the tag these tags are related to */
	private Integer tagGlobalCount = 1;
	
	public RelatedTagCommand() {}
	
	public RelatedTagCommand(List<Tag> relatedTags) {
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

	public Integer getTagGlobalCount() {
		return this.tagGlobalCount;
	}

	public void setTagGlobalCount(Integer tagGlobalCount) {
		this.tagGlobalCount = tagGlobalCount;
	}	
}
