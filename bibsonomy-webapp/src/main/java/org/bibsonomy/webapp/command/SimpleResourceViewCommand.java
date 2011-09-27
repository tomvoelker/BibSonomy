package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * command with fields for the resource lists (one list for each resource).
 * 
 * is mainly a container for two list commands (bookmarks & publications), the requested username
 * and a list of tags associated with the bookmarks / publications
 * 
 * @author Jens Illig
 * @author Dominik Benz
 * @version $Id$
 */
public class SimpleResourceViewCommand extends ResourceViewCommand {
	private ListCommand<Post<Bookmark>> bookmark = new ListCommand<Post<Bookmark>>(this);
	private ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>(this);
	private ListCommand<Post<GoldStandardPublication>> goldStandardPublications = new ListCommand<Post<GoldStandardPublication>>(this);
	
	// TODO: move to listcommand or use the listCommand
	@Deprecated
	private Post<GoldStandardPublication> goldStandardPublication;
	
	@Deprecated
	private Post<Bookmark> goldStandardBookmark;
	
	@Deprecated
	private List<DiscussionItem> discussionItems;
	
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Resource> ListCommand<Post<T>> getListCommand(final Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return (ListCommand) getBibtex();
		}
		
		if (resourceType == Bookmark.class) {
			return (ListCommand) getBookmark();
		}
		
		if (resourceType == GoldStandardPublication.class) {
			return (ListCommand) getGoldStandardPublications();
		}
		
		if (resourceType == GoldStandardBookmark.class) {
			// TODO
			return null;
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
	public void setBibtex(final ListCommand<Post<BibTex>> bibtex) {
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
	public void setBookmark(final ListCommand<Post<Bookmark>> bookmark) {
		this.bookmark = bookmark;
	}

	/**
	 * @param goldStandardPublication the goldStandardPublication to set
	 */
	public void setGoldStandardPublication(final Post<GoldStandardPublication> goldStandardPublication) {
		this.goldStandardPublication = goldStandardPublication;
	}

	/**
	 * @return the goldStandardPublication
	 */
	public Post<GoldStandardPublication> getGoldStandardPublication() {
		return goldStandardPublication;
	}
	
	/**
	 * @return the goldStandardBookmark
	 */
	public Post<Bookmark> getGoldStandardBookmark() {
		return this.goldStandardBookmark;
	}

	/**
	 * @param goldStandardBookmark the goldStandardBookmark to set
	 */
	public void setGoldStandardBookmark(final Post<Bookmark> goldStandardBookmark) {
		this.goldStandardBookmark = goldStandardBookmark;
	}


	/**
	 * @param goldStandardPublications the goldStandardPublications to set
	 */
	public void setGoldStandardPublications(final ListCommand<Post<GoldStandardPublication>> goldStandardPublications) {
		this.goldStandardPublications = goldStandardPublications;
	}

	/**
	 * @return the goldStandardPublications
	 */
	public ListCommand<Post<GoldStandardPublication>> getGoldStandardPublications() {
		return goldStandardPublications;
	}

	/**
	 * @return the goldStandardBookmarks
	 */
	public List<DiscussionItem> getDiscussionItems() {
		return this.discussionItems;
	}

	/**
	 * @param discussionItems the discussionItems to set
	 */
	public void setDiscussionItems(final List<DiscussionItem> discussionItems) {
		this.discussionItems = discussionItems;
	}
	
}