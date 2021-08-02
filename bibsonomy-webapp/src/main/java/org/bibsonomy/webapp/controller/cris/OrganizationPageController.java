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
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
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
		final Group group = this.logic.getGroupDetails(requestedOrganizationName, false);

		if (!present(group)) {
			throw new ObjectNotFoundException(requestedOrganizationName);
		}

		/*
		 * TODO: check if group is organization
		 */

		command.setGroup(group);

		/*
		 * person query
		 */
		final ListCommand<Person> personsListCommand = command.getPersons();
		final PersonQuery personOrganizationQuery = new PersonQuery();
		personOrganizationQuery.setOrganization(group);
		personOrganizationQuery.setOrder(PersonSortKey.MAIN_NAME_LAST_NAME);
		final int personStart = personsListCommand.getStart();
		personOrganizationQuery.setStart(personStart);
		personOrganizationQuery.setEnd(personStart + personsListCommand.getEntriesPerPage());

		/*
		 * publication query
		 */
		final ListCommand<Post<GoldStandardPublication>> publicationsListCommand = command.getPublications();
		final int start = publicationsListCommand.getStart();
		final PostQuery<GoldStandardPublication> postOrganizationQuery = new PostQueryBuilder()
						.setSortCriteria(Collections.singletonList(new SortCriteria(SortKey.YEAR, SortOrder.DESC)))
						.setGrouping(GroupingEntity.ORGANIZATION)
						.setGroupingName(group.getName())
						.entriesStartingAt(publicationsListCommand.getEntriesPerPage(), publicationsListCommand.getStart())
						.createPostQuery(GoldStandardPublication.class);

		/*
		 * project query
		 */
		final ListCommand<Project> projectsListCommand = command.getProjects();
		final ProjectQuery.ProjectQueryBuilder projectQueryBuilder = new ProjectQuery.ProjectQueryBuilder();
		final int projectStart = projectsListCommand.getStart();
		projectQueryBuilder.organization(group).start(projectStart).end(start + projectsListCommand.getEntriesPerPage());
		final ProjectQuery projectsQuery = projectQueryBuilder.build();

		final OrganizationPageSubPage subPage = getSubPage(command);
		switch (subPage) {
			case INFO:
				// nothing to do
				break;
			case PERSONS:
				/*
				 * get persons for the organization
				 */
				final List<Person> persons = this.logic.getPersons(personOrganizationQuery);
				personsListCommand.setList(persons);
				break;
			case PROJECTS:
				/*
				 * get projects for the organization
				 */
				final List<Project> projects = this.logic.getProjects(projectsQuery);
				projectsListCommand.setList(projects);
				break;
			case PUBLICATIONS:
				/*
				 * get publications for organization
				 */
				final List<Post<GoldStandardPublication>> organizationPosts = this.logic.getPosts(postOrganizationQuery);
				publicationsListCommand.setList(organizationPosts);
				break;
		}

		/*
		 * set total counts if not set already (for menu)
		 */
		this.setTotalCount(publicationsListCommand, () -> this.logic.getStatistics(postOrganizationQuery));
		this.setTotalCount(personsListCommand, () -> this.logic.getStatistics(personOrganizationQuery));
		this.setTotalCount(projectsListCommand, () -> this.logic.getStatistics(projectsQuery));

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
