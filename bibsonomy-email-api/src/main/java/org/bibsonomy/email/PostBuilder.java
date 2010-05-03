package org.bibsonomy.email;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;

/**
 * Creates {@link Post}s from an {@link Email}.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class PostBuilder {

	private UrlProvider urlProvider;
	
	public List<Post<? extends Resource>> buildPosts(final Email email, final String username) {
		return buildPosts(email, username, GroupUtils.getPublicGroup().getName());
	}
	
	/**
	 * Creates a bookmark post for every URL found in the email.
	 * 
	 * FIXME: because of ugly Java Generics we return 
	 *   List<Post<? extends Resource>> 
	 * instead of 
	 *   List<Post<Bookmark>> 
	 * to be compatible to LogicInterface.createPosts(List<Post<? extends Resource>> posts)
	 * 
	 * @param email
	 * @return
	 */
	public List<Post<? extends Resource>> buildPosts(final Email email, final String username, final String group) {
		/*
		 * create post from given email + username + groups
		 */
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		for (final String url: email.getUrls()) {
			final Post<Bookmark> post = new Post<Bookmark>();
			post.setTags(email.getTags()); // FIXME: use tags.clone() here!
			post.setUser(new User(username));
			post.setGroups(Collections.singleton(new Group(group)));
			post.setResource(urlProvider.resolveUrl(url));
			posts.add(post);
		}
		return posts;
	}

	public UrlProvider getUrlProvider() {
		return urlProvider;
	}

	public void setUrlProvider(UrlProvider urlProvider) {
		this.urlProvider = urlProvider;
	}
	
}
