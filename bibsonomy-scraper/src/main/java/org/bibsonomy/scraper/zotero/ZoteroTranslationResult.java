/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.zotero;

/**
 * Result returned by the Zotero translation server client.
 *
 * @author tvolker
 */
public class ZoteroTranslationResult {

	private final String bibTeX;
	private final boolean multipleChoice;
	private final int choiceCount;

	/**
	 * @param bibTeX the exported BibTeX
	 * @param multipleChoice <code>true</code> if Zotero returned multiple choices
	 * @param choiceCount number of returned choices
	 */
	public ZoteroTranslationResult(final String bibTeX, final boolean multipleChoice, final int choiceCount) {
		this.bibTeX = bibTeX;
		this.multipleChoice = multipleChoice;
		this.choiceCount = choiceCount;
	}

	/**
	 * @return the exported BibTeX
	 */
	public String getBibTeX() {
		return this.bibTeX;
	}

	/**
	 * @return <code>true</code> if Zotero returned multiple choices
	 */
	public boolean isMultipleChoice() {
		return this.multipleChoice;
	}

	/**
	 * @return number of returned choices
	 */
	public int getChoiceCount() {
		return this.choiceCount;
	}
}
