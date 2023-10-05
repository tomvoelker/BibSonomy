/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.persons;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * strategy to get a list of person
 *
 * @author dzo
 */
public class GetListOfPersonsStrategy extends AbstractGetListStrategy<List<Person>> {

	private final String userName;

	private final String personId;

	private final AdditionalKey additionalKey;

	/**
	 * @param context
	 */
	public GetListOfPersonsStrategy(final Context context) {
		super(context);
		this.userName = context.getStringAttribute(RESTConfig.USER_PARAM, null);
		this.personId = context.getStringAttribute(RESTConfig.PERSON_ID_PARAM, null);
		this.additionalKey = RESTUtils.getAdditionalKeyParam(context);
	}

	@Override
	protected void render(final Writer writer, final List<Person> persons) {
		this.getRenderer().serializePersons(writer, persons, this.getView());
	}

	@Override
	protected List<Person> getList() {
		final PersonQueryBuilder queryBuilder = new PersonQueryBuilder()
				.byUserName(this.userName)
				.byPersonId(this.personId)
				.byAdditionalKey(this.additionalKey)
				.sortBy(PersonSortKey.RANK)
				.orderBy(SortOrder.DESC)
				.start(this.getView().getStartValue())
				.end(this.getView().getEndValue());

		return this.getLogic().getPersons(queryBuilder.build());
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersons();
	}

}
