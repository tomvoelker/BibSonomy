package org.bibsonomy.ibatis.params.generic;


import org.bibsonomy.ibatis.enums.ConstantID;




/**
 * @author mgr
 *
 */
/*
 * Define class for sql request: show me all bookmark entries of a given friend, define paramters for sql request
 * PAGE_FRIEND -- aggregiert über alle User, bei denen currUser Friend ist 
 */
public abstract class ByUserFriendsBookmark{
	
	
	
	private String User;
	private ConstantID groupType;
	private int itemCount;
	private int startBook;
	private int startBib;
	
	
	
	public ByUserFriendsBookmark(){
		
		this.groupType=ConstantID.GROUP_FRIENDS;
		
		}

	public ConstantID getGroupType() {
		return groupType;
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

	public int getStartBook() {
		return startBook;
	}

	public void setStartBook(int startBook) {
		this.startBook = startBook;
	}

	public String getUser() {
		return User;
	}

	public void setUser(String user) {
		User = user;
	}
	
	
	
	
	
	
	
}