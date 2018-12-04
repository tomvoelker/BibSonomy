package org.bibsonomy.database.params.group;

/**
 * parameter class to insert groups recursively
 *
 * @author ada
 */
public class GetParentGroupIdsRecursively {

	private String username;
	private String groupname;

	public GetParentGroupIdsRecursively(String username, String groupname) {
		this.username = username;
		this.groupname = groupname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
}
