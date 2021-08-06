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
package org.bibsonomy.database.managers.chain.util;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.Query;

/**
 * adapter to pass a query with information through a chain
 * @param <T>
 *
 * @author dzo
 */
public class QueryAdapter<T extends Query> {

    private final T query;

    private final User loggedinUser;

    /**
     * default constructor
     * @param query the query
     * @param loggedinUser the logged in user
     */
    public QueryAdapter(T query, User loggedinUser) {
        this.query = query;
        this.loggedinUser = loggedinUser;
    }

    /**
     * @return the query
     */
    public T getQuery() {
        return query;
    }

    /**
     * @return the loggedinUser
     */
    public User getLoggedinUser() {
        return loggedinUser;
    }
}
