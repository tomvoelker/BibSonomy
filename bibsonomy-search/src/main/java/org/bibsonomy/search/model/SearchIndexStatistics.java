package org.bibsonomy.search.model;

import java.util.Date;

/**
 *
 * @author dzo
 */
public class SearchIndexStatistics {

	private Date newestRecordDate;
	private long lastTasId;

	/**
	 * @return the newestRecordDate
	 */
	public Date getNewestRecordDate() {
		return this.newestRecordDate;
	}

	/**
	 * @param newestRecordDate the newestRecordDate to set
	 */
	public void setNewestRecordDate(final Date newestRecordDate) {
		this.newestRecordDate = newestRecordDate;
	}

	public long getLastTasId() {
		return this.lastTasId;
	}

	public void setLastTasId(long lastTasId) {
		this.lastTasId = lastTasId;
	}
	
}
