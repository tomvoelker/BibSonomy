/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search;

import java.util.Date;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;


/**
 * search post class, extending the model class with index management fields.
 * 
 * @author fei
 *
 * @param <R>
 */
public class SearchPost<R extends Resource> extends Post<R> {
	private static final long serialVersionUID = 6167951235868739450L;

	/** newest tas_id during last index update */
	private Integer lastTasId;

	/** newest log_date during last index update */
	private Date lastLogDate;

	/**
	 * @return the lastTasId
	 */
	public Integer getLastTasId() {
		return lastTasId;
	}

	/**
	 * @param lastTasId the lastTasId to set
	 */
	public void setLastTasId(Integer lastTasId) {
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
	public void setLastLogDate(Date lastLogDate) {
		this.lastLogDate = lastLogDate;
	}

}
