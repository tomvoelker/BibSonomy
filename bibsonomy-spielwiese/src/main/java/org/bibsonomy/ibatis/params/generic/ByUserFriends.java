package org.bibsonomy.ibatis.params.generic;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.common.LimitOffset;

/**
 * Define class for sql request: show me all bibtex entries of a given friend,
 * define parameters for sql-request -- aggregates about all users, if current
 * user is equals to friend
 * 
 * @author mgr
 * 
 */
public abstract class ByUserFriends extends LimitOffset {

	private String user;
	private ConstantID groupType;

	public ByUserFriends() {
		this.groupType = ConstantID.GROUP_FRIENDS;
	}

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}