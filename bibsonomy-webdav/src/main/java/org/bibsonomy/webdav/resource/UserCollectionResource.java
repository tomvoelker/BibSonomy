package org.bibsonomy.webdav.resource;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * This class represents the "home" of a single user.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class UserCollectionResource extends StandardFilesCollectionResource {

	/**
	 * Constructs the new resource.
	 * 
	 * @param root
	 *            The resource's parent.
	 * @param backend
	 *            The backend
	 * @param name
	 *            The name of this resource
	 * @param displayName
	 *            The displayName of this resource
	 */
	public UserCollectionResource(final BibSonomyCollectionResource root, final BibSonomyBackend backend, final String name, final String displayName) {
		super(root, backend, name, displayName);
	}

	@Override
	public Resource getChild(final String name) {
		if (ALL_BIBTEX.equals(name)) {
			final List<Post<BibTex>> posts = this.getBackend().getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);
			final String allBibTex = this.getAllBibTex(posts);
			return new BibSonomyResource(this, this.getBackend(), ALL_BIBTEX, allBibTex);
		} else if (ALL_BOOKMARK.equals(name)) {
			final List<Post<Bookmark>> posts = this.getBackend().getLogicInterface().getPosts(Bookmark.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);
			final String allBookmark = this.getAllBookmark(posts);
			return new BibSonomyResource(this, this.getBackend(), ALL_BOOKMARK, allBookmark);
		}
		return null;
	}
}