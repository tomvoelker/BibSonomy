package org.bibsonomy.webdav.resource;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * This class is the logical root of the resource hierarchy.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class RootCollectionResource extends BibSonomyCollectionResource {

//	private final BookmarkCollectionResource bookmarkCollectionResource;
	private final BibTexCollectionResource bibtexCollectionResource;

	/**
	 * Constructor.
	 * 
	 * @param backend
	 *            The backend
	 */
	public RootCollectionResource(final BibSonomyBackend backend) {
		super(null, backend, "", "RootCollectionResource");
//		this.bookmarkCollectionResource = new BookmarkCollectionResource(this, backend);
		this.bibtexCollectionResource = new BibTexCollectionResource(this, backend);
	}

	public List<Resource> getChildren() {
		final List<Resource> children = new ArrayList<Resource>();
//		children.add(this.bookmarkCollectionResource);
		children.add(this.bibtexCollectionResource);
		return children;
	}

	public Resource getChild(final String name) {
		/*if (this.bookmarkCollectionResource.getName().equals(name)) {
			return this.bookmarkCollectionResource;
		} else*/ if (this.bibtexCollectionResource.getName().equals(name)) {
			return this.bibtexCollectionResource;
		}
		return null;
	}
}