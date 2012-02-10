package org.bibsonomy.webapp.command;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.model.statistics.StatisticsValues;

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
	// TODO: rename to bookmarks
	private ListCommand<Post<Bookmark>> bookmark = new ListCommand<Post<Bookmark>>(this);
	// TODO: rename to publications
	private ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>(this);
	
	private ListCommand<Post<GoldStandardPublication>> goldStandardPublications = new ListCommand<Post<GoldStandardPublication>>(this);
	private ListCommand<Post<GoldStandardBookmark>>	goldStandardBookmarks = new ListCommand<Post<GoldStandardBookmark>>(this);
	
	// TODO: move to DiscussedViewCommand ?
	private StatisticsValues discussionsStatistic;
	
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
			return (ListCommand) getGoldStandardBookmarks();
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
	public ListCommand<Post<GoldStandardBookmark>> getGoldStandardBookmarks() {
		return this.goldStandardBookmarks;
	}

	/**
	 * @param goldStandardBookmarks the goldStandardBookmarks to set
	 */
	public void setGoldStandardBookmarks(final ListCommand<Post<GoldStandardBookmark>> goldStandardBookmarks) {
		this.goldStandardBookmarks = goldStandardBookmarks;
	}

	/**
	 * @param statistics the discussionsStatistic to set
	 */
	public void setDiscussionsStatistic(final Statistics statistics) {
		if (statistics instanceof StatisticsValues) {
			this.discussionsStatistic = (StatisticsValues) statistics;
		} 
	}

	/**
	 * @return the discussionsStatistic
	 */
	public StatisticsValues getDiscussionsStatistic() {
		return discussionsStatistic;
	}
	
}