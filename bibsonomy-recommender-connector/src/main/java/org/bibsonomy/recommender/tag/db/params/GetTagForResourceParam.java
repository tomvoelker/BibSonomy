package org.bibsonomy.recommender.tag.db.params;

/**
 * Parameter to retrieve tags belonging to a resource.
 */
public class GetTagForResourceParam {

	private String userName;
	private String hash;
	private int range;
	private int timestamp;
	
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
	 * @return the username
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param username the username to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}
	/**
	 * @param range the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
