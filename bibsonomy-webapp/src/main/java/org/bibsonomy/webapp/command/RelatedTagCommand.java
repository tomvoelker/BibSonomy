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

	/**
	 * @return the requestedTags
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}

	/**
	 * @param requestedTags the requestedTags to set
	 */
	public void setRequestedTags(String requestedTags) {
		this.requestedTags = requestedTags;
	}

	/**
	 * @return the relatedTags
	 */
	public List<Tag> getRelatedTags() {
		return this.relatedTags;
	}

	/**
	 * @param relatedTags the relatedTags to set
	 */
	public void setRelatedTags(List<Tag> relatedTags) {
		this.relatedTags = relatedTags;
	}

	/**
	 * @return the tagGlobalCount
	 */
	public Integer getTagGlobalCount() {
		return this.tagGlobalCount;
	}

	/**
	 * @param tagGlobalCount the tagGlobalCount to set
	 */
	public void setTagGlobalCount(Integer tagGlobalCount) {
		this.tagGlobalCount = tagGlobalCount;
	}
}
