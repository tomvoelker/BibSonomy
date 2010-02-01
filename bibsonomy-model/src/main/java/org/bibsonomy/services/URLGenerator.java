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
 * Generates the URLs used by the web application.
 * 
 * @author rja
 * @version $Id$
 */
public class URLGenerator {

	private static final String USER_PREFIX = "user";
	private static final String PUBLICATION_PREFIX = "bibtex";
	private static final String BOOKMARK_PREFIX = "url";
	private static final String PUBLICATION_INTRA_HASH_ID = new Integer(HashID.INTRA_HASH.getId()).toString();
	private static final String PUBLICATION_INTER_HASH_ID = new Integer(HashID.INTER_HASH.getId()).toString();

	
	private String projectHome;

	/**
	 * Sets up a new URLGenerator with the given projectHome.
	 * 
	 * @param projectHome
	 */
	public URLGenerator(String projectHome) {
		super();
		this.projectHome = projectHome;
	}
	
	/**
	 * Returns the URL which represents a post. Depending on the type
	 * of the resource, this forwarded to {@link #getBookmarkUrl(Bookmark, User)} and 
	 * {@link #getPublicationUrl(BibTex, User)}.
	 * 
	 * @param post - The post for which the URL should be constructed. User and resources must not be null.
	 * @return The URL representing the given post.
	 */
	public URL getPostUrl(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		if (resource instanceof Bookmark) {
			return getBookmarkUrl(((Bookmark) resource), post.getUser());
		} else if (resource instanceof BibTex) {
			return getPublicationUrl(((BibTex) resource), post.getUser());
		} else {
			throw new UnsupportedResourceTypeException();
		}	
	}
	
	
	/**
	 * Constructs a URL for the given resource and user. If no user 
	 * is given, the URL points to all posts for that resource.
	 * 
	 * @param publication - must have proper inter and intra hashes
	 * (a call to {@link Resource#recalculateHashes()} might be necessary
	 * but is not done by this method)
	 * 
	 * @param user - if null, the URL to all posts for the given publication 
	 * is returned.
	 * @return - The URL which represents the given publication.
	 */
	public URL getPublicationUrl(final BibTex publication, final User user) {
		/*
		 * no user given
		 */
		if (!ValidationUtils.present(user) || !ValidationUtils.present(user.getName())){
			return getUrl(projectHome + PUBLICATION_PREFIX + "/" + PUBLICATION_INTER_HASH_ID + publication.getInterHash());
		}
		return getUrl(projectHome + PUBLICATION_PREFIX + "/" + PUBLICATION_INTRA_HASH_ID + publication.getIntraHash() + "/" + encode(user.getName()));
	}


	/**
	 * Constructs a URL for the given resource and user. If no user 
	 * is given, the URL points to all posts for that resource.
	 * 
	 * @param bookmark - must have proper inter and intra hashes
	 * (a call to {@link Resource#recalculateHashes()} might be necessary
	 * but is not done by this method)
	 * 
	 * @param user - if null, the URL to all posts for the given bookmark
	 * is returned.
	 * @return - The URL which represents the given bookmark
	 */
	public URL getBookmarkUrl(final Bookmark bookmark, final User user) {
		/*
		 * no user given
		 */
		if (!ValidationUtils.present(user) || !ValidationUtils.present(user.getName())){
			return getUrl(projectHome + BOOKMARK_PREFIX + "/" + bookmark.getInterHash());
		}
		return getUrl(projectHome + BOOKMARK_PREFIX + "/" + bookmark.getIntraHash() + "/" + encode(user.getName()));
	}

	
	/**
	 * Constructs the URL for the user's page.
	 * 
	 * @param user
	 * @return The URL for the user's page.
	 */
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
