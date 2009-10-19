package org.bibsonomy.lucene.database.params;

import java.util.List;

import org.bibsonomy.model.Resource;

/**
 * Super class for parameter objects that are about resources.
 * 
 * @param <T> resource (e.g. Bookmark, BibTex, etc.)
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class ResourcesParam<T extends Resource> extends GenericParam {
	
	/** A list of resources. */
	private List<T> resources;

	
	public ResourcesParam() {
		super();
	}
	
	/**
	 * @return resources
	 */
	public List<T> getResources() {
		return this.resources;
	}

	/**
	 * @param resources
	 */
	public void setResources(List<T> resources) {
		this.resources = resources;
	}

}