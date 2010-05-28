package org.bibsonomy.lucene.database.params;

import java.util.Date;
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
	
	/** newest tas_id during last index update */
	private Integer lastTasId;

	/** newest change_date during last index update */
	private Date lastLogDate;
	
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

	/**
	 * @return the lastTasId
	 */
	public Integer getLastTasId() {
		return lastTasId;
	}

	/**
	 * @param lastTasId the lastTasId to set
	 */
	public void setLastTasId(Integer lastTasId) {
		this.lastTasId = lastTasId;
	}

	/**
	 * @return the lastLogDate
	 */
	public Date getLastLogDate() {
		return lastLogDate;
	}

	/**
	 * @param lastLogDate the lastLogDate to set
	 */
	public void setLastLogDate(Date lastLogDate) {
		this.lastLogDate = lastLogDate;
	}
}