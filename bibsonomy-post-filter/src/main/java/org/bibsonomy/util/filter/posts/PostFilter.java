package org.bibsonomy.util.filter.posts;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.filter.posts.matcher.Matcher;
import org.bibsonomy.util.filter.posts.modifier.Modifier;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class PostFilter {

	private Matcher matcher;
	private Modifier modifier;

	public PostFilter() {
		// TODO Auto-generated constructor stub
	}


	public PostFilter(Matcher matcher, Modifier modifier) {
		super();
		this.matcher = matcher;
		this.modifier = modifier;
	}
	/**
	 * Returns all posts where the {@link #matcher} matched.
	 * 
	 * @param posts
	 * @return
	 */
	public List<Post<? extends Resource>> getFilteredPosts(List<Post<? extends Resource>> posts) {
		final List<Post<? extends Resource>> filteredPosts = new LinkedList<Post<? extends Resource>>();

		for (final Post<? extends Resource> post : posts) {
			if (matcher.matches(post)) {
				filteredPosts.add(post);
			}
		}

		return filteredPosts;
	}
	/**
	 * Filters the posts using {@link #matcher}, then updates them. Returns only
	 * those posts, where the filter matches and which have been updated (i.e.,
	 * where the changed something).
	 * 
	 * @param posts
	 * @return
	 */
	public List<Post<? extends Resource>> getFilteredAndUpdatedPosts(List<Post<? extends Resource>> posts) {
		final List<Post<? extends Resource>> updatedPosts = new LinkedList<Post<? extends Resource>>();

		for (final Post<? extends Resource> post : posts) {
			if (matcher.matches(post)) {
				if (modifier.updatePost(post)) {
					updatedPosts.add(post);
				}

			}
		}

		return updatedPosts;
	}
	public Matcher getMatcher() {
		return matcher;
	}
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}
	public Modifier getModifier() {
		return modifier;
	}
	public void setModifier(Modifier modifier) {
		this.modifier = modifier;
	}

}

