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

package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.query.PersonQuery;

/**
 * person query builder
 *
 * @author kchoong
 */
public class PersonQueryBuilder extends BasicQueryBuilder<PersonQueryBuilder> {

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

	/** sorting */
	private PersonSortKey sortKey;
	private SortOrder sortOrder;

	public PersonQueryBuilder byUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public PersonQueryBuilder byPersonId(String personId) {
		this.personId = personId;
		return this;
	}

	public PersonQueryBuilder byAdditionalKey(AdditionalKey additionalKey) {
		this.additionalKey = additionalKey;
		return this;
	}

	public PersonQueryBuilder byCollege(String college) {
		this.college = college;
		return this;
	}

	public PersonQueryBuilder byOrganization(Group organization) {
		this.organization = organization;
		return this;
	}

	public PersonQueryBuilder byPrefix(Prefix prefix) {
		this.prefix = prefix;
		return this;
	}

	public PersonQueryBuilder sortBy(PersonSortKey sortKey) {
		this.sortKey = sortKey;
		return this;
	}

	public PersonQueryBuilder orderBy(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

    /**
	 * builds the query
	 * @return the query
	 */
	public PersonQuery build() {
		return new PersonQuery(this.userName, this.personId, this.additionalKey, this.college, this.organization,
				this.prefix, this.search, this.start, this.end, this.sortKey, this.sortOrder, this.usePrefixMatch, this.phraseMatch);
	}
    
    @Override
    protected PersonQueryBuilder builder() {
        return this;
    }
}
