package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.model.LuceneIndexStatistics;

/**
 * Bean for classifier settings
 * 
 * @author Sven Stefani
 * @version $Id: LuceneIndexSettingsCommand.java,v 1.2 2008-04-07 13:25:48
 *          ss05sstuetzer Exp $
 */
public class LuceneIndexSettingsCommand {

	private String instance;
	private int numDocs;
	private int numDeletedDocs;
	private String newestDate;
	private long lastModified;
	private long currentVersion;
	private boolean isCurrent;

	/**
	 * Constructor
	 */
	public LuceneIndexSettingsCommand() {
		
		/**
		 * initialize options
		 */
		
	}

	
	
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