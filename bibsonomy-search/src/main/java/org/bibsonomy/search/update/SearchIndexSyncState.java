package org.bibsonomy.search.update;

/**
 * common interface for all search index sync states
 *
 * @author dzo
 */
public abstract class SearchIndexSyncState {

	private String mappingVersion;

	/**
	 * @return the mappingVersion
	 */
	public String getMappingVersion() {
		return mappingVersion;
	}

	/**
	 * @param mappingVersion the mappingVersion to set
	 */
	public void setMappingVersion(String mappingVersion) {
		this.mappingVersion = mappingVersion;
	}
}
