package org.bibsonomy.model;

import java.net.URL;

import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.util.StringUtils;

/**
 * This is a bookmark, which is derived from {@link Resource} like all
 * resources.
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

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Bookmarks use the same hash value for both intrahash and interhash
	 */
	@Override
	public String getInterHash() {
		return super.getIntraHash();
	}

	public String getHash() {
		return StringUtils.getMD5Hash(this.url);
	}

	@Override
	public void recalculateHashes() {
		this.setIntraHash(getHash());
	}
}