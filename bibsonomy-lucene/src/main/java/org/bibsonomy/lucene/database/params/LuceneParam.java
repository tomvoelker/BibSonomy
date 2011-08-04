package org.bibsonomy.lucene.database.params;

import java.util.Date;

/**
 * Class for lucene queries
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class LuceneParam {
	
	private String userName;
	
	/** The SQL-Limit */
	private int limit;
	
	/** The SQL-Offset */
	private int offset;
	
	/** newest tas_id during last index update */
	private Integer lastTasId;
	
	private int lastContentId;

	/** newest change_date during last index update */
	private Date lastLogDate;
	
	private Date lastDate;

	/**
	 * @return the lastTasId
	 */
	public Integer getLastTasId() {
		return lastTasId;
	}

	/**
	 * @param lastTasId the lastTasId to set
	 */
	public void setLastTasId(final Integer lastTasId) {
		this.lastTasId = lastTasId;
	}

	/**
	 * @return the lastLogDate
	 */
	public Date getLastLogDate() {
		return lastLogDate;
	}

	/**
	 * @param lastLogDate the lastLogDate to set
	 */
	public void setLastLogDate(final Date lastLogDate) {
		this.lastLogDate = lastLogDate;
	}

	/**
	 * @param lastDate the lastDate to set
	 */
	public void setLastDate(final Date lastDate) {
		this.lastDate = lastDate;
	}

	/**
	 * @return the lastDate
	 */
	public Date getLastDate() {
		return lastDate;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(final int limit) {
		this.limit = limit;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(final int offset) {
		this.offset = offset;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return the lastContentId
	 */
	public int getLastContentId() {
		return lastContentId;
	}

	/**
	 * @param lastContentId the lastContentId to set
	 */
	public void setLastContentId(final int lastContentId) {
		this.lastContentId = lastContentId;
	}
}