package org.bibsonomy.webapp.controller.reporting;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.reporting.PublicationReportingCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for the publication reporting page
 * - /reporting/publications
 *
 * @author pda
 * @author dzo
 */
public class PublicationReportingPageController extends AbstractReportingPageController<PublicationReportingCommand> {

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

	private String college;

	@Override
	protected PublicationReportingCommand instantiateReportingCommand() {
		return new PublicationReportingCommand();
	}

	@Override
	protected void workOn(PublicationReportingCommand command, final Person person, final Group organization) {
		final GroupingEntity groupingEntity = getGroupingEntity(person, organization);
		final String groupingName = getGroupingName(groupingEntity, person, organization);

		final ListCommand<Post<GoldStandardPublication>> publicationListCommand = command.getPublications();
		final int start = publicationListCommand.getStart();
		final PostQuery<GoldStandardPublication> query = new PostQueryBuilder().setStartDate(command.getStartDate()).
						setEndDate(command.getEndDate()).start(start).
						setGrouping(groupingEntity)
						.setGroupingName(groupingName)
						.search(command.getSearch())
						.college(this.college).
						end(start + publicationListCommand.getEntriesPerPage()).
						createPostQuery(GoldStandardPublication.class);
		publicationListCommand.setList(this.logic.getPosts(query));
	}

	@Override
	protected View reportingView() {
		return Views.PUBLICATIONS_REPORTING;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}
}
