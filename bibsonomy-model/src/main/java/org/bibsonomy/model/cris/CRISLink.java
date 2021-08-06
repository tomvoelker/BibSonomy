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
package org.bibsonomy.model.cris;

import java.util.Date;
import java.util.Objects;

/**
 * this class represents the link of a publication to an Linkable object of the CRIS system
 *
 * @author dzo
 */
public class CRISLink {

	/** the database id; only use in database module */
	private Integer id;

	private Linkable source;

	private Linkable target;

	private Date startDate;

	private Date endDate;

	private CRISLinkType linkType;

	private CRISLinkDataSource dataSource;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the source
	 */
	public Linkable getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Linkable source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public Linkable getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Linkable target) {
		this.target = target;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the linkType
	 */
	public CRISLinkType getLinkType() {
		return linkType;
	}

	/**
	 * @param linkType the linkType to set
	 */
	public void setLinkType(CRISLinkType linkType) {
		this.linkType = linkType;
	}

	/**
	 * @return the dataSource
	 */
	public CRISLinkDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(CRISLinkDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CRISLink crisLink = (CRISLink) o;
		return Objects.equals(source, crisLink.source) &&
						Objects.equals(target, crisLink.target) &&
						Objects.equals(linkType, crisLink.linkType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, target, linkType);
	}
}
