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

	/**
	 * Constructs the new resource.
	 * 
	 * @param root
	 *            The resource's parent.
	 * @param backend
	 *            The backend
	 */
	public BibTexCollectionResource(final RootCollectionResource root, final BibSonomyBackend backend) {
		super(root, backend, "Publications", "Publications");
	}

	public Resource getChild(final String name) {
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

		final List<Post<BibTex>> posts = this.getBackend().getLogicInterface().getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, 0, 10, null);
		for (final Post<BibTex> post : posts) {
			children.add(new BibTexResource(this, this.getBackend(), post.getResource().getTitle() + ".txt", ""));
		}
		return children;
	}
}