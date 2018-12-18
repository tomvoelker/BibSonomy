package org.bibsonomy.webapp.controller.reporting;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.reporting.PublicationReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

public class PublicationReportingPageController implements MinimalisticController<PublicationReportingCommand> {

	private LogicInterface logic;

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}


	@Override
	public PublicationReportingCommand instantiateCommand() {
		return new PublicationReportingCommand();
	}

	@Override
	public View workOn(PublicationReportingCommand command) {
		final PostQuery<BibTex> query = new PostQueryBuilder().setStartDate(command.getStartDate()).
						setEndDate(command.getEndDate()).createPostQuery(BibTex.class);
		final List<Post<BibTex>> publications = logic.getPosts(query);
		command.setPublications(publications);
		if (ValidationUtils.present(command.getDownloadFormat())) {
			return Views.REPORTING_DOWNLOAD;
		}
		return Views.PUBLICATIONS_REPORTING;
	}
}
