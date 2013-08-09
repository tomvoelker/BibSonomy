package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Command for delivering post recommendation data
 * 
 * @author Lukas
 * @version $Id$
 */
public class RecommendedPostsCommand extends SimpleResourceViewCommand {
	/**
	 * Fills the news box in the sidebar
	 */
	private List<Post<Bookmark>> news;

	/**
	 * @return The latest news posts.
	 */
	public List<Post<Bookmark>> getNews() {
		return this.news;
	}

	/**
	 * @param news 
	 */
	public void setNews(List<Post<Bookmark>> news) {
		this.news = news;
	}
}
