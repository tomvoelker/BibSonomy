/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationSortKey;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.UrlBuilder;

import java.util.Date;
import java.util.List;

/**
 * @author dzo
 */
public class GetResourcePersonRelationsQuery extends AbstractQuery<List<ResourcePersonRelation>> {
	private final String personId;
	private final String interhash;
	private final PersonResourceRelationType relationType;
	private final Integer authorIndex;
	private final Date changeDate;

	private final boolean withPersons;
	private final boolean withPosts;
	private final boolean withPersonsOfPosts;
	private final boolean onlyTheses;
	private final boolean groupByInterhash;

	private final PersonResourceRelationSortKey sortKey;
	private final SortOrder sortOrder;

	private final int start;

	private final int end;


	public GetResourcePersonRelationsQuery(ResourcePersonRelationQuery query) {
		if (!present(query)) {
			throw new IllegalArgumentException("No resource-person relation query given.");
		}

		this.personId = query.getPersonId();
		this.interhash = query.getInterhash();
		this.relationType = query.getRelationType();
		this.authorIndex = query.getAuthorIndex();
		this.changeDate = query.getChangeDate();
		this.withPersons = query.isWithPersons();
		this.withPosts = query.isWithPosts();
		this.withPersonsOfPosts = query.isWithPersonsOfPosts();
		this.onlyTheses = query.isOnlyTheses();
		this.groupByInterhash = query.isGroupByInterhash();
		this.sortKey = query.getSortKey();
		this.sortOrder = query.getSortOrder();
		this.start = query.getStart();
		this.end = query.getEnd();
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		UrlBuilder urlBuilder = this.getUrlRenderer().createUrlBuilderForPersonRelations(this.personId);
		urlBuilder.addParameter(RESTConfig.INTERHASH_PARAM, this.interhash);
		urlBuilder.addParameter(RESTConfig.RELATION_TYPE_PARAM, this.relationType);
		urlBuilder.addParameter(RESTConfig.RELATION_INDEX_PARAM, this.authorIndex);
		urlBuilder.addParameter(RESTConfig.CHANGE_DATE_PARAM, RESTConfig.serializeDate(this.changeDate));
		urlBuilder.addParameter(RESTConfig.WITH_PERSONS_PARAM, this.withPersons);
		urlBuilder.addParameter(RESTConfig.WITH_POSTS_PARAM, this.withPosts);
		urlBuilder.addParameter(RESTConfig.WITH_PERSONS_OF_POSTS_PARAM, this.withPersonsOfPosts);
		urlBuilder.addParameter(RESTConfig.ONLY_THESES_PARAM, this.onlyTheses);
		urlBuilder.addParameter(RESTConfig.GROUP_BY_INTERHASH_PARAM, this.groupByInterhash);
		urlBuilder.addParameter(RESTConfig.SORT_KEY_PARAM, this.sortKey);
		urlBuilder.addParameter(RESTConfig.SORT_ORDER_PARAM, this.sortOrder);
		urlBuilder.addParameter(RESTConfig.START_PARAM, this.start);
		urlBuilder.addParameter(RESTConfig.END_PARAM, this.end);

		this.downloadedDocument = this.performGetRequest(urlBuilder.asString());
	}

	@Override
	protected List<ResourcePersonRelation> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseResourcePersonRelations(this.downloadedDocument);
	}
}
