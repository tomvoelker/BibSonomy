package org.bibsonomy.ibatis.params.generic;


import org.bibsonomy.ibatis.enums.ConstantID;



/**
 * 
 * Define class for sql request: show me all bibtex entries of a given friend, define parameters for sql-request
 * -- aggregates about all users, if current user is equals to friend
 * 
 * @author mgr
 *
 */


public abstract class ByUserFriendsBibtex{
	
	
	
	private String user;
	private ConstantID groupType;
	private ConstantID simValue;
	private int itemCount;
	private int startBib;
	
	
	public ByUserFriendsBibtex(){
		
		this.groupType=ConstantID.GROUP_FRIENDS;
		this.simValue=ConstantID.SIM_HASH;
		
		}

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public int getStartBib() {
		return startBib;
	}

	public void setStartBib(int startBib) {
		this.startBib = startBib;
	}

	

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getSimValue() {
		return simValue.getId();
	}

	public void setSimValue(ConstantID simValue) {
		this.simValue = simValue;
	}
	
	
	
	
	
	
	
}