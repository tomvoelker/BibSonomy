package org.bibsonomy.lucene.param;


/**
 * lucene statistics like current version, number of docs
 *  
 * @author sst
 * @version $Id$
 */
public class LuceneIndexStatistics {

	private long newestRecordDate = 0;
	private int numDocs = 0;
	private int numDeletedDocs = 0;
	private long lastModified = 0;
	private long CurrentVersion = 0;
	private boolean isCurrent = true;
	
	/**
	 * @return the currentVersion
	 */
	public long getCurrentVersion() {
		return this.CurrentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(long currentVersion) {
		this.CurrentVersion = currentVersion;
	}

	/**
	 * @return the newestRecordDate
	 */
	public long getNewestRecordDate() {
		return this.newestRecordDate;
	}

	/**
	 * @param newestRecordDate the newestRecordDate to set
	 */
	public void setNewestRecordDate(long newestRecordDate) {
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
	public void setNumDocs(int numDocs) {
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
	public void setNumDeletedDocs(int numDeletedDocs) {
		this.numDeletedDocs = numDeletedDocs;
	}
	
	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return this.lastModified;
	}
	
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
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
	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}	
}