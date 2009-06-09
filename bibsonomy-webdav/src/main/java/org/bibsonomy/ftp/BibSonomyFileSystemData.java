package org.bibsonomy.ftp;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * Compiles the data used in the file system.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyFileSystemData {

	/** Handles login thus holds the LogicInterface */
	private final BibSonomyUserManager userManager;

	/**
	 * @param userManager
	 *            needed to access the LogicInterface
	 */
	public BibSonomyFileSystemData(final BibSonomyUserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * Returns all BibTeX entries.
	 * 
	 * @return BibTeXs
	 */
	public String getAllBibTex() {
		final List<Post<BibTex>> posts = this.userManager.getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);

		final StringBuilder buffer = new StringBuilder();
		for (final Post<BibTex> post : posts) {
			buffer.append(BibTexUtils.toBibtexString(post.getResource()) + "\n\n");
		}
		return buffer.toString();
	}

	/**
	 * Retuns all Bookmark entries.
	 * 
	 * FIXME: Netscape compatible export.
	 * 
	 * @return bookmarks
	 */
	public String getAllBookmark() {
		final List<Post<Bookmark>> posts = this.userManager.getLogicInterface().getPosts(Bookmark.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);

		final StringBuilder buffer = new StringBuilder();
		for (final Post<Bookmark> post : posts) {
			buffer.append(post.getResource().getUrl() + "<br/>\n");
		}
		return buffer.toString();
	}
}