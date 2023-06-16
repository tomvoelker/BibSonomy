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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
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
		this.userName = context.getStringAttribute(GroupingEntity.USER.toString().toLowerCase(), null);
		this.personId = context.getStringAttribute(RESTConfig.PERSON_ID_PARAM, null);
		this.additionalKey = extractAdditionalKey(context);
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
				.start(this.getView().getStartValue())
				.end(this.getView().getEndValue());

		return this.getLogic().getPersons(queryBuilder.build());
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersons();
	}

	/**
	 * extracts the additional key from the context
	 * @param context
	 * @return
	 */
	public static AdditionalKey extractAdditionalKey(final Context context) {
		final String additionalKeyStr = context.getStringAttribute(RESTConfig.PERSON_ADDITIONAL_KEY_PARAM, null);
		if (present(additionalKeyStr)) {
			return parseAdditionalKey(additionalKeyStr);
		}
		return null;
	}

	private static AdditionalKey parseAdditionalKey(final String additionalKeyStr) {
		final String[] split = additionalKeyStr.split(RESTConfig.PERSON_ADDITIONAL_KEY_PARAM_SEPARATOR);
		if (split.length != 2) {
			return null;
		}

		return new AdditionalKey(split[0], split[1]);
	}
}
