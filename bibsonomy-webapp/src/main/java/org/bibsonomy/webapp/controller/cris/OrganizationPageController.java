package org.bibsonomy.webapp.controller.cris;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.OrganizationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.Arrays;

/**
 * controller that lists a organization with all the details
 * <p>
 * request paths:
 * - /organization/ORGANIZATIONNAME
 *
 * @author dzo, pda
 */
public class OrganizationPageController implements MinimalisticController<OrganizationPageCommand> {

	private LogicInterface logic;

	@Override
	public OrganizationPageCommand instantiateCommand() {
		return new OrganizationPageCommand();
	}

	@Override
	public View workOn(OrganizationPageCommand command) {
		final Group group = logic.getGroupDetails(command.getRequestedOrganizationName(), false);
		command.setGroup(group);
		// get persons for the organization
		Person a = new Person(), b = new Person();
		command.setPersons(new ListCommand<>(command, Arrays.asList(a, b)));
		a.setMainName(new PersonName("Niemand", "Keiner"));
		b.setMainName(new PersonName("Lolodld", "jsjapoij"));
		a.setAcademicDegree("Dr.");
		b.setAcademicDegree("Prof.Dr.");
		a.setCollege("University of Würzburg");
		b.setCollege("University of Würzburg");

		// get projects for the organization
		command.setProjects(new ListCommand<>(command, Arrays.asList(logic.getProjectDetails("kallimachos"),
						logic.getProjectDetails("regio"))));

		// get publications for the organization
		group.getPosts().addAll(Arrays.asList(logic.getPostDetails("82e097f84bf3891bdd856e11b31ad68e", "mho"),
						logic.getPostDetails("9eb6e5899cb8f1a587ae6267d4d5f2e9", "jhi")));

		ListCommand<Post<? extends Resource>> listCommand = new ListCommand<>(command, group.getPosts());
		command.setBibtex(listCommand);
		group.setDescription("jfjalkjf pioaspodi aspöodipoiad apöoiwaedpu aälsidujüaps9dui asdpüo9aisdüp9aisu asdp9aoisduü0a9sud asdüp9asuidüa09sud\n" +
						"öoiujasöodifj asöodifjasöodifj asdöfoijasödofij asdöoifjasöodifj");
		return Views.ORGANIZATION_PAGE;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
