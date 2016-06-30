/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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
package org.bibsonomy.scraper;

/**
 * object that holds data for a scraper test
 *
 * @author dzo
 */
public class ScraperTestData {
	private String testId;
	private String scraperClassName;
	private String url;
	private String selection;
	private String expectedBibTeX;
	private boolean enabled = true;
	
	private String description;
	private String bibTeXFileName;
	
	/**
	 * @return the testId
	 */
	public String getTestId() {
		return this.testId;
	}

	/**
	 * @param testId the testId to set
	 */
	public void setTestId(String testId) {
		this.testId = testId;
	}

	/**
	 * @return the scraperClassName
	 */
	public String getScraperClassName() {
		return this.scraperClassName;
	}

	/**
	 * @param scraperClassName the scraperClassName to set
	 */
	public void setScraperClassName(String scraperClassName) {
		this.scraperClassName = scraperClassName;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the selection
	 */
	public String getSelection() {
		return this.selection;
	}
	
	/**
	 * @param selection the selection to set
	 */
	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	/**
	 * @return the expectedBibTeX
	 */
	public String getExpectedBibTeX() {
		return this.expectedBibTeX;
	}
	
	/**
	 * @param expectedBibTeX the expectedBibTeX to set
	 */
	public void setExpectedBibTeX(String expectedBibTeX) {
		this.expectedBibTeX = expectedBibTeX;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the bibTeXFileName
	 */
	public String getBibTeXFileName() {
		return this.bibTeXFileName;
	}

	/**
	 * @param bibTeXFileName the bibTeXFileName to set
	 */
	public void setBibTeXFileName(String bibTeXFileName) {
		this.bibTeXFileName = bibTeXFileName;
	}
}
