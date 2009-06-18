package org.bibsonomy.importer.bookmark.service;

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
 * Imports bookmarks and relations from Delicious. To get an instance of this 
 * class, use the {@link DeliciousImporterFactory}.
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
	private final String userAgent;


	private String password;
	private String userName;


	/**
	 * Constructor which allows to give a specific {@link #apiURL}.
	 * @param apiUrl - the URL to contact delicious
	 * @param userAgent - the userAgent this importer shall use to identify 
	 * itself in the corresponding HTTP header
	 */
	protected DeliciousImporter(final URL apiUrl, final String userAgent) {
		this.apiURL = apiUrl;
		this.userAgent = userAgent;
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

