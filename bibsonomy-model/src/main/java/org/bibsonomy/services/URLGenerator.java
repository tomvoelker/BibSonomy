/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.services;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.UrlUtils;

/**
 * FIXME: introduce a factory for the url generator and remove all *AndSysUrl methods
 * 
 * TODO: Unify URL constructions for various cases (history, community posts,
 * regular posts) Generates the URLs used by the web application.
 * 
 * @author rja
 */
public class URLGenerator {


	/**
	 * Provides page names.
	 * 
	 * XXX: experimental!
	 * 
	 * @author rja
	 * 
	 */
	public enum Page {
		/**
		 * all posts users' have sent me using the "send:" system tag
		 */
		INBOX("inbox"),
		/**
		 * all posts I have picked
		 */
		BASKET("clipboard");

		private final String path;

		private Page(final String path) {
			this.path = path;
		}

		/**
		 * @return The string representation of this page
		 */
		public String getPath() {
			return this.path;
		}
	}

	private static final String BOOKMARK = Bookmark.class.getSimpleName();

	private static final String ADMIN_PREFIX = "admin";
	private static final String AUTHOR_PREFIX = "author";
	private static final String BIBTEXEXPORT_PREFIX = "bib";
	private static final String BIBTEXKEY_PREFIX = "bibtexkey";
	public static final String BOOKMARK_PREFIX = "url";
	private static final String CONCEPTS_PREFIX = "concepts";
	private static final String CONCEPT_PREFIX = "concept";
	private static final String DOCUMENT_PREFIX = "documents";
	private static final String FOLLOWERS_PREFIX = "followers";
	private static final String FRIEND_PREFIX = "friend";
	private static final String GROUPS = "groups";
	private static final String GROUP_PREFIX = "group";
	private static final String LOGIN_PREFIX = "login";
	private static final String LAYOUT_PREFIX = "layout";
	private static final String ENDNOTE_PREFIX = "endnote";
	private static final String MSWORD_PREFIX = "msofficexml";
	private static final String REGISTER = "register";
	private static final String MYBIBTEX_PREFIX = "myBibTex";
	private static final String MYDOCUMENTS_PREFIX = "myDocuments";
	private static final String MYDUPLICATES_PREFIX = "myDuplicates";
	private static final String MYHOME_PREFIX = "myHome";
	private static final String MYRELATIONS_PREFIX = "myRelations";
	private static final String MYSEARCH_PREFIX = "mySearch";
	private static final String PICTURE_PREFIX = "picture";
	private static final String PUBLICATION_PREFIX = "bibtex";
	private static final String RELEVANTFOR_PREFIX = "relevantfor";
	private static final String SEARCH_PREFIX = "search";
	private static final String SHARED_RESOURCE_SEARCH_PREFIX = "sharedResourceSearch";
	private static final String SETTINGS_PREFIX = "settings";
	private static final String TAG_PREFIX = "tag";
	private static final String USER_PREFIX = "user";
	private static final String VIEWABLE_PREFIX = "viewable";
	private static final String VIEWABLE_FRIENDS_SUFFIX = "friends";
	private static final String VIEWABLE_PRIVATE_SUFFIX = "private";
	private static final String VIEWABLE_PUBLIC_SUFFIX = "public";
	private static final String HISTORY_PREFIX = "history";

	private static final String PUBLICATION_INTRA_HASH_ID = String.valueOf(HashID.INTRA_HASH.getId());
	private static final String PUBLICATION_INTER_HASH_ID = String.valueOf(HashID.INTER_HASH.getId());

	/**
	 * The default gives relative URLs.
	 */
	private String projectHome = "/";

	/**
	 * Per default, generated URLs are not checked.
	 */
	private boolean checkUrls = false;

	/**
	 * Prefix to be inserted after the project home.
	 */
	private String prefix = "";

	/**
	 * Sets up a new URLGenerator with the default projectHome ("/") and no
	 * checking of URLs.
	 */
	public URLGenerator() {
		// noop
	}

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
	 * Creates an absolute URL for the given path.
	 * 
	 * @param path
	 *            - the path part of the URL (TODO: with or without leading
	 *            "/"?)
	 * @return The absolute URL.
	 */
	public String getAbsoluteUrl(final String path) {
		return this.getUrl(this.projectHome + path);
	}

	/**
	 * Constructs a url to the admin page if no name is given or a url to a
	 * subpage otherwise
	 * 
	 * @param name
	 * @return The URL pointing to the page.
	 */
	public String getAdminUrlByName(final String name) {
		String url = this.projectHome + prefix + ADMIN_PREFIX;
		if (present(name)) {
			url += "/" + UrlUtils.safeURIEncode(name);
		}
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the author's page.
	 * 
	 * @param name
	 *            the name of the author
	 * @return The URL for the author's page.
	 */
	public String getAuthorUrlByPersonName(final PersonName name) {
		final String url = this.projectHome
				+ prefix
				+ AUTHOR_PREFIX
				+ "/"
				+ UrlUtils.safeURIEncode(name.getFirstName() + " "
						+ name.getLastName());
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the author's page.
	 * 
	 * @param author
	 *            the name of the author
	 * @return The URL for the author's page.
	 */
	public String getAuthorUrlByAuthor(final Author author) {
		final String url = this.projectHome
				+ prefix
				+ AUTHOR_PREFIX
				+ "/"
				+ UrlUtils.safeURIEncode(author.getFirstName() + " "
						+ author.getLastName());
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the author's page.
	 * 
	 * @param authorLastName
	 * @return The URL for the author's page.
	 */
	public String getAuthorUrlByName(final String authorLastName) {
		String url = this.projectHome
				+ prefix
				+ AUTHOR_PREFIX
				+ "/"
				+ UrlUtils.safeURIEncode(BibTexUtils
						.cleanBibTex(authorLastName));
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the author's page for the specified system.
	 * 
	 * @param authorLastName
	 * @param systemUrl
	 * @return The URL for the author's page.
	 */
	@Deprecated 
	public String getAuthorUrlByNameAndSysUrl(final String authorLastName,
			final String systemUrl) {
		String url = systemUrl
				+ prefix
				+ AUTHOR_PREFIX
				+ "/"
				+ UrlUtils.safeURIEncode(BibTexUtils
						.cleanBibTex(authorLastName));
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for the basket page, i.e. /basket
	 * 
	 * @return URL pointing to the basket page.
	 */
	public String getBasketUrl() {
		String url = this.projectHome + prefix + Page.BASKET.getPath();
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for the given resource and user. If no user is given,
	 * the URL points to all posts for that resource.
	 * 
	 * @param bookmark
	 *            - must have proper inter and intra hashes (a call to
	 *            {@link Resource#recalculateHashes()} might be necessary but is
	 *            not done by this method)
	 * 
	 * @param user
	 *            - if null, the URL to all posts for the given bookmark is
	 *            returned.
	 * @return - The URL which represents the given bookmark
	 */
	public String getBookmarkUrl(final Bookmark bookmark, final User user) {
		/*
		 * no user given
		 */
		if (!present(user) || !present(user.getName())) {
			return this.getUrl(this.projectHome + prefix + BOOKMARK_PREFIX
					+ "/" + bookmark.getInterHash());
		}
		return this.getBookmarkUrlByIntraHashAndUsername(
				bookmark.getIntraHash(), user.getName());
	}

	/**
	 * Constructs a bookmark URL for the given intraHash. If you have the
	 * resource as object, please use {@link #getBookmarkUrl(Bookmark, User)}
	 * 
	 * @param intraHash
	 * @return The URL pointing to the post of that user for the bookmark
	 *         represented by the given intrahash.
	 */
	public String getBookmarkUrlByIntraHash(final String intraHash) {
		return this.getBookmarkUrlByIntraHashAndUsername(intraHash, null);
	}

	/**
	 * Constructs a bookmark URL for the given intraHash and userName. If you
	 * have the resource as object, please use
	 * {@link #getBookmarkUrl(Bookmark, User)}
	 * 
	 * @param intraHash
	 * @param userName
	 * @return The URL pointing to the post of that user for the bookmark
	 *         represented by the given intrahash.
	 */
	public String getBookmarkUrlByIntraHashAndUsername(final String intraHash,
			final String userName) {
		String url = this.projectHome + prefix + BOOKMARK_PREFIX + "/" + intraHash;
		if (present(userName)) {
			url += "/" + UrlUtils.safeURIEncode(userName);

		}
		return this.getUrl(url);
	}

	/**
	 * url for BibTex export
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return returns the BibTex Export url
	 */
	public String getBibtexExportUrlByIntraHashAndUserName(final String intraHash,
			final String userName){
		
		return this.getBibtexExportUrlByIntraHashUserNameAndSysUrl(intraHash, userName, this.projectHome);
		
	}
	/**
	 * url for BibTex export for a specific system
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return returns the BibTex export url for the specified system
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getBibtexExportUrlByIntraHashUserNameAndSysUrl(final String intraHash,
			final String userName, final String systemUrl){
		String url = systemUrl + prefix + BIBTEXEXPORT_PREFIX + "/" + PUBLICATION_PREFIX + "/" + PUBLICATION_INTRA_HASH_ID + intraHash;
		if (present(userName)) {
			url += "/" + UrlUtils.safeURIEncode(userName);

		}
		return this.getUrl(url);
		
	}
	
	/**
	 * url for Endnote export
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return returns the Endnote export url
	 */
	public String getEndnoteUrlByIntraHashAndUserName(final String intraHash, final String userName){
		
		return this.getEndnoteUrlByIntraHashUserNameAndSysUrl(intraHash, userName, this.projectHome);
		
	}
	/**
	 * url for Endnote export for a specific system
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return returns the Endnote export url for the specified system
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getEndnoteUrlByIntraHashUserNameAndSysUrl(final String intraHash, final String userName, final String systemUrl){
		String url = systemUrl + prefix + LAYOUT_PREFIX + "/" + ENDNOTE_PREFIX + "/" + PUBLICATION_PREFIX + "/" + PUBLICATION_INTRA_HASH_ID + intraHash;
		if (present(userName)) {
			url += "/" + UrlUtils.safeURIEncode(userName);
		}
		return this.getUrl(url);
	}
	
	/**
	 * url for MS WORD Reference Manager
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return returns the MS WORD Reference Manager url
	 */
	public String getMSWordUrlByIntraHashAndUserName(final String intraHash, final String userName){
		return this.getMSWordUrlByIntraHashUserNameAndSysUrl(intraHash, userName, this.projectHome);
	}
	/**
	 * url for MS WORD Reference Manager for a specific system
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return returns the MS WORD Reference Manager url for the specified system
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getMSWordUrlByIntraHashUserNameAndSysUrl(final String intraHash, final String userName, final String systemUrl){
		String url = systemUrl + prefix + LAYOUT_PREFIX + "/" + MSWORD_PREFIX + "/" + PUBLICATION_PREFIX + "/" + PUBLICATION_INTRA_HASH_ID + intraHash;
		if (present(userName)) {
			url += "/" + UrlUtils.safeURIEncode(userName);
		}
		return this.getUrl(url);
		
	}
	/**
	 * Constructs a concepts URL for the given name.
	 * 
	 * @param name
	 * @return The URL pointing to the concepts of the user.
	 */
	public String getConceptsUrlByString(final String name) {
		String url = this.projectHome + prefix + CONCEPTS_PREFIX;
		if (present(name)) {
			url += "/" + UrlUtils.safeURIEncode(name);
		}
		return this.getUrl(url);
	}

	/**
	 * Constructs a concepts URL for the given user i.e. a URL of the form
	 * /concepts/USERNAME
	 * 
	 * @param user
	 * @return The URL pointing to the concepts of the user
	 */
	public String getConceptsUrlForUser(final User user) {
		return this.getConceptsUrlByString(user.getName());
	}

	/**
	 * Constructs a concept URL for the given username and tagname, i.e. a URL
	 * of the form /concept/user/USERNAME/TAGNAME.
	 * 
	 * @param userName
	 * @param tagName
	 * @return The URL pointing to the concepts of the user with the specified
	 *         tags.
	 */
	public String getConceptUrlByUserNameAndTagName(final String userName, final String tagName) {
		String url = this.projectHome + prefix + CONCEPT_PREFIX + "/" + USER_PREFIX;
		url += "/" + UrlUtils.safeURIEncode(userName);
		url += "/" + UrlUtils.safeURIEncode(tagName);

		return this.getUrl(url);
	}
	
	/**
	 * url of the document
	 * 
	 * @param intraHash
	 * @param userName
	 * @param fileName
	 * @return returns the url of the document
	 */
	public String getDocumentUrlByIntraHashUserNameAndFileName(final String intraHash, final String userName, final String fileName){
		return this.getDocumentUrlByIntraHashUserNameFileNameAndSysUrl(intraHash, userName, fileName, this.projectHome);
	}
	
	/**
	 * url of the document for a specific system
	 * 
	 * @param intraHash
	 * @param userName
	 * @param fileName 
	 * @param systemUrl
	 * @return returns the url of the document for the specified system
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getDocumentUrlByIntraHashUserNameFileNameAndSysUrl(final String intraHash, final String userName, final String fileName, final String systemUrl){
		String url = systemUrl + prefix + DOCUMENT_PREFIX + "/" + intraHash;
		if (present(userName)) {
			url += "/" + UrlUtils.safeURIEncode(userName);
		}
		url += "/" + UrlUtils.safeURIEncode(fileName);
		
		return this.getUrl(url);
		
	}

	/**
	 * Constructs a URL with the posts of all users you are following, i.e.
	 * /followers
	 * 
	 * @return URL pointing to the posts of the users you are following.
	 */
	public String getFollowersUrl() {
		String url = this.projectHome + prefix + FOLLOWERS_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a friend URL for the given username, i.e. /friend/USERNAME
	 * 
	 * @param userName
	 * @return URL pointing to the posts viewable for friends of User with name
	 *         username.
	 */
	public String getFriendUrlByUserName(final String userName) {
		return this.getFriendUrlByUserNameAndSysUrl(userName, this.projectHome);
	}

	/**
	 * Constructs a friend URL for the given username and systemurl i.e.
	 * /friend/USERNAME
	 * 
	 * @param userName
	 * @param systemUrl
	 * @return URL pointing to the posts viewable for friends of User with
	 *         username.
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getFriendUrlByUserNameAndSysUrl(final String userName, final String systemUrl) {
		String url = systemUrl + prefix + FRIEND_PREFIX + "/";
		url += UrlUtils.safeURIEncode(userName);
		return this.getUrl(url);
	}

	/**
	 * Constructs a friend URL for the given username and tagname, i.e.
	 * /friend/USERNAME/TAGNAME
	 * 
	 * @param userName
	 * @param tagName
	 * @return URL pointing to the posts viewable for friends of User with name
	 *         username and tag tagName.
	 */
	public String getFriendUrlByUserNameAndTagName(final String userName, final String tagName) {
		String url = this.getFriendUrlByUserName(userName);
		url += "/" + UrlUtils.safeURIEncode(tagName);
		return this.getUrl(url);
	}

	/**
	 * @param interHash
	 * @param resourceType
	 * @return URL pointing to the publication represented by the inter hash and
	 *         the resource type
	 */
	public String getCommunityPostUrlByInterHash(final String interHash, final String resourceType) {
		if (BOOKMARK.equalsIgnoreCase(resourceType)) {
			return this.getCommunityBookmarkUrlByInterHash(interHash);
		}
		return this.getCommunityPublicationUrlByInterHash(interHash);
	}

	/**
	 * @param interHash
	 * @param resourceType
	 * @param systemUrl
	 * @return URL pointing to the publication represented by the inter hash,
	 *         the resource type and the system url
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getCommunityPostUrlByInterHashAndSysUrl(final String interHash, final String resourceType, final String systemUrl) {
		if (BOOKMARK.equalsIgnoreCase(resourceType)) {
			return this.getCommunityBookmarkUrlByInterHashAndSysUrl(interHash, systemUrl);
		}
		return this.getCommunityPublicationUrlByInterHashAndSysUrl(interHash, systemUrl);
	}

	/**
	 * Constructs a URL for a community publication specified by its inter hash
	 * and system url.
	 * 
	 * @param interHash
	 * @param systemUrl
	 * @return URL pointing to the publication represented by the inter hash and
	 *         system url
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getCommunityPublicationUrlByInterHashAndSysUrl(final String interHash, final String systemUrl) {
		return this.getCommunityPublicationUrlByInterHashUsernameAndSysUrl(interHash, null, systemUrl);
	}

	/**
	 * Constructs a URL for a community publication specified by its inter hash.
	 * 
	 * @param interHash
	 * @return URL pointing to the publication represented by the inter hash
	 */
	public String getCommunityPublicationUrlByInterHash(final String interHash) {
		return this.getCommunityPublicationUrlByInterHashUsernameAndSysUrl(
				interHash, null, this.projectHome);
	}

	/**
	 * Constructs a URL for a community publication specified by its inter hash,
	 * the username and the system url. If no username is present, it will not
	 * occur in the URL and the trailing '/' will be omitted.
	 * 
	 * @param interHash
	 * @param userName
	 * @param systemUrl
	 * @return URL pointing to the goldstandard publication represented by the
	 *         interHash and the userName
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getCommunityPublicationUrlByInterHashUsernameAndSysUrl(
			final String interHash, final String userName,
			final String systemUrl) {
		return getCommunityPostUrlByInterHashUsernameAndSysUrl(interHash,
				userName, systemUrl, false);
	}

	/**
	 * Constructs a URL for a community publication specified by its inter hash.
	 * 
	 * @param interHash
	 * @return URL pointing to the publication represented by the inter hash
	 */
	public String getCommunityBookmarkUrlByInterHash(final String interHash) {
		return this.getCommunityBookmarkUrlByInterHashUsernameAndSysUrl(
				interHash, null, this.projectHome);
	}

	/**
	 * Constructs a URL for a community publication specified by its inter hash
	 * and system url.
	 * 
	 * @param interHash
	 * @param systemUrl
	 * @return URL pointing to the publication represented by the inter hash
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getCommunityBookmarkUrlByInterHashAndSysUrl(
			final String interHash, final String systemUrl) {
		return this.getCommunityBookmarkUrlByInterHashUsernameAndSysUrl(
				interHash, null, systemUrl);
	}

	/**
	 * Constructs a URL for a goldstandard publication specified by its inter
	 * hash and systemUrl.
	 * 
	 * @param interHash
	 * @param systemUrl
	 * @return URL pointing to the publication represented by the inter hash
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getGoldstandardUrlByInterHashAndSysUrl(
			final String interHash, final String systemUrl) {
		return this.getGoldstandardUrlByInterHashUsernameAndSysUrl(interHash,
				null, systemUrl);
	}

	/**
	 * Constructs a URL for a community publication specified by its inter hash
	 * and the username. If no username is present, it will not occur in the URL
	 * and the trailing '/' will be omitted.
	 * 
	 * @param interHash
	 * @param userName
	 * @param systemUrl
	 * @return URL pointing to the goldstandard publication represented by the
	 *         interHash and the userName
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getCommunityBookmarkUrlByInterHashUsernameAndSysUrl(
			final String interHash, final String userName,
			final String systemUrl) {
		return getCommunityPostUrlByInterHashUsernameAndSysUrl(interHash,
				userName, systemUrl, true);
	}
	
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	private String getCommunityPostUrlByInterHashUsernameAndSysUrl(
			final String interHash, final String userName,
			final String systemUrl, boolean bookmark) {
		return this.getUrl(systemUrl
				+ prefix
				+ getPartialPostUrlByInterHashAndUserName(interHash, userName,
						bookmark));
	}

	private String getPartialPostUrlByInterHashAndUserName(final String hash,
			final String userName, boolean bookmark) {
		String urlPart = (bookmark ? BOOKMARK_PREFIX : PUBLICATION_PREFIX)
				+ "/" + hash;

		if (present(userName))
			return this
					.getUrl(urlPart + "/" + UrlUtils.safeURIEncode(userName));

		return urlPart;
	}

	/**
	 * The URL to the history of a post
	 * 
	 * @param hash
	 * @param userName
	 * @param resourceType TODO: should not be string
	 * @return
	 */
	public String getHistoryURLByHashAndUserName(final String hash,
			final String userName, String resourceType) {
		return this.getHistoryURLByHashUserNameAndSysUrl(hash, userName,
				resourceType, this.projectHome);
	}
	
	/**
	 * @param post
	 * @return the history url for a community post
	 */
	public String getHistoryUrlForPost(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		final Class<? extends Resource> resourceType = resource.getClass();
		final String interHash = resource.getInterHash();
		
		// XXX: not nice
		if (resourceType == GoldStandardPublication.class) {
			return this.getHistoryUrlForCommunityPublication(interHash);
		}
		
		if (resourceType == GoldStandardBookmark.class) {
			return this.getHistoryUrlForCommunityBookmark(interHash);
		}
		
		final String intraHash = resource.getIntraHash();
		final String name = post.getUser().getName();
		if (resourceType == Bookmark.class) {
			return this.getHistoryUrlForBookmark(intraHash, name);
		}
		
		if (resourceType == BibTex.class) {
			return this.getHistoryUrlForPublication(intraHash, name);
		}
		
		throw new UnsupportedResourceTypeException();
	}

	/**
	 * @param intraHash
	 * @param userName
	 * @return
	 */
	private String getHistoryUrlForBookmark(String intraHash, String userName) {
		return this.getUrl(this.projectHome + prefix + HISTORY_PREFIX + "/" + BOOKMARK_PREFIX + "/" + intraHash + "/" + userName);
	}
	
	/**
	 * 
	 * @param intraHash
	 * @param userName
	 * @return
	 */
	private String getHistoryUrlForPublication(String intraHash, String userName) {
		return this.getUrl(this.projectHome + prefix + HISTORY_PREFIX + "/" + PUBLICATION_PREFIX + "/" + intraHash + "/" + userName);
	}

	/**
	 * @param hash
	 * @return
	 */
	private String getHistoryUrlForCommunityBookmark(final String hash) {
		return this.getUrl(this.projectHome + prefix + HISTORY_PREFIX + "/" + BOOKMARK_PREFIX + "/" + hash);
	}

	/**
	 * @param hash
	 * @return
	 */
	private String getHistoryUrlForCommunityPublication(final String hash) {
		return this.getUrl(this.projectHome + prefix + HISTORY_PREFIX + "/" + PUBLICATION_PREFIX + "/" + hash);
	}

	/**
	 * The URL to the history of a post of the specific system
	 * 
	 * @param hash
	 * @param userName
	 * @param resourceType
	 * @param systemUrl
	 * @return
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getHistoryURLByHashUserNameAndSysUrl(final String hash,
			final String userName, String resourceType, final String systemUrl) {
		return this.getUrl(systemUrl
				+ prefix
				+ HISTORY_PREFIX
				+ "/"
				+ getPartialPostUrlByInterHashAndUserName(hash, userName,
						BOOKMARK.equalsIgnoreCase(resourceType)));
	}

	/**
	 * Constructs a URL for a goldstandard publication specified by its inter
	 * hash and the username and systemUrl
	 * 
	 * @param interHash
	 * @param userName
	 * @param systemUrl
	 * @return URL pointing to the goldstandard publication represented by the
	 *         interHash and the userName
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getGoldstandardUrlByInterHashUsernameAndSysUrl(
			final String interHash, final String userName,
			final String systemUrl) {
		String url = systemUrl + prefix + PUBLICATION_PREFIX + "/" + interHash;

		if (present(userName))
			return this.getUrl(url + "/" + UrlUtils.safeURIEncode(userName));

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the groups page
	 * 
	 * @return URL pointing to the groups page
	 */
	public String getGroupsUrl() {
		String url = this.projectHome + prefix + GROUPS;
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the group's page.
	 * 
	 * @param groupName
	 * @return The URL for the group's page.
	 */
	public String getGroupUrlByGroupName(final String groupName) {
		String url = this.projectHome + prefix + GROUP_PREFIX + "/"
				+ UrlUtils.safeURIEncode(groupName);
		return this.getUrl(url);
	}

	public String getGroupSettingsUrlByGroupName(final String groupName) {
		String url = this.projectHome + prefix + "settings" + "/"
				+ GROUP_PREFIX + "/" + UrlUtils.safeURIEncode(groupName);
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the group's page for all posts tagged with tagName
	 * 
	 * @param groupName
	 * @param tagName
	 * @return URL pointing to the site of the group with all posts tagged with
	 *         tagName
	 */
	public String getGroupUrlByGroupNameAndTagName(final String groupName,
			final String tagName) {
		String url = this.getGroupUrlByGroupName(groupName);
		url += "/" + UrlUtils.safeURIEncode(tagName);

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the login page
	 * 
	 * @return URL pointing to the login page
	 */
	public String getLoginUrl() {
		String url = this.projectHome + prefix + LOGIN_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the register page
	 * 
	 * @return URL pointing to the register page
	 */
	public String getRegisterUrl() {
		final String url = this.projectHome + prefix + REGISTER;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL pointing to the bibtex-bookmarks and publications of the
	 * user, i.e. /myBibTex
	 * 
	 * @return URL pointing to the bookmarks and publications of the user
	 */
	public String getMyBibTexUrl() {
		String url = this.projectHome + prefix + MYBIBTEX_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL pointing to the documents of the user i.e. /myDocuments
	 * 
	 * @return URL pointing to the documents of the user
	 */
	public String getMyDocumentsUrl() {
		String url = this.projectHome + prefix + MYDOCUMENTS_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL pointing to the duplicates of the user i.e.
	 * /myDuplicates
	 * 
	 * @return URL pointing to the duplicates of the user
	 */
	public String getMyDuplicatesUrl() {
		String url = this.projectHome + prefix + MYDUPLICATES_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL pointing to the bookmarks and publications of the user,
	 * i.e. /myHome
	 * 
	 * @return URL pointing to the bookmarks and publications of the user
	 */
	public String getMyHomeUrl() {
		String url = this.projectHome + prefix + MYHOME_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL pointing to the relations of the user i.e. /myRelations
	 * 
	 * @return URL pointing to the relations of the user
	 */
	public String getMyRelationsUrl() {
		String url = this.projectHome + prefix + MYRELATIONS_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL pointing to the fast user search, i.e. /mySearch
	 * 
	 * @return URL pointing to the user search
	 */
	public String getMySearchUrl() {
		String url = this.projectHome + prefix + MYSEARCH_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for the given resource's intrahash. If you have the post
	 * as object, please use {@link #getPostUrl(Post)}.
	 * 
	 * @param resourceType
	 *            - The type of resource. Currently, only URLs for
	 *            {@link Bookmark} or {@link BibTex} are supported.
	 * @param intraHash
	 * @param userName
	 * @return The URL pointing to the post of that user for the resource
	 *         represented by the given intrahash.
	 */
	public String getPostUrl(final Class<?> resourceType,
			final String intraHash, final String userName) {
		if (resourceType == Bookmark.class) {
			return this.getBookmarkUrlByIntraHashAndUsername(intraHash,
					userName);
		} else if (resourceType == BibTex.class) {
			return this.getPublicationUrlByIntraHashAndUsername(intraHash,
					userName);
		} else {
			throw new UnsupportedResourceTypeException();
		}
	}

	/**
	 * Returns the URL which represents a post. Depending on the type of the
	 * resource, this forwarded to {@link #getBookmarkUrl(Bookmark, User)} and
	 * {@link #getPublicationUrl(BibTex, User)}.
	 * 
	 * @param post
	 *            - The post for which the URL should be constructed. User and
	 *            resources must not be null.
	 * @return The URL representing the given post.
	 */
	public String getPostUrl(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		if (resource instanceof Bookmark) {
			return this.getBookmarkUrl(((Bookmark) resource), post.getUser());
		} else if (resource instanceof BibTex) {
			return this.getPublicationUrl(((BibTex) resource), post.getUser());
		} else {
			throw new UnsupportedResourceTypeException();
		}
	}

	/**
	 * @return the projectHome
	 */
	public String getProjectHome() {
		return this.projectHome;
	}

	/**
	 * @return URL to all publications of the main page in bibtex formats.
	 */
	public String getPublicationsAsBibtexUrl() {
		String url = this.projectHome + prefix + BIBTEXEXPORT_PREFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL to all publications of the publication page of the user
	 * with name userName, i.e. /bib/user/USERNAME
	 * 
	 * @param userName
	 * @return URL pointing to publications in bibtex format of user with name
	 *         userName
	 */
	public String getPublicationsAsBibtexUrlByUserName(final String userName) {
		String url = getPublicationsAsBibtexUrl();
		url += "/" + USER_PREFIX;
		url += "/" + UrlUtils.safeURIEncode(userName);

		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for the given resource and user. If no user is given,
	 * the URL points to all posts for that resource.
	 * 
	 * @param publication
	 *            - must have proper inter and intra hashes (a call to
	 *            {@link Resource#recalculateHashes()} might be necessary but is
	 *            not done by this method)
	 * 
	 * @param user
	 *            - if null, the URL to all posts for the given publication is
	 *            returned.
	 * @return - The URL which represents the given publication.
	 */
	public String getPublicationUrl(final BibTex publication, final User user) {
		if (!present(user) || !present(user.getName())) {
			/*
			 * If a user name is given, return the url to that users post
			 * (intrahash + username) otherwise return the URL to the resources
			 * page (interhash)
			 */
			String url = this.projectHome + PUBLICATION_PREFIX + "/"
					+ PUBLICATION_INTER_HASH_ID + publication.getInterHash();
			return this.getUrl(url);
		}
		String url = this.projectHome + prefix + PUBLICATION_PREFIX + "/"
				+ PUBLICATION_INTRA_HASH_ID + publication.getIntraHash() + "/"
				+ UrlUtils.safeURIEncode(user.getName());
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for all the publications with the specified BibTeX key,
	 * i.e. /bibtexkey/BIBTEXKEY
	 * 
	 * @param bibtexKey
	 * @return URL pointing to all publications with BibTeX key bibtexKey
	 */
	public String getPublicationUrlByBibTexKey(final String bibtexKey) {
		String url = this.projectHome + BIBTEXKEY_PREFIX;
		url += "/" + UrlUtils.safeURIEncode(bibtexKey);

		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for all the publications with the specified BibTeX key
	 * and username, i.e. /bibtexkey/BIBTEXKEY/USERNAME
	 * 
	 * @param bibtexKey
	 * @param userName
	 * @return URL pointing to all publications with BibTeX key bibtexKey and
	 *         user name userName
	 */
	public String getPublicationUrlByBibTexKeyAndUserName(
			final String bibtexKey, final String userName) {
		String url = this.getPublicationUrlByBibTexKey(bibtexKey);
		url += "/" + UrlUtils.safeURIEncode(userName);

		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for a publication specified by its inter hash.
	 * 
	 * @param interHash
	 * @return URL pointing to the publication represented by the inter hash
	 */
	public String getPublicationUrlByInterHash(final String interHash) {
		return this.getPublicationUrlByInterHashAndUsername(interHash, null);
	}

	/**
	 * Constructs a URL for a publication specified by its inter hash and system.
	 * 
	 * @param interHash
	 * @param systemUrl 
	 * @return URL pointing to the publication represented by the inter hash and system url
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getPublicationUrlByInterHashAndSysUrl(final String interHash, final String systemUrl) {
		return this.getPublicationUrlByInterHashUsernameAndSysUrl(interHash, null, systemUrl);
	}
	
	/**
	 * Constructs a URL for a publication specified by its inter hash and the
	 * username. If no username is present, it will not occur in the URL and the
	 * trailing '/' will be omitted.
	 * 
	 * @param interHash
	 * @param userName
	 * @return URL pointing to the publication represented by the interHash and
	 *         the userName
	 */
	public String getPublicationUrlByInterHashAndUsername(
			final String interHash, final String userName) {
		return this.getPublicationUrlByInterHashUsernameAndSysUrl(interHash, userName, this.projectHome);
	}
	
	/**
	 * Constructs a URL for a publication specified by its inter hash, the
	 * username and the system. If no username is present, it will not occur in the URL and the
	 * trailing '/' will be omitted.
	 * 
	 * @param interHash
	 * @param userName
	 * @param systemUrl 
	 * @return URL pointing to the publication represented by the interHash,
	 *         the userName and the system url
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getPublicationUrlByInterHashUsernameAndSysUrl(
			final String interHash, final String userName, final String systemUrl) {
		String url = systemUrl + prefix + PUBLICATION_PREFIX + "/"
				+ PUBLICATION_INTER_HASH_ID + interHash;

		if (present(userName))
			return this.getUrl(url + "/" + UrlUtils.safeURIEncode(userName));

		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for a publication specified by its intra hash.
	 * 
	 * @param intraHash
	 * @return URL pointing to the publication represented by the intra hash
	 */
	public String getPublicationUrlByIntraHash(final String intraHash) {
		return this.getPublicationUrlByIntraHashAndUsername(intraHash, null);
	}
	
	/**
	 * Constructs a URL for a publication specified by its intra hash and system.
	 * 
	 * @param intraHash
	 * @param systemUrl 
	 * @return URL pointing to the publication represented by the intra hash and the specified system
	 */
	@Deprecated // TODO: remove (use proper configured URLGenerator)
	public String getPublicationUrlByIntraHashAndSysUrl(final String intraHash, final String systemUrl) {
		return this.getPublicationUrlByIntraHashUsernameAndSysUrl(intraHash, null, systemUrl);
	}

	/**
	 * Constructs a URL for a publication specified by its intra hash and the
	 * username. If no username is present, it will not occur in the URL and the
	 * trailing '/' will be omitted.
	 * 
	 * @param intraHash
	 * @param userName
	 * @return URL pointing to the publication represented by the intraHash and
	 *         the userName
	 */
	public String getPublicationUrlByIntraHashAndUsername(final String intraHash, final String userName) {
		return this.getPublicationUrlByIntraHashUsernameAndSysUrl(intraHash, userName, this.projectHome);
	}

	/**
	 * Constructs a URL for a publication specified by its intra hash, the
	 * username and the system
	 * 
	 * @param intraHash
	 * @param userName
	 * @param systemUrl
	 * @return URL pointing to the publication represented by the intraHash and
	 *         the userName
	 */
	@Deprecated // TODO: remove (use properly configured URLGenerator)
	public String getPublicationUrlByIntraHashUsernameAndSysUrl(
			final String intraHash, final String userName,
			final String systemUrl) {
		String url = systemUrl + prefix + PUBLICATION_PREFIX + "/"
				+ PUBLICATION_INTRA_HASH_ID + intraHash;

		if (present(userName))
			return this.getUrl(url + "/" + UrlUtils.safeURIEncode(userName));
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for a publication specified by its post
	 * 
	 * @param post
	 * @return URL pointing to the publication represented by the intraHash and
	 *         the userName
	 */
	public String getPublicationUrlByPost(final Post<BibTex> post) {
		final User user = post.getUser();
		if (present(user)) {
			return this.getPublicationUrlByIntraHashAndUsername(post
					.getResource().getIntraHash(), user.getName());
		}

		return this.getPublicationCommunityUrlByInterHash(post.getResource()
				.getInterHash());
	}

	/**
	 * @param interHash
	 * @return the link to the community post
	 */
	public String getPublicationCommunityUrlByInterHash(String interHash) {
		final String url = this.projectHome + this.prefix + PUBLICATION_PREFIX
				+ "/" + interHash;
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL for the relevant posts for a group.
	 * 
	 * @param groupName
	 * @return URL pointing to the page with posts relevant for the group with
	 *         name groupName.
	 */
	public String getRelevantForUrlByGroupName(final String groupName) {
		String url = this.projectHome + prefix + RELEVANTFOR_PREFIX + "/"
				+ GROUP_PREFIX;
		url += "/" + UrlUtils.safeURIEncode(groupName);
		return this.getUrl(url);
	}
	
	/**
	 * Constructs a search URL for the requested search string.
	 * 
	 * @param toSearch
	 * @param searchScope the search type such as 'group', 'search', 'sharedResourceSearch'
	 * @return URL pointing to the results of the search.
	 */
	public String getSearchUrl(final String toSearch, String searchScope) {
		String searchPrefix = SEARCH_PREFIX;
		if (SHARED_RESOURCE_SEARCH_PREFIX.equals(searchScope)) {
			searchPrefix = SHARED_RESOURCE_SEARCH_PREFIX;
		}
		String url = this.projectHome + prefix + searchPrefix + "/"
				+ UrlUtils.safeURIEncode(toSearch);
		return this.getUrl(url);
	}

	/**
	 * Constructs a search URL for the requested search string.
	 * 
	 * @param toSearch
	 * @return URL pointing to the results of the search.
	 */
	public String getSearchUrl(final String toSearch) {
		return getSearchUrl(toSearch, SEARCH_PREFIX);
	}

	/**
	 * Returns just the url for settings.
	 * 
	 * @return settings url
	 */
	public String getSettingsUrl() {
		String url = this.projectHome + prefix + SETTINGS_PREFIX;

		return this.getUrl(url);
	}

	/**
	 * Returns a specific page of the settings url. TODO: Make sure that the
	 * HTTP parameters are acceptable like that (with ?).
	 * 
	 * @param selTab
	 *            the selected tab to be shown
	 * @return settings url with seltab
	 */
	public String getSettingsUrlWithSelectedTab(int selTab) {
		String url = this.getSettingsUrl() + "?selTab=" + selTab;

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the tag's page.
	 * 
	 * @param tagName
	 * @return The URL for the tag's page.
	 */
	public String getTagUrlByTagName(final String tagName) {
		String url = this.projectHome + prefix + TAG_PREFIX;
		if (present(tagName)) {
			url += "/" + UrlUtils.safeURIEncode(tagName);
		}
		return this.getUrl(url);
	}

	/**
	 * If {@link #checkUrls} is <code>true</code>, each given string is
	 * converted into a {@link URL} (if that fails, <code>null</code> is
	 * returned). Otherwise, the given string is returned as is.
	 * 
	 * @param url
	 * @return
	 */
	private String getUrl(final String url) {
		if (this.checkUrls) {
			try {
				return new URL(url).toString();
			} catch (final MalformedURLException ex) {
				// FIXME!
				return null;
			}
		}
		return url;
	}

	/**
	 * Constructs the URL for the picture of a user.
	 * 
	 * @param userName
	 * @return The URL to the picture of the user.
	 */
	public String getUserPictureUrlByUsername(final String userName) {
		String url = this.projectHome + prefix + PICTURE_PREFIX + "/"
				+ USER_PREFIX + "/" + UrlUtils.safeURIEncode(userName);
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the user's page.
	 * 
	 * @param user
	 * @return The URL for the user's page.
	 */
	public String getUserUrl(final User user) {
		return this.getUserUrlByUserName(user.getName());
	}

	/**
	 * Constructs the URL for the user's page.
	 * 
	 * @param userName
	 * @return The URL for the user's page.
	 */
	public String getUserUrlByUserName(final String userName) {
		return this.getUserUrlByUserNameAndSysUrl(userName, this.projectHome);
	}

	/**
	 * Constructs the URL for the user's page for a specified system
	 * 
	 * @param userName
	 * @param systemUrl
	 * @return The URL for the user's page for the system
	 */
	public String getUserUrlByUserNameAndSysUrl(final String userName,
			final String systemUrl) {
		String url = systemUrl + prefix + USER_PREFIX + "/"
				+ UrlUtils.safeURIEncode(userName);
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the user's page with all posts tagged with tagName
	 * 
	 * @param userName
	 * @param tagName
	 * @return The URL for the user's page with all posts tagged with tagName
	 */
	public String getUserUrlByUserNameAndTagName(final String userName,
			final String tagName) {
		return this.getUserUrlByUserNameTagNameAndSysUrl(userName, tagName, this.projectHome);
	}

	/**
	 * Constructs the URL for the user's page with all posts tagged with tagName
	 * for a specified system
	 * 
	 * @param userName
	 * @param tagName
	 * @param systemUrl
	 * @return The URL for the user's page with all posts tagged with tagName
	 *         and systemUrl
	 */
	public String getUserUrlByUserNameTagNameAndSysUrl(final String userName,
			final String tagName, final String systemUrl) {
		String url = this.getUserUrlByUserNameAndSysUrl(userName, systemUrl);
		url += "/" + UrlUtils.safeURIEncode(tagName);
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the posts viewable for friends, i.e.
	 * /viewable/friends
	 * 
	 * @return URL pointing to the viewable posts for friends
	 */
	public String getViewableFriendsUrl() {
		String url = this.getProjectHome() + prefix + VIEWABLE_PREFIX + "/"
				+ VIEWABLE_FRIENDS_SUFFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the posts viewable for friends tagged with
	 * tagName, i.e. /viewable/friends/TAGNAME
	 * 
	 * @param tagName
	 * 
	 * @return URL pointing to the viewable posts for friends tagged with
	 *         tagName
	 */
	public String getViewableFriendsUrlByTagName(final String tagName) {
		String url = this.getViewableFriendsUrl();
		url += "/" + UrlUtils.safeURIEncode(tagName);

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the posts viewable for public i.e.
	 * /viewable/public
	 * 
	 * @return URL pointing to the public viewable posts
	 */
	public String getViewablePublicUrl() {
		String url = this.getProjectHome() + prefix + VIEWABLE_PREFIX + "/"
				+ VIEWABLE_PUBLIC_SUFFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the posts viewable for public tagged with tagName
	 * i.e. /viewable/public/TAGNAME
	 * 
	 * @param tagName
	 * 
	 * @return URL pointing to the public viewable posts tagged with tagName
	 */
	public String getViewablePublicUrlByTagName(final String tagName) {
		String url = this.getViewablePublicUrl();
		url += "/" + UrlUtils.safeURIEncode(tagName);

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the posts viewable for private
	 * 
	 * @return URL pointing to the private viewable posts
	 */
	public String getViewablePrivateUrl() {
		String url = this.getProjectHome() + prefix + VIEWABLE_PREFIX + "/"
				+ VIEWABLE_PRIVATE_SUFFIX;
		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for the posts viewable for private tagged with tagName
	 * i.e. /viewable/private/TAGNAME
	 * 
	 * @param tagName
	 * 
	 * @return URL pointing to the private viewable posts tagged with tagName
	 */
	public String getViewablePrivateUrlByTagName(final String tagName) {
		String url = this.getViewablePrivateUrl();
		url += "/" + UrlUtils.safeURIEncode(tagName);

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for all viewable posts of a group, i.e.
	 * /viewable/GROUPNAME
	 * 
	 * @param groupName
	 * @return the URL for all viewable posts of a group.
	 */
	public String getViewableUrlByGroupName(final String groupName) {
		String url = this.projectHome + prefix + VIEWABLE_PREFIX;
		url += "/" + UrlUtils.safeURIEncode(groupName);

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for all viewable posts of a group of a specific system
	 * i.e. /viewable/GROUPNAME
	 * 
	 * @param groupName
	 * @param systemurl
	 * @return the URL for all viewable posts of a group.
	 */
	public String getViewableUrlByGroupNameAndSysUrl(final String groupName,
			final String systemurl) {
		String url = systemurl + prefix + VIEWABLE_PREFIX;
		url += "/" + UrlUtils.safeURIEncode(groupName);

		return this.getUrl(url);
	}

	/**
	 * Constructs the URL for all viewable posts of a group tagged with tagName
	 * 
	 * @param groupName
	 * @param tagname
	 * @return the URL for all viewable posts of a group tagged with tagName
	 */
	public String getViewableUrlByGroupNameAndTagName(final String groupName,
			final String tagname) {
		String url = this.getViewableUrlByGroupName(groupName);
		url += "/" + UrlUtils.safeURIEncode(tagname);

		return this.getUrl(url);
	}

	/**
	 * @see URLGenerator#setCheckUrls(boolean)
	 * @return checkUrls
	 */
	public boolean isCheckUrls() {
		return this.checkUrls;
	}

	/**
	 * Checks if the given URL points to the given page. Useful for checking the
	 * referrer header.
	 * 
	 * @param url
	 * @param page
	 * @return <code>true</code> if the given URL points to the given page.
	 */
	public boolean matchesPage(final String url, final Page page) {
		final String pageName = page.getPath();
		final String absoluteUrl = this.getAbsoluteUrl(pageName);
		return (url != null) && url.contains(absoluteUrl);
	}

	/**
	 * Checks if the given URL points to the given resource.
	 * 
	 * @param url
	 *            - the URL that should be checked.
	 * @param userName
	 *            - the owner of the resource
	 * @param intraHash
	 *            - the intra hash of the resource
	 * @return <code>true</code> if the url points to the resource.
	 */
	public boolean matchesResourcePage(final String url, final String userName,
			final String intraHash) {
		if (!present(url)) {
			return false;
		}
		return url.matches(".*/(" + PUBLICATION_PREFIX + "|" + BOOKMARK_PREFIX
				+ ")/[0-3]?" + intraHash + "/" + userName + ".*");
	}

	/**
	 * @param prefixToUse
	 *            the prefix to use for this url generator
	 * @return the url generator
	 */
	public URLGenerator prefix(String prefixToUse) {
		if (present(prefixToUse)) {
			if (!prefixToUse.endsWith("/")) {
				prefixToUse += "/";
			}
		}
		this.prefix = prefixToUse;
		return this;
	}

	/**
	 * @param post
	 *            adds all misc field urls to the bibtex in this post
	 */
	public void setBibtexMiscUrls(final Post<BibTex> post) {
		post.getResource().addMiscField(
				BibTexUtils.ADDITIONAL_MISC_FIELD_BIBURL,
				this.getPublicationUrl(post.getResource(), post.getUser())
						.toString());
	}

	/**
	 * If set to <code>true</code>, all generated URLs are put into {@link URL}
	 * objects. If that fails, <code>null</code> is returned. The default is
	 * <code>false</code> such that no checking occurs.
	 * 
	 * @param checkUrls
	 */
	public void setCheckUrls(final boolean checkUrls) {
		this.checkUrls = checkUrls;
	}

	/**
	 * @param post
	 * @return the rating url of the provided post
	 */
	public String getCommunityRatingUrl(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		final String interHash = resource.getInterHash();
		final String userName = post.getUser().getName();
		final String intraHash = resource.getIntraHash();
		if (resource instanceof Bookmark) {
			return this.getBookmarkRatingUrl(interHash, userName, intraHash);
		} else if (resource instanceof BibTex) {
			return this.getPublicationRatingUrl(interHash, userName, intraHash);
		} else {
			throw new UnsupportedResourceTypeException();
		}
	}

	/**
	 * Constructs a URL to rate the new publication for the given publication
	 * and user name.
	 * 
	 * @param interHash
	 * @param userName
	 * @param intraHash
	 * @return The URL pointing to rating the post of that user for the
	 *         publication represented by the given inter and intra hashes.
	 */
	public String getPublicationRatingUrl(final String interHash,
			final String userName, final String intraHash) {
		final String url = this.projectHome + PUBLICATION_PREFIX + "/"
				+ PUBLICATION_INTER_HASH_ID + interHash + "?postOwner="
				+ UrlUtils.safeURIEncode(userName) + "&amp;intraHash="
				+ intraHash + "#discussionbox";
		return this.getUrl(url);
	}

	/**
	 * Constructs a URL to rate the new bookmark for the given bookmark and user
	 * name.
	 * 
	 * @param interHash
	 * @param userName
	 * @param intraHash
	 * @return The URL pointing to rating the post of that user for the bookmark
	 *         represented by the given inter and intra hashes.
	 */
	public String getBookmarkRatingUrl(final String interHash,
			final String userName, final String intraHash) {
		final String url = this.projectHome + BOOKMARK_PREFIX + "/" + interHash
				+ "?postOwner=" + UrlUtils.safeURIEncode(userName)
				+ "&amp;intraHash=" + intraHash + "#discussionbox";
		return this.getUrl(url);
	}

	/**
	 * ProjectHome defaults to <code>/</code>, such that relative URLs are
	 * generated. Note that this does not work with
	 * {@link #setCheckUrls(boolean)} set to <code>true</code>, since
	 * {@link URL} does not support relative URLs (or more correctly: relative
	 * URLs are not URLs).
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(final String projectHome) {
		this.projectHome = projectHome;
	}
}