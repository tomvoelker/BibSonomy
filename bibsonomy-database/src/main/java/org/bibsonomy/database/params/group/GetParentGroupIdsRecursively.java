package org.bibsonomy.database.params.group;

/**
 * parameter class to insert groups recursively
 *
 * @author ada
 */
public class GetParentGroupIdsRecursively {

	private String username;
	private String groupname;

	/**
	 * default constructor
	 * @param username
	 * @param groupname
	 */
	public GetParentGroupIdsRecursively(String username, String groupname) {
		this.username = username;
		this.groupname = groupname;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the groupname
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * @param groupname the groupname to set
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
}
