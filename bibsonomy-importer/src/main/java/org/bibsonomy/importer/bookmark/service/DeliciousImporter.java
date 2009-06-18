package org.bibsonomy.importer.bookmark.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;

/**
 * 
 * Imports bookmarks and relations from Delicious.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class DeliciousImporter implements RemoteServiceBookmarkImporter, RelationImporter {

	private static final Log log = LogFactory.getLog(DeliciousImporter.class);

	/**
	 * The URL to contact Delicious.
	 */
	private final URL apiURL;


	private String password;
	private String userName;


	/**
	 * Default constructor using the default {@link #apiURL}.
	 *  
	 * @throws MalformedURLException
	 */
	public DeliciousImporter() throws MalformedURLException {
		/*
		 * TODO: there was a reason we use "-1" as port ... please document it 
		 * here
		 */
		this.apiURL = new URL ("https", "api.del.icio.us", -1, "/v1/posts/all");
	}

	/**
	 * Constructor which allows to give a specific {@link #apiURL}.
	 * @param apiUrl
	 */
	public DeliciousImporter(final URL apiUrl) {
		this.apiURL = apiUrl;
	}

	@Override
	public List<Post<Bookmark>> getPosts() {
		final List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
		/*
		 * TODO: get posts
		 */
		return posts;
	}

	@Override
	public List<Tag> getRelations() {
		final List<Tag> relations = new LinkedList<Tag>();
		/*
		 * TODO: get relations
		 */
		return relations;
	}

	@Override
	public void setCredentials(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
	}
}

