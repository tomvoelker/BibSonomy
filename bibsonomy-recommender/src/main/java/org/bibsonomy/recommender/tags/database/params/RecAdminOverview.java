package org.bibsonomy.recommender.tags.database.params;


/**
 * @author bsc
 * @version $Id$
 */
public class RecAdminOverview {
	
	private Long settingID, latency;
	private String recID;
	
	
	public void setSettingID(Long settingID){
	    this.settingID = settingID;
	}
	public Long getSettingID(){
		return this.settingID;
	}
	
	public void setLatency(Long latency){
		this.latency = latency;
	}
	public Long getLatency(){
		return this.latency;
	}
	
	public void setRecID(String recID){
		this.recID = recID;
	}
	public String getRecID(){
		return this.recID;
	}
}
