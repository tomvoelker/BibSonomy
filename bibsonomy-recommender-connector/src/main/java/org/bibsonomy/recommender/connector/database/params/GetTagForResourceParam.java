package org.bibsonomy.recommender.connector.database.params;

/**
 * Parameter to retrieve tags belonging to a resource.
 */
public class GetTagForResourceParam {

	private String userName;
	private int id;
	private int range;
	private int timestamp;
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param i the id to set
	 */
	public void setId(int i) {
		this.id = i;
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
