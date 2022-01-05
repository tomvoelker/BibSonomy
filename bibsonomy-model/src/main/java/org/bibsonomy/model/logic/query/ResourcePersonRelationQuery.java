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
package org.bibsonomy.model.logic.query;

import lombok.Getter;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * A class for specifying queries that yield resource - person relations.
 *
 * @author ada
 */
@Getter
public class ResourcePersonRelationQuery extends BasicPaginatedQuery {

	private boolean withPersons;
	private boolean withPosts;
	private boolean withPersonsOfPosts;

	private String interhash;
	private Integer authorIndex;
	private String personId;

	private PersonResourceRelationType relationType;
	private PersonResourceRelationOrder order;

	private boolean onlyTheses;
	private boolean groupByInterhash;

	public ResourcePersonRelationQuery(int start, int end, boolean withPersons, boolean withPosts, boolean withPersonsOfPosts,
									 PersonResourceRelationType relationType,
									 String interhash,
									 Integer authorIndex,
									 String personId,
									 PersonResourceRelationOrder order,
									 boolean onlyTheses,
									 boolean groupByInterhash) {

		super(start, end);
		this.withPersons = withPersons;
		this.withPosts = withPosts;
		this.withPersonsOfPosts = withPersonsOfPosts;
		this.relationType = relationType;
		this.interhash = interhash;
		this.authorIndex = authorIndex;
		this.personId = personId;
		this.order = order;
		this.onlyTheses = onlyTheses;
		this.groupByInterhash = groupByInterhash;
	}

}
