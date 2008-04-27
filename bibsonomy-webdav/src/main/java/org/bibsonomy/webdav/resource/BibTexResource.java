package org.bibsonomy.webdav.resource;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.CollectionResource;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexResource extends BibSonomyResource {

	/**
	 * Constructs the new resource.
	 * 
	 * @param parent
	 *            The resource's parent. May be <code>null</code>.
	 * @param backend
	 *            The backend
	 * @param name
	 *            The name of this resource
	 * @param content
	 *            The content of this resource
	 */
	public BibTexResource(CollectionResource parent, BibSonomyBackend backend, String name, String content) {
		super(parent, backend, name, content);
	}
}