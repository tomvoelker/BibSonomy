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

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ProjectQueryBuilder;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.OrganizationPageCommand;
import org.bibsonomy.webapp.command.cris.OrganizationPageSubPage;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * controller that lists a organization with all the details
 *
 * request paths:
 * - /organization/ORGANIZATIONNAME
 * - /organization/ORGANIZATIONNAME/projects
 * - /organization/ORGANIZATIONNAME/persons
 * - /organization/ORGANIZATIONNAME/publications
 *
 * @author dzo
 * @author pda
 */
public class OrganizationPageController implements MinimalisticController<OrganizationPageCommand> {

	private static final int DEFAULT_ENTRIES_PER_PAGE = 30;

	private LogicInterface logic;

	@Override
	public OrganizationPageCommand instantiateCommand() {
		final OrganizationPageCommand organizationPageCommand = new OrganizationPageCommand();
		organizationPageCommand.getPublications().setEntriesPerPage(DEFAULT_ENTRIES_PER_PAGE);
		organizationPageCommand.getPersons().setEntriesPerPage(DEFAULT_ENTRIES_PER_PAGE);
		organizationPageCommand.getProjects().setEntriesPerPage(DEFAULT_ENTRIES_PER_PAGE);
		return organizationPageCommand;
	}

	@Override
	public View workOn(final OrganizationPageCommand command) {
		final String requestedOrganizationName = command.getRequestedOrganizationName();
		final Group organization = this.logic.getGroupDetails(requestedOrganizationName, false);

		if (!present(organization)) {
			throw new ObjectNotFoundException(requestedOrganizationName);
		}
		command.setGroup(organization); // TODO check, if group is organization (dzo)

		// query for persons of organization
		final ListCommand<Person> personsListCommand = command.getPersons();
		final int personStart = personsListCommand.getStart();

		final PersonQuery personQuery =  new PersonQueryBuilder()
				.byOrganization(organization)
				.sortBy(PersonSortKey.MAIN_NAME_LAST_NAME)
				.orderBy(SortOrder.ASC)
				.start(personStart)
				.end(personStart + personsListCommand.getEntriesPerPage())
				.build();

		// query for publications of organization
		final ListCommand<Post<GoldStandardPublication>> publicationsListCommand = command.getPublications();
		final int postStart = publicationsListCommand.getStart();
		final PostQuery<GoldStandardPublication> postQuery = new PostQueryBuilder()
						.setSortCriteria(Collections.singletonList(new SortCriteria(SortKey.YEAR, SortOrder.DESC)))
						.setGrouping(GroupingEntity.ORGANIZATION)
						.setGroupingName(organization.getName())
						.entriesStartingAt(publicationsListCommand.getEntriesPerPage(), publicationsListCommand.getStart())
						.createPostQuery(GoldStandardPublication.class);

		// query for projects of organization
		final ListCommand<Project> projectsListCommand = command.getProjects();
		final int projectStart = projectsListCommand.getStart();

		final ProjectQuery projectQuery = new ProjectQueryBuilder()
				.organization(organization)
				.start(projectStart)
				.end(projectStart + projectsListCommand.getEntriesPerPage())
				.build();

		final OrganizationPageSubPage subPage = getSubPage(command);
		switch (subPage) {
			case INFO:
				// nothing to do
				break;
			case PERSONS:
				// get persons for the organization
				final List<Person> persons = this.logic.getPersons(personQuery);
				personsListCommand.setList(persons);
				break;
			case PROJECTS:
				// get projects for the organization
				final List<Project> projects = this.logic.getProjects(projectQuery);
				projectsListCommand.setList(projects);
				break;
			case PUBLICATIONS:
				// get publications for organization
				final List<Post<GoldStandardPublication>> organizationPosts = this.logic.getPosts(postQuery);
				publicationsListCommand.setList(organizationPosts);
				break;
		}

		// set total counts if not set already (for menu)
		this.setTotalCount(publicationsListCommand, () -> this.logic.getStatistics(postQuery));
		this.setTotalCount(personsListCommand, () -> this.logic.getStatistics(personQuery));
		this.setTotalCount(projectsListCommand, () -> this.logic.getStatistics(projectQuery));

		return Views.ORGANIZATION_PAGE;
	}

	private void setTotalCount(final ListCommand<?> listCommand, final Supplier<Statistics> statisticsSupplier) {
		if (!present(listCommand.getTotalCountAsInteger())) {
			final Statistics statistics = statisticsSupplier.get();
			listCommand.setTotalCount(statistics.getCount());
		}
	}

	private static OrganizationPageSubPage getSubPage(final OrganizationPageCommand command) {
		final OrganizationPageSubPage subPage = command.getSubPage();
		if (present(subPage)) {
			return subPage;
		}

		return OrganizationPageSubPage.INFO;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
