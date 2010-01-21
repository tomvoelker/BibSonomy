package org.bibsonomy.recommender.tags.database.params;

/**
 * @author bsc
 * @version $Id$
 */
public class LatencyParam {
    private Long sid;
    private Long numberOfQueries;
    
    public LatencyParam(Long sid, Long numberOfQueries){
    	this.setSettingID(sid);
    	this.setNumberOfQueries(numberOfQueries);
    }
    
    public void setSettingID(Long sid){
    	this.sid = sid;
    }
    public Long getSettingID(){
    	return this.sid;
    }
    
    public void setNumberOfQueries(Long numberOfQueries){
    	this.numberOfQueries = numberOfQueries;
    }
    public Long getNumberOfQueries(){
    	return this.numberOfQueries;
    }
}
