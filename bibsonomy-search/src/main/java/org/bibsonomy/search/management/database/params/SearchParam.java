/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.management.database.params;

import java.util.Date;

/**
 * Class for lucene queries
 * 
 * @author Jens Illig
 */
public class SearchParam {
	
	private String userName;
	
	/** The SQL-Limit */
	private int limit;
	
	/** The SQL-Offset */
	private int offset;
	
	/** newest tas_id during last index update */
	private Integer lastTasId;
	
	private int lastContentId;
	
	private int lastOffset; // TODO or just use offset?

	/** newest change_date during last index update */
	private Date lastLogDate;
	
	private Date lastDate;
	
	private String userRelation;

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
	
	/**
	 * @return the lastOffset
	 */
	public int getLastOffset() {
		return lastOffset;
	}
	
	/**
	 * @param lastOffset the lastOffset to set
	 */
	public void setLastOffset(final int lastOffset) {
		this.lastOffset = lastOffset;
	}

	/**
	 * @return the userRelation
	 */
	public String getUserRelation() {
		return userRelation;
	}

	/**
	 * @param userRelation the userRelation to set
	 */
	public void setUserRelation(String userRelation) {
		this.userRelation = userRelation;
	}
}