package org.bibsonomy.lucene.param;

/**
 * bean for configuring lucene via JNDI
 * 
 * @author fei
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
	
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	public String getIndexPath() {
		return indexPath;
	}              
	public void setEnableUpdater(String enableUpdater) {
		this.enableUpdater = Boolean.valueOf(enableUpdater);
	}
	public String getEnableUpdater() {
		return enableUpdater.toString();
	}
	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}
	public String getSearchMode() {
		return searchMode;
	}              
	public void setLoadIndexIntoRam(String loadIndexIntoRam) {
		this.loadIndexIntoRam = Boolean.valueOf(loadIndexIntoRam);
	}
	public String getLoadIndexIntoRam() {
		return loadIndexIntoRam.toString();
	}
	public void setMaximumFieldLength(String maximumFieldLength) {
		this.maximumFieldLength = maximumFieldLength;
	}
	public String getMaximumFieldLength() {
		return maximumFieldLength;
	}
	public void setDbDriverName(String dbDriverName) {
		this.dbDriverName = dbDriverName;
	}
	public String getDbDriverName() {
		return dbDriverName;
	}
	public void setRedundantCnt(String redundantCnt) {
		this.redundantCnt = redundantCnt;
	}
	public String getRedundantCnt() {
		return redundantCnt;
	}

	
}
