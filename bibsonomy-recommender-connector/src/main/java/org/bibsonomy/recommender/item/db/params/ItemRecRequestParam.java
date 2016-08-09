package org.bibsonomy.recommender.item.db.params;

/**
 * Parameter used to retrieve similar users from the database.
 */
public class ItemRecRequestParam {

	private int count;
	private String userName;
	private String tag;
	
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the requestingUserName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param requestingUserName the requestingUserName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the tag name
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tagname to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
}
