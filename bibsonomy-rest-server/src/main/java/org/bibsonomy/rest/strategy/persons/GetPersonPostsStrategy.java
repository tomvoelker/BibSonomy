/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import org.bibsonomy.model.PersonPost;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.logic.querybuilder.PersonPostQueryBuilder;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.List;

/**
 * Strategy to get the gold standard publication of a person by their ID.
 *
 * @author kchoong
 */
public class GetPersonPostsStrategy extends AbstractGetListStrategy<List<Post>> {

    private final String personId;

    /**
     *
     * @param context
     * @param personId
     */
    public GetPersonPostsStrategy(Context context, String personId) {
        super(context);
        this.personId = personId;
    }

    @Override
    protected void render(final Writer writer, final List<Post> resultList) {
        this.getRenderer().serializePersonPosts(writer, resultList);
    }

    @Override
    protected List<Post> getList() {
        return this.getLogic().getPersonPosts(new PersonPostQueryBuilder().setPersonId(this.personId));
    }

    @Override
    protected UrlBuilder getLinkPrefix() {
        return this.getUrlRenderer().createUrlBuilderForPersonPosts(this.personId);
    }
}
