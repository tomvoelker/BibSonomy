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

	/** start date */
	Date fromDate;
	
	/** end date */
	Date toDate;
	
	/** newest tas_id during last index update */
	private Integer lastTasId;

	/** newest change_date during last index update */
	private Date lastLogDate;
	
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
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getFromDate() {
		return fromDate;
	}
	
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setLastTasId(Integer lastTasId) {
		this.lastTasId = lastTasId;
	}

	public Integer getLastTasId() {
		return lastTasId;
	}

	public void setLastLogDate(Date lastLogDate) {
		this.lastLogDate = lastLogDate;
	}

	public Date getLastLogDate() {
		return lastLogDate;
	}
}