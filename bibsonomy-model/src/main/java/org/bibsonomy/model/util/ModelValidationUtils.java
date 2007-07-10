package org.bibsonomy.model.util;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;

/**
 * Sanity checks for the model.
 * 
 * @author Manuel Bork
 * @author Christian Schenk
 * @version $Id$
 */
public class ModelValidationUtils {

	private static final String XML_IS_INVALID_MSG = "The body part of the received XML document is not valid: ";

	public static void checkTag(final Tag tag) throws InvalidModelException {
		if (tag.getName() == null || tag.getName().length() == 0) {
			throw new InvalidModelException("found a tag without tagname assigned.");
		}
	}

	public static void checkUser(final User user) throws InvalidModelException {
		if (user.getName() == null || user.getName().length() == 0) {
			throw new InvalidModelException("found an user without username assigned.");
		}
	}

	public static void checkGroup(final Group group) throws InvalidModelException {
		if (group.getName() == null || group.getName().length() == 0) {
			throw new InvalidModelException("found a group without username assigned.");
		}
	}

	public static void checkBookmark(final Bookmark bookmark) throws InvalidModelException {
		if (bookmark.getUrl() == null || bookmark.getUrl().length() == 0) {
			throw new InvalidModelException("found a bookmark without url assigned.");
		}
		if (bookmark.getInterHash() == null || bookmark.getInterHash().length() == 0 || bookmark.getIntraHash() == null || bookmark.getIntraHash().length() == 0) {
			throw new InvalidModelException("found a bookmark without hash assigned.");
		}
	}

	public static void checkBibtex(final BibTex bibtex) {
		if (bibtex.getTitle() == null || bibtex.getTitle().length() == 0) {
			throw new InvalidModelException("found a bibtex without title assigned.");
		}
	}

	/*
	 * XXX: InvalidModelException was an:
	 * 
	 * public InvalidXMLException(final String message) {
	 *   super("The body part of the received XML document is not valid: " + message);
	 * }
	 * 
	 * Probably we want to have something similar again. Maybe we want a generic
	 * ValidationException and an InvalidModelException and InvalidXMLException
	 * inheriting from that.
	 */

	public static void checkTag(final TagType xmlTag) throws InvalidModelException {
		if (xmlTag.getName() == null || xmlTag.getName().length() == 0) {
			throw new InvalidModelException(XML_IS_INVALID_MSG + "tag name is missing");
		}
	}

	public static void checkUser(final UserType xmlUser) throws InvalidModelException {
		if (xmlUser.getName() == null || xmlUser.getName().length() == 0) {
			throw new InvalidModelException(XML_IS_INVALID_MSG + "username is missing");
		}
	}

	public static void checkGroup(final GroupType xmlGroup) throws InvalidModelException {
		if (xmlGroup.getName() == null || xmlGroup.getName().length() == 0) {
			throw new InvalidModelException(XML_IS_INVALID_MSG + "groupname is missing");
		}
	}

	public static void checkPost(final PostType xmlPost) throws InvalidModelException {
		if (xmlPost.getTag() == null) throw new InvalidModelException(XML_IS_INVALID_MSG + "list of tags is missing");
		if (xmlPost.getTag().size() == 0) throw new InvalidModelException(XML_IS_INVALID_MSG + "no tags specified");
		if (xmlPost.getUser() == null) throw new InvalidModelException(XML_IS_INVALID_MSG + "user is missing");

		final BibtexType xmlBibtex = xmlPost.getBibtex();
		final BookmarkType xmlBookmark = xmlPost.getBookmark();
		if (xmlBibtex == null && xmlBookmark == null) {
			throw new InvalidModelException(XML_IS_INVALID_MSG + "resource is missing");
		} else if (xmlBibtex != null && xmlBookmark != null) {
			throw new InvalidModelException(XML_IS_INVALID_MSG + "only one resource is allowed");
		} else {
			// just fine (bibtex xor bookmark):
			// ( xmlBibtex == null && xmlBookmark != null ) || ( xmlBibtex != null || xmlBookmark == null )
		}
	}

	public static void checkBookmark(final BookmarkType xmlBookmark) throws InvalidModelException {
		if (xmlBookmark.getUrl() == null) throw new InvalidModelException(XML_IS_INVALID_MSG + "url is missing");
		// do not test hash value - it depends on the request if its available,
		// so we check it later
		// if( xmlBookmark.getIntrahash() == null ) throw new
		// InvalidXMLException( "hash is missing" );
	}

	public static void checkBibTex(final BibtexType xmlBibtex) throws InvalidModelException {
		if (xmlBibtex.getTitle() == null) throw new InvalidModelException(XML_IS_INVALID_MSG + "title is missing");
		// do not test hash value - it depends on the request if its available,
		// so we check it later
		// if( xmlBibtex.getIntrahash() == null ) throw new InvalidXMLException(
		// "hash is missing" );
	}
}