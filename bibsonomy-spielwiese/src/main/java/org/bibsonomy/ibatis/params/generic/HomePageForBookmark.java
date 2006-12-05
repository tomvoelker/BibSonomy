package org.bibsonomy.ibatis.params.generic;
import org.bibsonomy.ibatis.enums.ConstantID;


/**
 * define parameters for sql-statement: give me all bookmark entries of the main page
 * 
 * 
 * @author mgr
 *
 */





public abstract class HomePageForBookmark{
	
	
	
	private ConstantID groupType;
	private int itemCount;
	private int startBook;
	
	
	public HomePageForBookmark(){
		
		this.groupType=ConstantID.GROUP_PUBLIC;
		
		}

	public abstract int getContentType();

	public int getGroupType() {
		return groupType.getId() ;
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