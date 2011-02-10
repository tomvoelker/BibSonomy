package org.bibsonomy.model.synch;

import java.util.Date;

/**
 * @author wla
 * @version $Id$
 */
public abstract class SynchronizationResource {
    
    /**
     * Create date of this resource.
     */
    private Date createDate;
    
    private Date changeDate;
    
    
    private SynchronizationStates state;
    
    /**
     * @param resource
     * @return true if resources are same
     */
    public abstract boolean same(SynchronizationResource resource);
    
    /**
     * @param createDate the create date and time of this resource to set
     */
    public void setCreateDate(Date createDate) {
	this.createDate = createDate;
    }

    /**
     * @return the create date of this resource to set
     */
    public Date getCreateDate() {
	return createDate;
    }

    /**
     * @param changeDate the date and time of the last change of this resource to set
     */
    public void setChangeDate(Date changeDate) {
	this.changeDate = changeDate;
    }

    /**
     * @return date and time of the last change of this resource
     */
    public Date getChangeDate() {
	return changeDate;
    }

    /**
     * @param state the synchronization state to set
     */
    public void setStatus(SynchronizationStates state) {
	this.state = state;
    }

    /**
     * @return the state 
     */
    public SynchronizationStates getState() {
	return state;
    }
    
}
