package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationTag;

/**
 * This class wraps a BibSonomy {@link Tag} to allow injection of those into the framework.
 * 
 * @author lukas
 *
 */
public class TagWrapper implements RecommendationTag {

	private Tag tag;

	public TagWrapper(Tag tag) {
		this.tag = tag;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.tag.getName();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.tag.setName(name);
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return (this.tag.equals(obj));
	}

	@Override
	public int compareTo(final recommender.core.interfaces.model.RecommendationTag tag) {
		return this.tag.getName().toLowerCase().compareTo(tag.getName().toLowerCase());
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
