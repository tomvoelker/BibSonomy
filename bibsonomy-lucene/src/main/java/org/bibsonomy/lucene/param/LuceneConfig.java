package org.bibsonomy.lucene.param;

/**
 * bean for configuring lucene via JNDI
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneConfig {
	/** configure search mode (lucene/database) */
	private String searchMode;
	/** base path to the indices */
	private String indexPath;
	/** enable/disable index updater */
	private Boolean enableUpdater = false;
	/** indicate whether the index should be loaded into the ram */
	private Boolean loadIndexIntoRam = false;
	/** determing maximal field length for lucene fields */
	private String maximumFieldLength;
	/** db driver name - FIXME: only needed for offline index creation */
	private String dbDriverName;
	/** nr. of redundant indeces */
	private String redundantCnt = "2";
	/** enable/disable tag clouds on search pages */
	private Boolean enableTagClouds = false;
	/** number of posts to consider for building the tag cloud */
	private String tagCloudLimit = "1000";
	
	/**
	 * @return the indexPath
	 */
	public String getIndexPath() {
		return indexPath;
	}
	
	/**
	 * @param indexPath the indexPath to set
	 */
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	
	/**
	 * @param enableUpdater the string representation of enableUpdater
	 */
	public void setEnableUpdater(String enableUpdater) {
		this.enableUpdater = Boolean.valueOf(enableUpdater);
	}
	
	/**
	 * @return the string representation of enableUpdater
	 */
	public String getEnableUpdater() {
		return enableUpdater.toString();
	}
	
	/**
	 * @return the enableTagClouds
	 */
	public Boolean getEnableTagClouds() {
		return enableTagClouds;
	}

	/**
	 * @param enableTagClouds the enableTagClouds to set
	 */
	public void setEnableTagClouds(Boolean enableTagClouds) {
		this.enableTagClouds = enableTagClouds;
	}

	/**
	 * @return the searchMode
	 */
	public String getSearchMode() {
		return searchMode;
	}

	/**
	 * @param searchMode the searchMode to set
	 */
	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	/**
	 * @param loadIndexIntoRam the string representation of loadINtexIntoRam
	 */
	public void setLoadIndexIntoRam(String loadIndexIntoRam) {
		this.loadIndexIntoRam = Boolean.valueOf(loadIndexIntoRam);
	}
	
	/**
	 * @return the string representation of loadIndexIntoRam
	 */
	public String getLoadIndexIntoRam() {
		return loadIndexIntoRam.toString();
	}
	
	/**
	 * @return the maximumFieldLength
	 */
	public String getMaximumFieldLength() {
		return maximumFieldLength;
	}

	/**
	 * @param maximumFieldLength the maximumFieldLength to set
	 */
	public void setMaximumFieldLength(String maximumFieldLength) {
		this.maximumFieldLength = maximumFieldLength;
	}
	
	/**
	 * @return the dbDriverName
	 */
	public String getDbDriverName() {
		return dbDriverName;
	}

	/**
	 * @param dbDriverName the dbDriverName to set
	 */
	public void setDbDriverName(String dbDriverName) {
		this.dbDriverName = dbDriverName;
	}
	
	/**
	 * @return the redundantCnt
	 */
	public String getRedundantCnt() {
		return redundantCnt;
	}

	/**
	 * @param redundantCnt the redundantCnt to set
	 */
	public void setRedundantCnt(String redundantCnt) {
		this.redundantCnt = redundantCnt;
	}

	/**
	 * @return the tagCloudLimit
	 */
	public String getTagCloudLimit() {
		return tagCloudLimit;
	}

	/**
	 * @param tagCloudLimit the tagCloudLimit to set
	 */
	public void setTagCloudLimit(String tagCloudLimit) {
		this.tagCloudLimit = tagCloudLimit;
	}
}
