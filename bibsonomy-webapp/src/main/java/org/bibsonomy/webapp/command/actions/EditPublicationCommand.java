/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.command.actions;

import java.net.URL;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.scraper.ScrapingContext;

/**
 * FIXME: check the methods here
 * 
 * @author rja
 * @author dzo
 */
public class EditPublicationCommand extends EditPostCommand<BibTex> {
	
	/**
	 * selected text provided by bookmarklet
	 */
	private String selection;
	
	/**
	 * TODO: can we use {@link URL} as type?
	 * url provided by bookmarklet
	 */
	private String url;
	
	/**
	 * The metadata from scraping
	 */
	private ScrapingContext scrapingContext;
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
	
	/**
	 * Sets the title of a post.
	 * Needed for the (old) postBookmark button and "copy" links.
	 * 
	 * @param description
	 */
	public void setDescription(final String description){
		this.getPost().setDescription(description); // TODO
	}
	
	/**
	 * Sets the description of a post.
	 * Needed for the (old) postBookmark button and "copy" links.
	 * 
	 * @param description
	 */
	public void setExtended(final String description){
		this.getPost().setDescription(description); // TODO
	}

	/**
	 * @param selection the selection to set
	 */
	public void setSelection(final String selection) {
		this.selection = selection;
	}

	/**
	 * @return the selection
	 */
	public String getSelection() {
		return this.selection;
	}

	/**
	 * @return The scraping context which describes where this bookmark is 
	 * coming from.
	 */
	public ScrapingContext getScrapingContext() {
		return this.scrapingContext;
	}

	/**
	 * The scraping context allows us to show the user meta information about
	 * the scraping process.
	 * 
	 * @param scrapingContext
	 */
	public void setScrapingContext(final ScrapingContext scrapingContext) {
		this.scrapingContext = scrapingContext;
	}
}

