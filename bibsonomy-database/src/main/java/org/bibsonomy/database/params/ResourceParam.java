package org.bibsonomy.database.params;

import org.bibsonomy.model.Resource;

/** 
 * Super class for parameter objects that are about resources.
 * 
 * @param <T> resource (e.g. Bookmark, Publication, etc.)
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class ResourceParam<T extends Resource> extends GenericParam {

	protected T resource;

	/**
	 * @param resource the resource to set
	 */
	public void setResource(final T resource) {
		this.resource = resource;
	}

	/**
	 * @return the resource
	 */
	public T getResource() {
		return resource;
	}

}