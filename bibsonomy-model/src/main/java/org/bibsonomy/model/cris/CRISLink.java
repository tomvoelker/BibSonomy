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

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/**
 * this class represents the link of a publication to an Linkable object of the CRIS system
 *
 * @author dzo
 */
@Getter
@Setter
public class CRISLink {

	/** the database id; only use in database module */
	private Integer id;

	private Linkable source;

	private Linkable target;

	private Date startDate;

	private Date endDate;

	private CRISLinkType linkType;

	private CRISLinkDataSource dataSource;

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
