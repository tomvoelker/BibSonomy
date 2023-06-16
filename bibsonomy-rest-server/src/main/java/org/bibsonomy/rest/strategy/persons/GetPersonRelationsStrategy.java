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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
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
    private Date changeDate;

    public GetPersonRelationsStrategy(final Context context) {
        super(context);

        this.personId = context.getStringAttribute(RESTConfig.PERSON_ID_PARAM, null);

        String changeDateString = context.getStringAttribute(RESTConfig.CHANGE_DATE_PARAM, null);
        if (present(changeDateString)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                this.changeDate = formatter.parse(changeDateString);
            } catch (ParseException e) {
                this.changeDate = null;
            }
        }
    }

    @Override
    protected List<ResourcePersonRelation> getList() {
        final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
                .byPersonId(this.personId)
                .byChangeDate(this.changeDate)
                .withPosts(true)
                .withPersonsOfPosts(true)
                .groupByInterhash(true)
                .orderBy(PersonResourceRelationOrder.PublicationYear);

        return this.getLogic().getResourceRelations(queryBuilder.build());
    }

    @Override
    protected void render(final Writer writer, final List<ResourcePersonRelation> resultList) {
        this.getRenderer().serializeResourcePersonRelations(writer, resultList);
    }

    @Override
    protected UrlBuilder getLinkPrefix() {
        return this.getUrlRenderer().createUrlBuilderForResourcePersonRelations(this.personId);
    }
}
