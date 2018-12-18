package org.bibsonomy.webapp.controller.cris;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonOrder;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.webapp.command.cris.OrganizationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

/**
 * controller that lists a organization with all the details
 * <p>
 * request paths:
 * - /organization/ORGANIZATIONNAME
 *
 * @author dzo
 * @author pda
 */
public class OrganizationPageController implements MinimalisticController<OrganizationPageCommand> {

	private LogicInterface logic;

	@Override
	public OrganizationPageCommand instantiateCommand() {
		return new OrganizationPageCommand();
	}

	@Override
	public View workOn(final OrganizationPageCommand command) {
		final Group group = this.logic.getGroupDetails(command.getRequestedOrganizationName(), false);
		command.setGroup(group);
		// get persons for the organization
		final PersonQuery personOrganizationQuery = new PersonQuery(null);
		personOrganizationQuery.setOrganization(group);
		personOrganizationQuery.setOrder(PersonOrder.MAIN_NAME_LAST_NAME);
		final List<Person> persons = this.logic.getPersons(personOrganizationQuery);
		command.getPersons().setList(persons);

		final ProjectQuery.ProjectQueryBuilder projectQueryBuilder = new ProjectQuery.ProjectQueryBuilder();
		projectQueryBuilder.organization(group);
		final List<Project> projects = this.logic.getProjects(projectQueryBuilder.build());
		// get projects for the organization
		command.getProjects().setList(projects);

		final PostQuery<GoldStandardPublication> postOrganizationQuery = new PostQueryBuilder()
						.setOrder(Order.YEAR)
						.setGrouping(GroupingEntity.ORGANIZATION)
						.setGroupingName(group.getName())
						.createPostQuery(GoldStandardPublication.class);
		final List<Post<GoldStandardPublication>> organizationPosts = this.logic.getPosts(postOrganizationQuery);
		command.getBibtex().setList(organizationPosts);
		return Views.ORGANIZATION_PAGE;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
