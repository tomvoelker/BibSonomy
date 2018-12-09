package org.bibsonomy.webapp.controller.cris;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller that lists all publications of a configurable college
 *
 * @author dzo
 */
public class PublicationsPageController implements MinimalisticController<SimpleResourceViewCommand> {

	private String college;
	private LogicInterface logic;

	@Override
	public SimpleResourceViewCommand instantiateCommand() {
		return new SimpleResourceViewCommand();
	}

	@Override
	public View workOn(SimpleResourceViewCommand command) {
		final ListCommand<Post<GoldStandardPublication>> goldStandardPublications = command.getGoldStandardPublications();
		final PostQuery<GoldStandardPublication> query = new PostQuery<>(GoldStandardPublication.class);
		query.setCollege(this.college);
		final int start = goldStandardPublications.getStart();
		query.setStart(start);
		query.setEnd(start + goldStandardPublications.getEntriesPerPage());
		goldStandardPublications.setList(this.logic.getPosts(query));

		return Views.PUBLICATIONS_OVERVIEW;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
