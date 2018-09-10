package org.bibsonomy.webapp.command;


public class ReportCommand extends BaseCommand{
	/**
	 * List of all porjects being returned
	 */
	private List<Post<T>> posts;

	/**
	 * @param posts
	 */
	public void setPosts(List<Post<T>> posts) {
		this.posts = posts;
	}

	/**
	 * @return
	 */
	public List<Post<T>> getPosts() {
		return this.posts;
	}
}
