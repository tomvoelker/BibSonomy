package org.bibsonomy.ibatis.params.generic;
import org.bibsonomy.ibatis.enums.ConstantID;




/**
 * define parameters for sql-statement: give me all bibtex entries of the main page
 * 
 * 
 * @author mgr
 *
 */
public abstract class HomePageForBibtex{
	
	
	
	private ConstantID groupType;
	private ConstantID simValue;
	private int itemCount;
	private int startBib;
	
	
	public  HomePageForBibtex(){
		
		this.groupType=ConstantID.GROUP_PUBLIC;
		
		
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


	public ConstantID getSimValue() {
		return simValue;
	}


	public void setSimValue(ConstantID simValue) {
		this.simValue = simValue;
	}



	public int getStartBib() {
		return startBib;
	}


	public void setStartBib(int startBib) {
		this.startBib = startBib;
	}
	
	
}