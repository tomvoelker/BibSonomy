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

	/**
	 * The default gives relative URLs.
	 */
	private String projectHome = "/";

	/**
	 * Per default, generated URLs are not checked.
	 */
	private boolean checkUrls = false; 

	/**
	 * Sets up a new URLGenerator with the given projectHome.
	 * 
	 * @param projectHome
	 */
	public URLGenerator(final String projectHome) {
		super();
		this.projectHome = projectHome;
	}
	
	/**
	 * Sets up a new URLGenerator with the default projectHome ("/") and no 
	 * checking of URLs.
	 */
	public URLGenerator() {
		// noop
	}

	/**
	 * Creates an absolute URL for the given path.
	 * 
	 * @param path - the path part of the URL (TODO: with our without leading "/"?)
	 * @return The absolute URL.
	 */
	public String getAbsoluteUrl(final String path) {
		return getUrl(projectHome + path);
	}

	/**
	 * Returns the URL which represents a post. Depending on the type
	 * of the resource, this forwarded to {@link #getBookmarkUrl(Bookmark, User)} and 
	 * {@link #getPublicationUrl(BibTex, User)}.
	 * 
	 * @param post - The post for which the URL should be constructed. User and resources must not be null.
	 * @return The URL representing the given post.
	 */
	public String getPostUrl(final Post<? extends Resource> post) {
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
	public String getPublicationUrl(final BibTex publication, final User user) {
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
	public String getBookmarkUrl(final Bookmark bookmark, final User user) {
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
	public String getUserUrl(final User user) {
		return getUrl(projectHome + "user/" + encode(user.getName()));
	}

	/**
	 * If {@link #checkUrls} is <code>true</code>, each given string is converted
	 * into a {@link URL} (if that fails, <code>null</code> is returned).
	 * Otherwise, the given string is returned as is.
	 * 
	 * @param url
	 * @return
	 */
	private String getUrl(final String url) {
		if (checkUrls) {
			try {
				return new URL(url).toString();
			} catch (MalformedURLException ex) {
				// FIXME!
				return null;
			}
		}
		return url;
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

	/**
	 * ProjectHome defaults to <code>/</code>, such that relative URLs are
	 * generated. Note that this does not work with {@link #setCheckUrls(boolean)}
	 * set to <code>true</code>, since {@link URL} does not support relative URLs
	 * (or more correctly: relative URLs are not URLs).
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

	public boolean isCheckUrls() {
		return this.checkUrls;
	}

	/**
	 * If set to <code>true</code>, all generated URLs are put into {@link URL}
	 * objects. If that fails, <code>null</code> is returned. The default is 
	 * <code>false</code> such that no checking occurs. 
	 * 
	 * @param checkUrls
	 */
	public void setCheckUrls(boolean checkUrls) {
		this.checkUrls = checkUrls;
	}
}
