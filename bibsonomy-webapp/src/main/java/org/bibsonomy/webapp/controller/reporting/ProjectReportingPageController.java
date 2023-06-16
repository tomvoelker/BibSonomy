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
package org.bibsonomy.webapp.controller.reporting;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.querybuilder.ProjectQueryBuilder;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.reporting.ProjectReportingCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for the project reporting page
 * - /reporting/projects
 *
 * @author pda
 * @author dzo
 */
public class ProjectReportingPageController extends AbstractReportingPageController<ProjectReportingCommand> {

    @Override
    protected ProjectReportingCommand instantiateReportingCommand() {
        return new ProjectReportingCommand();
    }

    @Override
    protected void workOn(ProjectReportingCommand command, Person person, Group organization) {
        final ListCommand<Project> projectsPageCommand = command.getProjects();
        final int start = projectsPageCommand.getStart();
        final ProjectQuery projectQuery = new ProjectQueryBuilder()
                .search(command.getSearch())
                .person(person)
                .organization(organization)
                .type(command.getType())
                .sponsor(command.getSponsor())
                .prefix(command.getPrefix())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .start(start)
                .end(start + projectsPageCommand.getEntriesPerPage())
                .build();
        final List<Project> projects = this.logic.getProjects(projectQuery);
        projectsPageCommand.setList(projects);
    }

    @Override
    protected View reportingView() {
        return Views.PROJECTS_REPORTING;
    }
}
