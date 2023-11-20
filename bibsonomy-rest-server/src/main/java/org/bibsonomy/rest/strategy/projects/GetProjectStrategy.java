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
package org.bibsonomy.rest.strategy.projects;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

import java.io.ByteArrayOutputStream;

/**
 * strategy to get a project by its id
 *
 * @author pda
 */
public class GetProjectStrategy extends Strategy {
    private final String projectId;

    /**
     * constructor
     *
     * @param context
     * @param projectId
     */
    public GetProjectStrategy(final Context context, final String projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    public void perform(final ByteArrayOutputStream outStream)
            throws InternServerException, NoSuchResourceException, ObjectMovedException, ObjectNotFoundException {
        final Project project = this.getLogic().getProjectDetails(projectId);
        if (project == null) {
            throw new NoSuchResourceException("The requested project with id '" + projectId + "' does not exist.");
        }
        this.getRenderer().serializeProject(writer, project, new ViewModel());
    }
}
