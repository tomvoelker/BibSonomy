package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author mwa
 * @version $Id$
 * 
 * With this command we are able to receive multiple lists of posts for a resource. 
 * 
 */
public class MultiResourceViewCommand extends ResourceViewCommand {

	/** a list of bibtex lists **/
	private final List<ListCommand<Post<BibTex>>> listsBibTeX = new ArrayList<ListCommand<Post<BibTex>>>();

	/** a list of bookmark lists**/
	private final List<ListCommand<Post<Bookmark>>> listsBookmark = new ArrayList<ListCommand<Post<Bookmark>>>();

	/** description for a bibtex list **/
	private List<String> listsBibTeXDescription = new ArrayList<String>();

	/** description for a bookmark list **/
	private List<String> listsBookmarkDescription = new ArrayList<String>();

	
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<ListCommand<Post<T>>> getListCommand(final Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return (List) getListsBibTeX();
		} else if (resourceType == Bookmark.class) {
			return (List) getListsBookmark();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}
	
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	public <T extends Resource> List<String> getListsDescription(final Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return getListsBibTeXDescription();
		} else if (resourceType == Bookmark.class) {
			return getListsBookmarkDescription();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}

	public List<String> getListsBibTeXDescription() {
		return this.listsBibTeXDescription;
	}


	public void setListsBibTeXDescription(final List<String> listBibTeXDescription) {
		this.listsBibTeXDescription = listBibTeXDescription;
	}


	public List<String> getListsBookmarkDescription() {
		return this.listsBookmarkDescription;
	}


	public void setListsBookmarkDescription(final List<String> listBookmarkDescription) {
		this.listsBookmarkDescription = listBookmarkDescription;
	}


	public List<ListCommand<Post<BibTex>>> getListsBibTeX() {
		return this.listsBibTeX;
	}


	public List<ListCommand<Post<Bookmark>>> getListsBookmark() {
		return this.listsBookmark;
	}

	
}
