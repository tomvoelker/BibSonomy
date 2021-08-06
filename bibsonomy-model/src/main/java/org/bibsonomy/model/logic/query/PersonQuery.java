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

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 *
 * FIXME add real person query builder
 * @author dzo
 */
public class PersonQuery extends PersonSuggestionQueryBuilder implements PaginatedQuery, Query {

	private String college;
	private Prefix prefix;
	private int start = 0;
	private int end = 20;
	private PersonSortKey order;
	/** the organization to filter for */
	private Group organization;
	/** find the person claimed by the specified user */
	private String userName;
	/** additiona person key */
	private AdditionalKey additionalKey;

	/** the query provided is only a prefix, perform a prefix search */
	private boolean usePrefixMatch = false;
	private boolean phraseMatch = false;

	/**
	 * default person query with empty search
	 */
	public PersonQuery() {
		super(null);
	}

	/**
	 * @param query any combination of title, author-name
	 */
	public PersonQuery(String query) {
		super(query);
	}

	/**
	 * @return the college
	 */
	public String getCollege() {
		return college;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}

	/**
	 * @return the prefix
	 */
	public Prefix getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(Prefix prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the start
	 */
	@Override
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	@Override
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return the order
	 */
	public PersonSortKey getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(PersonSortKey order) {
		this.order = order;
	}

	/**
	 * @return the organization
	 */
	public Group getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Group organization) {
		this.organization = organization;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the additionalKey
	 */
	public AdditionalKey getAdditionalKey() {
		return additionalKey;
	}

	/**
	 * @param additionalKey the additionalKey to set
	 */
	public void setAdditionalKey(AdditionalKey additionalKey) {
		this.additionalKey = additionalKey;
	}

	/**
	 * @return the usePrefixMatch
	 */
	public boolean isUsePrefixMatch() {
		return usePrefixMatch;
	}

	/**
	 * @param usePrefixMatch the usePrefixMatch to set
	 */
	public void setUsePrefixMatch(boolean usePrefixMatch) {
		this.usePrefixMatch = usePrefixMatch;
	}

	/**
	 * @return the phraseMatch
	 */
	public boolean isPhraseMatch() {
		return phraseMatch;
	}

	/**
	 * @param phraseMatch the phraseMatch to set
	 */
	public void setPhraseMatch(boolean phraseMatch) {
		this.phraseMatch = phraseMatch;
	}
}
