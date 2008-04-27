package org.bibsonomy.webdav.resource;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.CollectionResource;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class VirtualCollectionResource extends BibSonomyCollectionResource {

	/**
	 * Constructs the new resource.
	 * 
	 * @param parent
	 *            The resource's parent. May be <code>null</code>.
	 * @param backend
	 *            The backend
	 * @param name
	 *            The name of this resource
	 * @param displayName
	 *            The displayName of this resource
	 */
	public VirtualCollectionResource(final CollectionResource parent, final BibSonomyBackend backend, final String name, final String displayName) {
		super(parent, backend, name, displayName);
	}

	@Override
	public boolean isVirtual() {
		return true;
	}
}