package org.bibsonomy.community.webapp.command;

import org.bibsonomy.model.BibTex;

/**
 * FIXME: check the methods here
 * 
 * @author rja
 * @version $Id$
 */
public class EditPublicationCommand extends EditPostCommand<BibTex> {
	
	private static final String[] entryTypes = new String[]{"article", "book", "booklet", "inbook", "incollection", "inproceedings",
		"manual", "mastersthesis", "misc", "phdthesis", "proceedings", "techreport", "unpublished"};
	
	/**
	 * Sets the URL of the post. 
	 * Needed for the (old) postBookmark button and "copy" links. 
	 *  
	 * @param url 
	 */
	public void setUrl(final String url){
		getPost().getResource().setUrl(url);
	}
	
	/**
	 * Sets the title of a post.
	 * Needed for the (old) postBookmark button and "copy" links.
	 * 
	 * @param title
	 */
	public void setDescription(final String title){
		getPost().getResource().setTitle(title);
	}
	
	/**
	 * Sets the description of a post.
	 * Needed for the (old) postBookmark button and "copy" links.
	 * 
	 * @param description
	 */
	public void setExtended(final String description){
		getPost().setDescription(description);
	}

	public String[] getEntryTypes() {
		return entryTypes;
	}
	
}
