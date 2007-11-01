/*
 * Created on 26.08.2007
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * command with fields for the resource lists.
 * 
 * @see BaseCommand
 * @author Jens Illig
 */
public class ResourceViewCommand extends BaseCommand {
	private ListView<Post<Bookmark>> bookmark = new ListView<Post<Bookmark>>();
	private ListView<Post<BibTex>> bibtex = new ListView<Post<BibTex>>();
	private String requestedUser;
	
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> ListView<Post<T>> getListView(Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return (ListView) getBibtex();
		} else if (resourceType == Bookmark.class) {
			return (ListView) getBookmark();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}
	
	/**
	 * @return the bibtex ListView
	 */
	public ListView<Post<BibTex>> getBibtex() {
		return this.bibtex;
	}
	/**
	 * @param bibtex the bibtex ListView
	 */
	public void setBibtex(ListView<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}
	/**
	 * @return the bookmark ListView
	 */
	public ListView<Post<Bookmark>> getBookmark() {
		return this.bookmark;
	}
	/**
	 * @param bookmark the bookmark ListView
	 */
	public void setBookmark(ListView<Post<Bookmark>> bookmark) {
		this.bookmark = bookmark;
	}
	/**
	 * @return name of the user whose resources are requested
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}
	/**
	 * @param requestedUser name of the user whose resources are requested
	 */
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}
	
	
}