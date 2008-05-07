package org.bibsonomy.webdav.resource;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class StandardFilesCollectionResource extends VirtualCollectionResource {

	/** Special file that contains all publications */
	protected static final String ALL_BIBTEX = "bibtex-all.bib";
	/** Special file that contains all bookmarks */
	protected static final String ALL_BOOKMARK = "bookmarks.html";

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
	public StandardFilesCollectionResource(final BibSonomyCollectionResource root, final BibSonomyBackend backend, final String name, final String displayName) {
		super(root, backend, name, displayName);
	}

	public Resource getChild(final String name) {
		if (ALL_BIBTEX.equals(name)) {
			final List<Post<BibTex>> posts = this.getBackend().getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, Order.ADDED, null, 0, 10, null);
			final String allBibTex = this.getAllBibTex(posts);
			return new BibSonomyResource(this, this.getBackend(), ALL_BIBTEX, allBibTex);
		} else if (ALL_BOOKMARK.equals(name)) {
			final List<Post<Bookmark>> posts = this.getBackend().getLogicInterface().getPosts(Bookmark.class, GroupingEntity.ALL, null, null, null, Order.ADDED, null, 0, 10, null);
			final String allBookmark = this.getAllBookmark(posts);
			return new BibSonomyResource(this, this.getBackend(), ALL_BOOKMARK, allBookmark);
		}
		return null;
	}

	public List<Resource> getChildren() {
		final List<Resource> children = new ArrayList<Resource>();
		children.add(new BibSonomyResource(this, this.getBackend(), ALL_BIBTEX, ""));
		children.add(new BibSonomyResource(this, this.getBackend(), ALL_BOOKMARK, ""));
		return children;
	}

	protected String getAllBibTex(final List<Post<BibTex>> posts) {
		final StringBuilder buffer = new StringBuilder();
		for (final Post<BibTex> post : posts) {
			buffer.append(BibTexUtils.toBibtexString(post.getResource()) + "\n\n");
		}
		return buffer.toString();
	}

	/*
	 * FIXME: Netscape compatible export...
	 */
	protected String getAllBookmark(final List<Post<Bookmark>> posts) {
		final StringBuilder buffer = new StringBuilder();
		for (final Post<Bookmark> post : posts) {
			buffer.append(post.getResource().getUrl() + "<br/>\n");
		}
		return buffer.toString();
	}
}