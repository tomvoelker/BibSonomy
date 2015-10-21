package org.bibsonomy.search.util;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <M> 
 */
public class Mapping<M> {
	private String type;
	
	private M mappingInfo;

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the mappingInfo
	 */
	public M getMappingInfo() {
		return this.mappingInfo;
	}

	/**
	 * @param mappingInfo the mappingInfo to set
	 */
	public void setMappingInfo(M mappingInfo) {
		this.mappingInfo = mappingInfo;
	}
}
