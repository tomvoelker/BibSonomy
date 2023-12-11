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
package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.querybuilder.ProjectQueryBuilder;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.ProjectsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

/**
 * controller for displaying a list of projects
 * paths:
 *    - /projects
 *
 * @author dzo
 */
public class ProjectsPageController implements MinimalisticController<ProjectsPageCommand> {

    private LogicInterface logic;

    @Override
    public ProjectsPageCommand instantiateCommand() {
        return new ProjectsPageCommand();
    }

    @Override
    public View workOn(final ProjectsPageCommand command) {
        final ListCommand<Project> projectListCommand = command.getProjects();

        // build the query based on the commands
        final ProjectQueryBuilder builder = new ProjectQueryBuilder()
                .projectStatus(command.getProjectStatus())
                .entriesStartingAt(projectListCommand.getEntriesPerPage(), projectListCommand.getStart())
                .search(command.getSearch())
                .prefixMatch(true)
                .prefix(command.getPrefix())
                .sortKey(command.getProjectSortKey())
                .sortOrder(command.getSortOrder());

        // query the logic for matching projects
        final ProjectQuery projectQuery = builder.build();
        final List<Project> projects = this.logic.getProjects(projectQuery);
        projectListCommand.setList(projects);

        if (!present(projectListCommand.getTotalCountAsInteger())) {
            final Statistics stats = this.logic.getStatistics(projectQuery);
            projectListCommand.setTotalCount(stats.getCount());
        }

        return Views.PROJECT_PAGE;
    }

    /**
     * @param logic the logic to set
     */
    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }
}
