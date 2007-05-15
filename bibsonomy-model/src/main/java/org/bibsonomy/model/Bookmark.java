package org.bibsonomy.model;

import java.net.URL;

import org.bibsonomy.model.util.ResourceUtils;

/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.gen_model.Resource} like all resources in BibSonomy.
 */
public class Bookmark extends Resource {

	/**
	 * An {@link URL} pointing to some website.
	 * FIXME: Use URL instead of String
	 */
	private String url;
	
	/**
	 * The title of this bookmark.
	 */
	private String title;
	
	public String getUrl() {
		return this.url;
	}

	/**
	 * bookmarks use the same hash value for both intrahash and interhash
	 */
	@Override
	public String getInterHash() {
		return super.getIntraHash();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHash() {
		return ResourceUtils.hash(this.url);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void recalculateHashes() {
		this.setIntraHash(getHash());
	}
}