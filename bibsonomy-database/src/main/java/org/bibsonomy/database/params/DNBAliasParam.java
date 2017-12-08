package org.bibsonomy.database.params;

/**
 * TODO: add documentation to this class
 *
 * @author jhi
 */
public class DNBAliasParam {
	private String dnbId;
	private String otherDnbId;
	
	/**
	 * @param dnbId
	 * @param otherDnbId
	 */
	public DNBAliasParam(String dnbId, String otherDnbId) {
		this.dnbId = dnbId;
		this.otherDnbId = otherDnbId;
	}
	
	/**
	 * @return the otherDnbId
	 */
	public String getOtherDnbId() {
		return this.otherDnbId;
	}
	/**
	 * @param otherDnbId the otherDnbId to set
	 */
	public void setOtherDnbId(String otherDnbId) {
		this.otherDnbId = otherDnbId;
	}
	/**
	 * @return the dnbId
	 */
	public String getDnbId() {
		return this.dnbId;
	}
	/**
	 * @param dnbId the dnbId to set
	 */
	public void setDnbId(String dnbId) {
		this.dnbId = dnbId;
	}
	
}
