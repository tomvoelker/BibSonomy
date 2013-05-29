package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.Bookmark;

import recommender.core.interfaces.model.RecommendationResource;

public class BookmarkWrapper implements RecommendationResource{

	/**
	 * for persistence
	 */
	private static final long serialVersionUID = -8364621454691141026L;

	private Bookmark bookmark;
	
	public BookmarkWrapper(Bookmark bookmark) {
		this.bookmark = bookmark;
	}
	
	public Bookmark getBookmark() {
		return bookmark;
	}
	
	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}
	
	@Override
	public String getInterHash() {
		return this.bookmark.getInterHash();
	}

	@Override
	public void setInterHash(String interHash) {
		this.bookmark.setInterHash(interHash);
	}

	@Override
	public String getIntraHash() {
		return this.bookmark.getIntraHash();
	}

	@Override
	public void setIntraHash(String intraHash) {
		this.bookmark.setIntraHash(intraHash);
	}

	@Override
	public int getCount() {
		return this.bookmark.getCount();
	}

	@Override
	public void setCount(int count) {
		this.bookmark.setCount(count);
	}

	@Override
	public String getTitle() {
		return this.bookmark.getTitle();
	}

	@Override
	public void setTitle(String title) {
		this.bookmark.setTitle(title);
	}

	@Override
	public String getUrl() {
		return this.bookmark.getUrl();
	}

	@Override
	public void setUrl(String url) {
		this.bookmark.setUrl(url);
	}

	@Override
	public void recalculateHashes() {
		this.bookmark.recalculateHashes();
	}

}
