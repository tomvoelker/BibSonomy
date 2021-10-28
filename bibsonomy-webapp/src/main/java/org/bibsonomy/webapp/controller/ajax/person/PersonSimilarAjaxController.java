/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.ajax.person;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller to handle AJAX requests for publications of a given person with a similar name.
 *
 * @author kchoong
 */
public class PersonSimilarAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());
        final PostQuery<GoldStandardPublication> personNameQuery = new PostQueryBuilder()
                .setPersonNames(person.getNames())
                .setOnlyIncludeAuthorsWithoutPersonId(true)
                .end(20) // get 20 "recommendations"
                .createPostQuery(GoldStandardPublication.class);
        final List<Post<GoldStandardPublication>> similarPosts = this.logic.getPosts(personNameQuery);

        final List<ResourcePersonRelation> similarAuthorRelations = new ArrayList<>();
        for (final Post<GoldStandardPublication> post : similarPosts) {
            final ResourcePersonRelation relation = new ResourcePersonRelation();
            relation.setPost(post);
            relation.setPersonIndex(PersonUtils.findIndexOfPerson(person, post.getResource()));
            relation.setRelationType(PersonUtils.getRelationType(person, post.getResource()));
            similarAuthorRelations.add(relation);
        }

        command.setSimilarAuthorPubs(similarAuthorRelations);

        return Views.AJAX_PERSON_PUBLICATIONS;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }
}
