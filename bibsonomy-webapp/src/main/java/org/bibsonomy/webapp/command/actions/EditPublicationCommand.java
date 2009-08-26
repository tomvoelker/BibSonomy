package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.BibTex;

/**
 * FIXME: check the methods here
 * 
 * @author rja
 * @author dzo
 * @version $Id$
 */
public class EditPublicationCommand extends EditPostCommand<BibTex> {
	
	/**
	 * selected text provided by bookmarklet
	 */
	private String selection;
	
	/**
	 * url provided by bookmarklet
	 */
	private String url;
	
	/**
	 * the scraped bibtex
	 */
	private String scrapedBibTex;
	
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
	public void setSelection(String selection) {
		this.selection = selection;
	}

	/**
	 * @return the selection
	 */
	public String getSelection() {
		return this.selection;
	}


	/**
	 * @param scrapedBibTex the scrapedBibTex to set
	 */
	public void setScrapedBibTex(String scrapedBibTex) {
		this.scrapedBibTex = scrapedBibTex;
	}


	/**
	 * @return the scrapedBibTex
	 */
	public String getScrapedBibTex() {
		return scrapedBibTex;
	}
}
