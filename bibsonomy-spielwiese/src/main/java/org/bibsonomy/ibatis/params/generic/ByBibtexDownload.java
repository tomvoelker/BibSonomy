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
    protected ConstantID contentType;
    
    
    public ByBibtexDownload(){
    	
    	this.simValue=ConstantID.SIM_HASH;
   	
    	}

    public abstract int getContentType();



	
	public String getUser() {
		return User;
	}


	public void setUser(String user) {
		User = user;
	}

	public int getSimValue() {
		return this.simValue.getId();
	}

	public void setSimValue(ConstantID simValue) {
		this.simValue = simValue;
	}
   
	
    
}
    
    
    
    
    
    
    
    
