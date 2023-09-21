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
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationSortKey;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * strategy to get a list of person-resource relations
 *
 * @author dzo, pda
 */
public class GetPersonRelationsStrategy extends AbstractGetListStrategy<List<ResourcePersonRelation>> {

    private String personId;
    private final AdditionalKey additionalKey;
    private final Date changeDate;
    private final boolean withPersons;
    private final boolean withPosts;
    private final boolean withPersonsOfPosts;
    private final boolean onlyTheses;
    private final boolean groupByInterhash;

    public GetPersonRelationsStrategy(final Context context) {
        super(context);

        this.personId = context.getStringAttribute(RESTConfig.PERSON_ID_PARAM, null);
        this.additionalKey = RESTUtils.getAdditionalKeyParam(context);
        this.changeDate = RESTUtils.getDateParam(context, RESTConfig.CHANGE_DATE_PARAM);
        this.withPersons = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.WITH_PERSONS_PARAM, "false"));
        this.withPosts = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.WITH_POSTS_PARAM, "false"));
        this.withPersonsOfPosts = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.WITH_PERSONS_OF_POSTS_PARAM, "false"));
        this.onlyTheses = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.ONLY_THESES_PARAM, "false"));
        this.groupByInterhash = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.GROUP_BY_INTERHASH_PARAM, "false"));
    }

    @Override
    protected List<ResourcePersonRelation> getList() {
        final Person person = this.getPerson();

        if (present(person)) {
            // Set person id, if additional keys were used
            this.personId = person.getPersonId();
        }

        final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
                .byPersonId(this.personId)
                .byChangeDate(this.changeDate)
                .withPersons(this.withPersons)
                .withPosts(this.withPosts)
                .withPersonsOfPosts(this.withPersonsOfPosts)
                .onlyTheses(this.onlyTheses)
                .groupByInterhash(this.groupByInterhash)
                .sortBy(PersonResourceRelationSortKey.PublicationYear)
                .orderBy(SortOrder.DESC);

        return this.getLogic().getResourceRelations(queryBuilder.build());
    }

    private Person getPerson() {
        if (present(this.personId)) {
            return this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.personId);
        }

        if (present(this.additionalKey)) {
            PersonQueryBuilder queryBuilder = new PersonQueryBuilder().byAdditionalKey(this.additionalKey);
            List<Person> persons = this.getLogic().getPersons(queryBuilder.build());
            if (present(persons)) {
                return persons.get(0);
            }
        }

        return null;
    }

    @Override
    protected void render(final Writer writer, final List<ResourcePersonRelation> resultList) {
        this.getRenderer().serializeResourcePersonRelations(writer, resultList);
    }

    @Override
    protected UrlBuilder getLinkPrefix() {
        return this.getUrlRenderer().createUrlBuilderForPersonRelations(this.personId);
    }
}
