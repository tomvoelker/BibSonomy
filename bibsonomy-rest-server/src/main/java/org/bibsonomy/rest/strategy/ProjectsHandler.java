/**
 * BibSonomy-Rest-Server - The REST-server.
 * <p>
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.projects.GetProjectStrategy;
import org.bibsonomy.rest.strategy.projects.PostProjectStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;

/**
 * handler for {@link org.bibsonomy.model.cris.Project} related strategies
 *
 * @author pda
 */
public class ProjectsHandler implements ContextHandler {
    @Override
    public Strategy createStrategy(Context context, URLDecodingPathTokenizer urlTokens, HttpMethod httpMethod) {
        final int numTokensLeft = urlTokens.countRemainingTokens();

        switch (numTokensLeft) {
            // /projects
            case 0:
                return createProjectStrategy(context, httpMethod);
            // /projects/[projectID]
            case 1:
                return createProjectStrategy(context, httpMethod, urlTokens.next());
            default:
                throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax");
        }
    }

    private Strategy createProjectStrategy(Context context, HttpMethod httpMethod, String projectId) {
        switch (httpMethod) {
            case GET:
                return new GetProjectStrategy(context, projectId);
            default:
                throw new UnsupportedHttpMethodException(httpMethod, "Project");
        }
    }

    private Strategy createProjectStrategy(Context context, HttpMethod httpMethod) {
        switch (httpMethod) {
            case POST:
                return new PostProjectStrategy(context);
            default:
                throw new UnsupportedHttpMethodException(httpMethod, "Project");
        }
    }
}
