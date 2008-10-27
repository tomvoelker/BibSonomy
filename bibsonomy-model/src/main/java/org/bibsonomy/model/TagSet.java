package org.bibsonomy.model;

import java.util.List;

/**
 * @author mwa
 * @version $Id$
 */
public class TagSet {

	/**
	 * List of tags in the set
	*/
	private List<Tag> tags;
	
	/**
	 * Name of the set
	*/
	private String setName;
	
	public TagSet(){};
	
	public TagSet(String setName, List<Tag> tags){
		this.setName = setName;
		this.tags = tags;
	}
	
	public List<Tag> getTags() {
		return this.tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public String getSetName() {
		return this.setName;
	}
	public void setSetName(String setName) {
		this.setName = setName;
	}
	
}
