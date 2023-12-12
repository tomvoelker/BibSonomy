/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 * University of Würzburg, Germany
 * https://www.informatik.uni-wuerzburg.de/datascience/home/
 * Information Processing and Analytics Group,
 * Humboldt-Universität zu Berlin, Germany
 * https://www.ibi.hu-berlin.de/en/research/Information-processing/
 * Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * https://www.kde.cs.uni-kassel.de/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * https://www.l3s.de/
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

import java.util.Date;

import lombok.Getter;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.PersonResourceRelationSortKey;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * A class for specifying queries that yield resource - person relations.
 *
 * @author ada
 */
@Getter
public class ResourcePersonRelationQuery extends BasicPaginatedQuery {

    private final String personId;
    private final String interhash;
    private final PersonResourceRelationType relationType;
    private final Integer authorIndex;
    private final Date beforeChangeDate;
    private final Date afterChangeDate;

    private final boolean withPersons;
    private final boolean withPosts;
    private final boolean withPersonsOfPosts;
    private final boolean onlyTheses;
    private final boolean groupByInterhash;

    private final PersonResourceRelationSortKey sortKey;
    private final SortOrder sortOrder;

    public ResourcePersonRelationQuery(String personId,
                                       String interhash,
                                       PersonResourceRelationType relationType,
                                       Integer authorIndex,
                                       Date beforeChangeDate,
                                       Date afterChangeDate,
                                       boolean withPersons,
                                       boolean withPosts,
                                       boolean withPersonsOfPosts,
                                       boolean onlyTheses,
                                       boolean groupByInterhash,
                                       PersonResourceRelationSortKey sortKey,
                                       SortOrder sortOrder,
                                       int start,
                                       int end) {
        super(start, end);

        this.personId = personId;
        this.interhash = interhash;
        this.relationType = relationType;
        this.authorIndex = authorIndex;
        this.beforeChangeDate = beforeChangeDate;
        this.afterChangeDate = afterChangeDate;
        this.withPersons = withPersons;
        this.withPosts = withPosts;
        this.withPersonsOfPosts = withPersonsOfPosts;
        this.onlyTheses = onlyTheses;
        this.groupByInterhash = groupByInterhash;
        this.sortKey = sortKey;
        this.sortOrder = sortOrder;
    }
}
