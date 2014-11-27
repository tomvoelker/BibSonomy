/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.sync;

import java.util.Date;

/**
 * @author wla
 */
public abstract class SynchronizationResource {

	/**
	 * Create date of this resource.
	 */
	private Date createDate;
	private Date changeDate;
	private SynchronizationAction action;

	/**
	 * @param resource
	 * @return true if resources are same
	 */
	public abstract boolean isSame(SynchronizationResource resource);

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
	 * @param action the synchronization state to set
	 */
	public void setAction(SynchronizationAction action) {
		this.action = action;
	}

	/**
	 * @return the state 
	 */
	public SynchronizationAction getAction() {
		return action;
	}

	@Override
	public String toString() {
		return action.toString();
	}
}
