package org.bibsonomy.ibatis.params.generic;


import org.bibsonomy.ibatis.enums.ConstantID;


/**
 * Define class for sql request: show me all bibtex entries of a given friend, define parameters for sql-request
 * -- aggregates about all users, if current user is equals to friend
 * 
 * @author mgr
 *
 */





public abstract class ByUserFriendsBookmark{
	
	
	
	private String User;
	private ConstantID groupType;
	private int itemCount;
	private int startBook;
	
	
	
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