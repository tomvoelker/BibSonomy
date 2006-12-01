package org.bibsonomy.ibatis.params.generic;

import org.bibsonomy.ibatis.enums.ConstantID;

public abstract class HomePageForBookmark{
	
	
	
	private ConstantID groupType;
	private int itemCount;
	private int startBook;
	
	
	
	public HomePageForBookmark(){
		
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
	
}