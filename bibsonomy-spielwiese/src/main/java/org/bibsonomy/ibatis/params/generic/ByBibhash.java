package org.bibsonomy.ibatis.params.generic;

import org.bibsonomy.ibatis.enums.ConstantID;



/**
 * @author mgr
 *
 */

/*
 * by a given Bibtex (hash), it is returned a list of BibTex entries
 */


public abstract class ByBibhash{
	
	private ConstantID simValue;
	private ConstantID groupType;
	private String requBibtex;
	private String requSim;
	private int itemCount;
	private int startBib;

	
	
	public ByBibhash() {
		
		this.groupType = ConstantID.GROUP_PUBLIC;	
	    this.simValue=ConstantID.SIM_HASH;
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


	public ConstantID getGroupType() {
		return groupType;
	}


	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}


	public ConstantID getSimValue() {
		return simValue;
	}


	public void setSimValue(ConstantID simValue) {
		this.simValue = simValue;
	}
	
	
	
	
	
}