package org.bibsonomy.services;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class URLGenerator {

	private static final String USER_PREFIX = "user";
	private static final String PUBLICATION_PREFIX = "bibtex";
	private static final String BOOKMARK_PREFIX = "url";
	private static final String PUBLICATION_INTRA_HASH_ID = new Integer(HashID.INTRA_HASH.getId()).toString();
	private static final String PUBLICATION_INTER_HASH_ID = new Integer(HashID.INTER_HASH.getId()).toString();
	// FIXME: we need different IDs for Bookmarks!
	private static final String BOOKMARK_INTRA_HASH_ID = new Integer(HashID.INTRA_HASH.getId()).toString();
	private static final String BOOKMARK_INTER_HASH_ID = new Integer(HashID.INTER_HASH.getId()).toString();

	
	private String projectHome;

	public URLGenerator(String projectHome) {
		super();
		this.projectHome = projectHome;
	}

	public URL getPostUrl(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		if (resource instanceof Bookmark) {
			return getUrl(((Bookmark) resource).getUrl());
		} else if (resource instanceof BibTex) {
			return getPublicationUrl(((BibTex) resource), post.getUser());
		} else {
			throw new UnsupportedResourceTypeException();
		}	
	}
	
	public URL getInternalPostUrl(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		if (resource instanceof Bookmark) {
			return getBookmarkUrl(((Bookmark) resource), post.getUser());
		} else if (resource instanceof BibTex) {
			return getPublicationUrl(((BibTex) resource), post.getUser());
		} else {
			throw new UnsupportedResourceTypeException();
		}	
	}
	
	
	public URL getPublicationUrl(final BibTex publication, final User user) {
		/*
		 * no user given
		 */
		if (!ValidationUtils.present(user) || !ValidationUtils.present(user.getName())){
			return getUrl(projectHome + PUBLICATION_PREFIX + "/" + PUBLICATION_INTER_HASH_ID + publication.getInterHash());
		}
		return getUrl(projectHome + PUBLICATION_PREFIX + "/" + PUBLICATION_INTRA_HASH_ID + publication.getIntraHash() + "/" + encode(user.getName()));
	}


	public URL getBookmarkUrl(final Bookmark bookmark, final User user) {
		/*
		 * no user given
		 */
		if (!ValidationUtils.present(user) || !ValidationUtils.present(user.getName())){
			return getUrl(projectHome + BOOKMARK_PREFIX + "/" + BOOKMARK_INTER_HASH_ID + bookmark.getInterHash());
		}
		return getUrl(projectHome + BOOKMARK_PREFIX + "/" + BOOKMARK_INTRA_HASH_ID + bookmark.getIntraHash() + "/" + encode(user.getName()));
	}

	
	public URL getUserUrl(final User user) {
		return getUrl(projectHome + "user/" + encode(user.getName()));
	}

	private static URL getUrl(final String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			// FIXME!
			return null;
		}
	}
	
	private static String encode(final String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}
	
	public String getProjectHome() {
		return this.projectHome;
	}

	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}
}
