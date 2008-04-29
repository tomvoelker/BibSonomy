package org.bibsonomy.webdav.resource;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexCollectionResource extends VirtualCollectionResource {

	/** Special file that aggregates all publications */
	private static final String ALL_BIBTEX = "ALL.bib";

	/**
	 * Constructs the new resource.
	 * 
	 * @param root
	 *            The resource's parent.
	 * @param backend
	 *            The backend
	 */
	public BibTexCollectionResource(final RootCollectionResource root, final BibSonomyBackend backend) {
		super(root, backend, "bibtex", "Publications");
	}

	public Resource getChild(final String name) {
		// contains all publications
		if (ALL_BIBTEX.equals(name)) {
			final List<Post<BibTex>> posts = this.getBackend().getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);
			final StringBuilder buffer = new StringBuilder();
			for (final Post<BibTex> post : posts) {
				buffer.append(BibTexUtils.toBibtexString(post.getResource()) + "\n\n");
			}
			return new BibTexResource(this, this.getBackend(), ALL_BIBTEX, buffer.toString());
		}

		final Post<BibTex> post = this.getBibTex(name);
		if (post != null) {
			return new BibTexResource(this, this.getBackend(), post.getResource().getTitle() + ".txt", BibTexUtils.toBibtexString(post.getResource()));
		}

		return null;
	}

	/*
	 * Retrieves a single post FIXME: inefficient...
	 */
	private final Post<BibTex> getBibTex(final String title) {
		final List<Post<BibTex>> posts = this.getBackend().getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);
		for (final Post<BibTex> post : posts) {
			if (post.getResource().getTitle().equals(title.substring(0, title.length() - ".txt".length()))) return post;
		}
		return null;
	}

	public List<Resource> getChildren() {
		final List<Resource> children = new ArrayList<Resource>();

		// contains all publications
		children.add(new BibTexResource(this, this.getBackend(), ALL_BIBTEX, ""));

		final List<Post<BibTex>> posts = this.getBackend().getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);
		for (final Post<BibTex> post : posts) {
			children.add(new BibTexResource(this, this.getBackend(), post.getResource().getTitle() + ".txt", ""));
		}
		return children;
	}
}