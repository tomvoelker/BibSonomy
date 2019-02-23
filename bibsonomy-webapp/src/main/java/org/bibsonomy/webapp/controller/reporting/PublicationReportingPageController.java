package org.bibsonomy.webapp.controller.reporting;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.function.Function;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.reporting.PublicationReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for the publication reporting page
 * - /reporting/publications
 *
 * @author pda
 * @author dzo
 */
public class PublicationReportingPageController implements MinimalisticController<PublicationReportingCommand> {

	private LogicInterface logic;
	private String college;

	@Override
	public PublicationReportingCommand instantiateCommand() {
		final PublicationReportingCommand publicationReportingCommand = new PublicationReportingCommand();
		publicationReportingCommand.setOrganization(new Group());
		publicationReportingCommand.setPerson(new Person());
		return publicationReportingCommand;
	}

	@Override
	public View workOn(PublicationReportingCommand command) {
		final Person person = getField(command, PublicationReportingCommand::getPerson, Person::getPersonId);
		final Group organization = getField(command, PublicationReportingCommand::getOrganization, Group::getName);

		final GroupingEntity groupingEntity = getGroupingEntity(person, organization);
		final String groupingName = getGroupingName(groupingEntity, person, organization);

		// TODO include filter by person and organization
		final ListCommand<Post<GoldStandardPublication>> publicationListCommand = command.getPublications();
		final int start = publicationListCommand.getStart();
		final PostQuery<GoldStandardPublication> query = new PostQueryBuilder().setStartDate(command.getStartDate()).
						setEndDate(command.getEndDate()).setStart(start).
						setGrouping(groupingEntity)
						.setGroupingName(groupingName)
						.college(this.college).
						setEnd(start + publicationListCommand.getEntriesPerPage()).
						createPostQuery(GoldStandardPublication.class);
		publicationListCommand.setList(this.logic.getPosts(query));

		if (present(command.getDownloadFormat())) {
			return Views.REPORTING_DOWNLOAD;
		}

		return Views.PUBLICATIONS_REPORTING;
	}

	private static String getGroupingName(GroupingEntity groupingEntity, Person person, Group organization) {
		switch (groupingEntity) {
			case ORGANIZATION: return organization.getName();
			case PERSON: return person.getPersonId();
		}

		return null;
	}

	private static GroupingEntity getGroupingEntity(final Person person, final Group organization) {
		if (present(organization)) {
			return GroupingEntity.ORGANIZATION;
		}

		if (present(person)) {
			return GroupingEntity.PERSON;
		}

		return GroupingEntity.ALL;
	}

	public static <T, C> T getField(C command, Function<C, T> fieldAccessor, Function<T, ?> checker) {
		final T field = fieldAccessor.apply(command);
		if (present(checker.apply(field))) {
			return field;
		}

		return null;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}
}
