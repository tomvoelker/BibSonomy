package org.bibsonomy.database.params;

/**
 * @author dzo
 * @version $Id$
 */
public class GoldStandardReferenceParam {
	private String hash;
	private String refHash;
	private String username;
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	/**
	 * @return the refHash
	 */
	public String getRefHash() {
		return this.refHash;
	}
	
	/**
	 * @param refHash the refHash to set
	 */
	public void setRefHash(String refHash) {
		this.refHash = refHash;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
