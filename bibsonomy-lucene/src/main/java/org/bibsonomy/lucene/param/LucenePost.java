package org.bibsonomy.lucene.param;

import java.util.Date;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;


/**
 * Lucene Post class, extending the model class with index
 * management fields.
 * 
 * @author fei
 *
 * @param <R>
 */
public class LucenePost<R extends Resource> extends Post<R> {

        private static final long serialVersionUID = 6167951235868739450L;

	/** newest tas_id during last index update */
	private Integer lastTasId;

	/** newest log_date during last index update */
	private Date lastLogDate;
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setLastTasId(Integer lastTasId) {
		this.lastTasId = lastTasId;
	}

	public Integer getLastTasId() {
		return lastTasId;
	}

	public void setLastLogDate(Date lastLogDate) {
		this.lastLogDate = lastLogDate;
	}

	public Date getLastLogDate() {
		return lastLogDate;
	}
	
}
