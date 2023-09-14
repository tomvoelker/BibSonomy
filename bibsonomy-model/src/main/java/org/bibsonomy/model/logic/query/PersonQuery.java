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
import lombok.Setter;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 *
 * @author dzo
 */
@Getter
@Setter
public class PersonQuery extends BasicQuery {

    /** find the person claimed by the specified user */
    private String userName;

    /** find by person id */
    private String personId;

    /** find by additional person key */
    private AdditionalKey additionalKey;

    /** find by college */
    private String college;

    /** find by organization */
    private Group organization;

    /** find by name prefix */
    private Prefix prefix;

    /** pagination and sorting */
    private int start;
    private int end;
    private PersonSortKey sortKey;
    private SortOrder sortOrder;

    /** the query provided is only a prefix, perform a prefix search */
    private boolean usePrefixMatch;
    private boolean phraseMatch;

    public PersonQuery(String userName, String personId, AdditionalKey additionalKey, String college, Group organization,
                       Prefix prefix, int start, int end, PersonSortKey sortKey, SortOrder sortOrder,
                       boolean usePrefixMatch, boolean phraseMatch) {
        this.userName = userName;
        this.personId = personId;
        this.additionalKey = additionalKey;
        this.college = college;
        this.organization = organization;
        this.prefix = prefix;
        this.start = start;
        this.end = end;
        this.sortKey = sortKey;
        this.sortOrder = sortOrder;
        this.usePrefixMatch = usePrefixMatch;
        this.phraseMatch = phraseMatch;
    }

}
