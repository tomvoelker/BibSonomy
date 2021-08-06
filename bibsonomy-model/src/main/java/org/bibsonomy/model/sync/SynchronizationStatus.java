/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

/**
 * The different states a synchronization can be in. 
 * 
 * @author rja
 */
public enum SynchronizationStatus {
	/*
	 * NOTE: column is a varchar(8), so please use short names
	 */
	/**
	 * A synchronization plan was requested. 
	 */
	PLANNED("planned"),
	/**
	 * A client is currently working on the plan = synchronizing.
	 */
	RUNNING("running"),
	/**
	 * Synchronization is complete. 
	 */
	DONE("done"),
	/**
	 * An error during sync occurred. 
	 */
	ERROR("error"),
	/**
	 * Synchronization was never performed before. 
	 */
	UNDONE("undone");
	
	
	private String status;

	private SynchronizationStatus(final String status) {
		this.status = status;
	}
	
	/**
	 * @return The string representation for the synchronization status.
	 */
	public String getSynchronizationStatus() {
		return status;
	}
}
