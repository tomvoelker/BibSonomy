package org.bibsonomy.services.help;

/**
 * represents a search result
 *
 * @author dzo
 */
public class HelpSearchResult implements Comparable<HelpSearchResult> {
	
	private float score;
	private String page;
	
	private String highlightContent;
	
	/**
	 * @return the score
	 */
	public float getScore() {
		return this.score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(float score) {
		this.score = score;
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return this.page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @return the highlightContent
	 */
	public String getHighlightContent() {
		return this.highlightContent;
	}

	/**
	 * @param highlightContent the highlightContent to set
	 */
	public void setHighlightContent(String highlightContent) {
		this.highlightContent = highlightContent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(HelpSearchResult o) {
		final float scoreDiff = this.score - o.score;
		if (scoreDiff == 0) {
			return this.page.compareTo(o.page);
		}
		return (int) Math.signum(scoreDiff);
	}
}
