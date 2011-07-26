/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.sync;

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


	private SynchronizationActions state;

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
	 * @param state the synchronization state to set
	 */
	public void setState(SynchronizationActions state) {
		this.state = state;
	}

	/**
	 * @return the state 
	 */
	public SynchronizationActions getState() {
		return state;
	}

}
