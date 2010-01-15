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
 * command with fields for the resource lists (one list for each resource).
 * 
 * is mainly a container for two list commands (bookmarks & bibtexs), the requested username
 * and a list of tags associated with the bookmarks / bibtexs
 * 
 * @see BaseCommand
 * @author Jens Illig
 * @author Dominik Benz
 */
public class SimpleResourceViewCommand extends ResourceViewCommand {
	/**
	 * coming from a postPublication/postBookmark dialogue,
	 * this parameter determines, if already existing posts can be overwritten by ones with the same resource
	 */
	private boolean overwrite;
	
	
	private ListCommand<Post<Bookmark>> bookmark = new ListCommand<Post<Bookmark>>(this);
	private ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>(this);
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> ListCommand<Post<T>> getListCommand(Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return (ListCommand) getBibtex();
		} else if (resourceType == Bookmark.class) {
			return (ListCommand) getBookmark();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}
	
	/**
	 * @return the bibtex ListView
	 */
	public ListCommand<Post<BibTex>> getBibtex() {
		return this.bibtex;
	}
	/**
	 * @param bibtex the bibtex ListView
	 */
	public void setBibtex(ListCommand<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}
	/**
	 * @return the bookmark ListView
	 */
	public ListCommand<Post<Bookmark>> getBookmark() {
		return this.bookmark;
	}
	/**
	 * @param bookmark the bookmark ListView
	 */
	public void setBookmark(ListCommand<Post<Bookmark>> bookmark) {
		this.bookmark = bookmark;
	}
	
	
	/**
	 * @return coming from a postPublication/postBookmark dialogue,
	 * overwrite determines, if already existing posts can be overwritten by ones with the same resource
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

	/**
	 * @param overwrite coming from a postPublication/postBookmark dialogue,
	 * this parameter determines, if already existing posts can be overwritten by ones with the same resource
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	
	
}