/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain.goldstandard.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.goldstandard.GoldStandardChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.services.searcher.PostSearchQuery;

/**
 * @author dzo
 * @param <RR>
 * @param <R>
 */
public class GetGoldStandardsAfterChangeDate<RR extends Resource, R extends Resource & GoldStandard<RR>> extends GoldStandardChainElement<RR, R> {

    @Override
    protected List<Post<R>> handle(final QueryAdapter<PostQuery<R>> param, final DBSession session) {
        final PostSearchQuery<?> query = new PostSearchQuery<>(param.getQuery());
        final Date changeDate = query.getAfterChangeDate();
        return this.databaseManager.getGoldStandardPostsAfterChangeDate(changeDate, session);
    }

    @Override
    protected boolean canHandle(QueryAdapter<PostQuery<R>> param) {
        PostQuery<R> query = param.getQuery();
        return present(query.getAfterChangeDate());
    }
}
