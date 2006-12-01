package org.bibsonomy.ibatis.params.generic;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * by a given user, it is returned a list of BibTex entries
 * 
 * @author mgr
 *
 */

public abstract class ByBibtexDownload{
	
	private String User;
    private ConstantID simValue;
    
    
    public ByBibtexDownload(){
    	
    	this.simValue=ConstantID.SIM_HASH;
    	
    	
    	
    }


	public ConstantID getSimValue() {
		return simValue;
	}


	public void setSimValue(ConstantID simValue) {
		this.simValue = simValue;
	}


	public String getUser() {
		return User;
	}


	public void setUser(String user) {
		User = user;
	}
    
    
}
    
    
    
    
    
    
    
    
