package org.bibsonomy.ibatis.params.generic;

import org.bibsonomy.ibatis.enums.ConstantID;



/**
 * by a given Bibtex (hash), it is returned a list of BibTex entries
 * 
 * @author mgr
 *
 */



public abstract class ByBibhash{
	
	private ConstantID simValue;
	private ConstantID groupType;
	protected ConstantID contentType;
	private String requBibtex;
	private String requSim;
	private int itemCount;
	private int startBib;

	
	
	public ByBibhash() {
		
		this.simValue=ConstantID.SIM_HASH;
		this.groupType=ConstantID.GROUP_PUBLIC;
	}

	
	public abstract int getContentType();
     
	public String getRequBibtex() {
		return requBibtex;
	}


    public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}


	public String getRequSim() {
		return requSim;
	}


	public void setRequSim(String requSim) {
		this.requSim = requSim;
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


	public int getGroupType() {
		return groupType.getId() ;
	}


	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}


	public int getSimValue() {
		return this.simValue.getId();
	}


	public void setSimValue(ConstantID simValue) {
		this.simValue = simValue;
	}
	
	
	
	
	
}