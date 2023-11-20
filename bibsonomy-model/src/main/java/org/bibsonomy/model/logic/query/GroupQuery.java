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

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.GroupSortKey;
import org.bibsonomy.model.logic.querybuilder.GroupQueryBuilder;

/**
 * Specifies a group query.
 * <p>
 * Depending on the settings, the query will be handled differently.
 *
 * @author ada, pda
 */
@Getter
@Setter
public class GroupQuery extends BasicQuery {

    /**
     * if set only get groups with names starting with prefix
     */
    private Prefix prefix;

    private Set<String> realnameSearch;

    /**
     * the group sort key
     */
    private GroupSortKey groupSortKey;

    /**
     * the sort order of the list sorting
     */
    private SortOrder sortOrder;

    /**
     * If set the query is restricted to groups the user is a member of.
     * When querying for pending groups, this is considered the creator of the group.
     */
    private String userName;

    /**
     * if a valid non-empty string is supplied, the query will lookup the group with the supplied external id.
     */
    private String externalId;

    /**
     * if set only organizations or non organizations should be returned
     */
    private boolean organization;

    /**
     * if set to <code>true</code> this query will retrieve pending groups, otherwise only activated groups will be retrieved.
     */
    private boolean pending;


    public static GroupQueryBuilder builder() {
        return new GroupQueryBuilder();
    }

}
