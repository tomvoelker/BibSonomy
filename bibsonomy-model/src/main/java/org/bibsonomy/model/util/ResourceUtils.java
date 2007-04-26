package org.bibsonomy.model.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

public class ResourceUtils {

	/** To set groupId in case of spam detection. Use logical OR to set 2nd bit */
	private static final int CONST_SET_1ST_BIT = 0x80000000;
	/** To set/clear first bit of an integer. Use logical AND to clear 2nd bit */
	private static final int CONST_CLEAR_1ST_BIT = 0x7FFFFFFF;

	/**
	 * Calculates the MD5-Hash of a String s and returns it encoded as a hex
	 * string of 32 characters length.
	 * 
	 * @param s
	 *            the string to be hashed
	 * @return the MD5 hash of s as a 32 character string
	 */
	public static String hash(final String s) {
		if (s == null) {
			return null;
		} else {
			final String charset = "UTF-8";
			try {
				final MessageDigest md = MessageDigest.getInstance("MD5");
				return toHexString(md.digest(s.getBytes(charset)));
			} catch (final UnsupportedEncodingException e) {
				return null;
			} catch (final NoSuchAlgorithmException e) {
				return null;
			}
		}
	}

	/**
	 * Converts a buffer of bytes into a string of hex values.
	 * 
	 * @param buffer
	 *            array of bytes which should be converted
	 * @return hex string representation of buffer
	 */
	public static String toHexString(final byte[] buffer) {
		final StringBuffer rVal = new StringBuffer();
		for (int i = 0, n = buffer.length; i < n; i++) {
			String hex = Integer.toHexString((int) buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			rVal.append(hex.substring(hex.length() - 2));
		}
		return rVal.toString();
	}

	/**
	 * In case of spam detection
	 */
	public static int getGroupId(final int groupId, final boolean isSpammer) {
		if (isSpammer) {
			return groupId | CONST_SET_1ST_BIT;
		} else {
			// NOTE: "return groupId" is not enough, since we want to use that
			// to unflag spammers posts, as well
			return groupId & CONST_CLEAR_1ST_BIT;
		}
	}

	/**
	 * TODO implement doUpdate Recommender
	 */
	public static void doUpdate(final List<Tag> oldResourceTags, final Bookmark bookmark) {
	}

	/**
	 * Convenience method to print a list of bookmarks.
	 */
	protected void printBookmarks(final List<Bookmark> bookmarks) {
		// TODO doesn't fit the model anymore
//		for (final Bookmark bookmark : bookmarks) {
//			System.out.println("ContentId   : " + bookmark.getContentId());
//			System.out.println("Description : " + bookmark.getDescription());
//			System.out.println("Extended    : " + bookmark.getExtended());
//			System.out.println("Date        : " + bookmark.getDate());
//			System.out.println("URL         : " + bookmark.getUrl());
//			System.out.println("URLHash     : " + bookmark.getHash());
//			System.out.println("UserName    : " + bookmark.getUserName());
//			System.out.print("Tags        : ");
//			for (final Tag tag : bookmark.getTags()) {
//				System.out.print(tag.getName() + " ");
//			}
//			System.out.println("\n");
//		}

//		for (final Post<Resource> post : list) {
//			final Bookmark resource = (Bookmark) post.getResource();
//			System.out.println(resource.getContentId());
//			System.out.println(resource.getUrlHash());
//			System.out.println("Tags: " + post.getTags());
//			System.out.println("User: " + post.getUser().getName());
//			System.out.println("-----------------------------");
//		}
	}

	/**
	 * Convenience method to print a list of BibTexs.
	 */
	protected void printBibTex(final List<BibTex> bibtexs) {
		// TODO doesn't fit the model anymore
//		for (final BibTex bibtex : bibtexs) {
//			System.out.println("Address          : " + bibtex.getAddress());
//			System.out.println("Annote           : " + bibtex.getAnnote());
//			System.out.println("Author           : " + bibtex.getAuthor());
//			System.out.println("BibTexAbstract   : " + bibtex.getBibtexAbstract());
//			System.out.println("BibTexKey        : " + bibtex.getBibtexKey());
//			System.out.println("BKey             : " + bibtex.getBKey());
//			System.out.println("Booktitle        : " + bibtex.getBooktitle());
//			System.out.println("Chapter          : " + bibtex.getChapter());
//			System.out.println("Crossref         : " + bibtex.getCrossref());
//			System.out.println("Day              : " + bibtex.getDay());
//			System.out.println("Description      : " + bibtex.getDescription());
//			System.out.println("Edition          : " + bibtex.getEdition());
//			System.out.println("Editor           : " + bibtex.getEditor());
//			System.out.println("Entrytype        : " + bibtex.getEntrytype());
//			System.out.println("HowPublished     : " + bibtex.getHowpublished());
//			System.out.println("Instution        : " + bibtex.getInstitution());
//			System.out.println("Journal          : " + bibtex.getJournal());
//			System.out.println("Misc             : " + bibtex.getMisc());
//			System.out.println("Month            : " + bibtex.getMonth());
//			System.out.println("Note             : " + bibtex.getNote());
//			System.out.println("Number           : " + bibtex.getNumber());
//			System.out.println("Organization     : " + bibtex.getOrganization());
//			System.out.println("Pages            : " + bibtex.getPages());
//			System.out.println("Publisher        : " + bibtex.getPublisher());
//			System.out.println("School           : " + bibtex.getSchool());
//			System.out.println("Series           : " + bibtex.getSeries());
//			System.out.println("Title            : " + bibtex.getTitle());
//			System.out.println("UserName         : " + bibtex.getUserName());
//			System.out.println("Volume           : " + bibtex.getVolume());
//			System.out.println("Year             : " + bibtex.getYear());
//			System.out.println("Url              : " + bibtex.getUrl());
//			System.out.print("Tags             : ");
//			for (final Tag tag : bibtex.getTags()) {
//				System.out.print(tag.getName() + " ");
//			}
//			System.out.println("\n");
//		}
	}
}