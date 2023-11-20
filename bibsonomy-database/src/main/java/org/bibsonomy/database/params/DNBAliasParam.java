/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.params;

/**
 * TODO: add documentation to this class
 *
 * @author jhi
 */
public class DNBAliasParam {
	private String dnbId;
	private String otherDnbId;
	
	/**
	 * @param dnbId
	 * @param otherDnbId
	 */
	public DNBAliasParam(String dnbId, String otherDnbId) {
		this.dnbId = dnbId;
		this.otherDnbId = otherDnbId;
	}
	
	/**
	 * @return the otherDnbId
	 */
	public String getOtherDnbId() {
		return this.otherDnbId;
	}
	/**
	 * @param otherDnbId the otherDnbId to set
	 */
	public void setOtherDnbId(String otherDnbId) {
		this.otherDnbId = otherDnbId;
	}
	/**
	 * @return the dnbId
	 */
	public String getDnbId() {
		return this.dnbId;
	}
	/**
	 * @param dnbId the dnbId to set
	 */
	public void setDnbId(String dnbId) {
		this.dnbId = dnbId;
	}
	
}
