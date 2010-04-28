package org.bibsonomy.webapp.command.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bibsonomy.lucene.param.LuceneIndexStatistics;

/**
 * Bean for classifier settings
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class LuceneIndexSettingsCommand {

	private String instance;
	private int numDocs;
	private int numDeletedDocs;
	private String newestDate;
	private long lastModified;
	private String lastModifiedString;
	private long currentVersion;
	private String currentVersionString;
	private boolean isCurrent;
	
	/**
	 * @param indexStatistics
	 */
	public void setIndexStatistics (LuceneIndexStatistics indexStatistics){
		setNumDocs(indexStatistics.getNumDocs());
		setNumDeletedDocs(indexStatistics.getNumDeletedDocs());
		setNewestDate(indexStatistics.getNewestRecordDate());
		setLastModified(indexStatistics.getLastModified());
		setCurrentVersion(indexStatistics.getCurrentVersion());
	}
	
	/**
	 * @return the instance
	 */
	public String getInstance() {
		return this.instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(String instance) {
		this.instance = instance;
	}

	/**
	 * @return the newestDate
	 */
	public String getNewestDate() {
		return this.newestDate;
	}

	/**
	 * @param newestDate the newestDate to set
	 */
	public void setNewestDate(String newestDate) {
		this.newestDate = newestDate;
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
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,S");
		setLastModifiedString(df.format(lastModified));
	}

	/**
	 * @return the currentVersion
	 */
	public long getCurrentVersion() {
		return this.currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(long currentVersion) {
		this.currentVersion = currentVersion;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,S");
		setCurrentVersionString(df.format(lastModified));
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
	
	/**
	 * @return the lastModifiedString
	 */
	public String getLastModifiedString() {
		return this.lastModifiedString;
	}

	/**
	 * @param lastModifiedString the lastModifiedString to set
	 */
	private void setLastModifiedString(String lastModifiedString) {
		this.lastModifiedString = lastModifiedString;
	}

	/**
	 * @return the currentVersionString
	 */
	public String getCurrentVersionString() {
		return this.currentVersionString;
	}

	/**
	 * @param currentVersionString the currentVersionString to set
	 */
	private void setCurrentVersionString(String currentVersionString) {
		this.currentVersionString = currentVersionString;
	}
}