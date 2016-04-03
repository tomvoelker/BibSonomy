/**
 * BibSonomy-Scrapingservice - Stand-alone web application for web page scrapers (see bibsonomy-scraper)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scrapingservice.beans;

import java.io.Serializable;
import java.net.URL;

import org.bibsonomy.scraper.Scraper;

/**
 * @author rja
 */
public class ScrapingResultBean implements Serializable {
	
	private static final long serialVersionUID = 8899554705056075887L;

	private String bibtex;
	private String errorMessage;
	private URL url;
	private String selection;
	private Scraper scraper;
	
	public ScrapingResultBean() {
		// TODO Auto-generated constructor stub
	}
	

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getBibtex() {
		return bibtex;
	}

	public void setBibtex(String bibtex) {
		this.bibtex = bibtex;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public String getSelection() {
		return selection;
	}


	public void setSelection(String selection) {
		this.selection = selection;
	}


	public Scraper getScraper() {
		return scraper;
	}


	public void setScraper(Scraper scraper) {
		this.scraper = scraper;
	}

}
