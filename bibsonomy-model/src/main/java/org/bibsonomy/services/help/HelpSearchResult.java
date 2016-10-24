/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		final float scoreDiff = o.score - this.score;
		if (scoreDiff == 0) {
			return this.page.compareTo(o.page);
		}
		return (int) Math.signum(scoreDiff);
	}
}
