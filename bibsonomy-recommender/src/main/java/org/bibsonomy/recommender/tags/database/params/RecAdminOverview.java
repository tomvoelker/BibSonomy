package org.bibsonomy.recommender.tags.database.params;


/**
 * @author bsc
 * @version $Id$
 */
public class RecAdminOverview {
	
	private int settingID, latency;
	private String recID;
	
	
	public void setSettingID(int settingID){
	    this.settingID = settingID;
	}
	public int getSettingID(){
		return this.settingID;
	}
	
	public void setLatency(int latency){
		this.latency = latency;
	}
	public int getLatency(){
		return this.latency;
	}
	
	public void setRecID(String recID){
		this.recID = recID;
	}
	public String getRecID(){
		return this.recID;
	}
}
