package org.bibsonomy.lucene.param;

import java.util.Date;


/**
 * lucene statistics like current version, number of docs
 *  
 * @author sst
 * @version $Id$
 */
public class LuceneIndexStatistics {

	private Date newestRecordDate;
	private int numDocs = 0;
	private int numDeletedDocs = 0;
	private Date lastModified;
	private long currentVersion = 0;
	private boolean isCurrent = true;
	
	/**
	 * @return the currentVersion
	 */
	public long getCurrentVersion() {
		return this.currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(final long currentVersion) {
		this.currentVersion = currentVersion;
	}

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

	/**
	 * @return the numDocs
	 */
	public int getNumDocs() {
		return this.numDocs;
	}

	/**
	 * @param numDocs the numDocs to set
	 */
	public void setNumDocs(final int numDocs) {
		this.numDocs = numDocs;
	}

	/**
	 * @return the numDeletedDocs
	 */
	public int getNumDeletedDocs() {
		return this.numDeletedDocs;
	}

	/**
	 * @param numDeletedDocs the numDeletedDocs to set
	 */
	public void setNumDeletedDocs(final int numDeletedDocs) {
		this.numDeletedDocs = numDeletedDocs;
	}
	
	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return this.lastModified;
	}
	
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(final Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the isCurrent
	 */
	public boolean isCurrent() {
		return this.isCurrent;
	}

	/**
	 * @param isCurrent the isCurrent to set
	 */
	public void setCurrent(final boolean isCurrent) {
		this.isCurrent = isCurrent;
	}	
}