package org.bibsonomy.webdav.resource;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * TODO: implement me...
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkCollectionResource extends VirtualCollectionResource {

	/**
	 * Constructs the new resource.
	 * 
	 * @param root
	 *            The resource's parent.
	 * @param backend
	 *            The backend
	 */
	public BookmarkCollectionResource(final RootCollectionResource root, final BibSonomyBackend backend) {
		super(root, backend, "Bookmarks", "Bookmarks");
	}

	public Resource getChild(final String name) {
		return null;
	}

	public List<Resource> getChildren() {
		final List<Resource> children = new ArrayList<Resource>();
		return children;
	}
}