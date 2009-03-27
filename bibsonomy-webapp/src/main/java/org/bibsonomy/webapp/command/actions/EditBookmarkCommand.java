package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.Bookmark;

/**
 * @author fba
 * @version $Id$
 */
public class EditBookmarkCommand extends EditPostCommand<Bookmark> {
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
}
