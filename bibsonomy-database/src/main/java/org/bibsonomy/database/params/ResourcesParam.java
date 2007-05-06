package org.bibsonomy.database.params;

import java.util.List;

import org.bibsonomy.model.Resource;

/**
 * Super class for parameter objects that are about resources.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class ResourcesParam<T extends Resource> extends GenericParam {
	/** A list of resources. */
	private List<T> resources;

	public List<T> getResources() {
		return this.resources;
	}

	public void setResources(List<T> resources) {
		this.resources = resources;
	}
}