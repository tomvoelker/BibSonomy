package org.bibsonomy.webapp.controller.cris;

import java.util.List;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.PublicationsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller that lists all publications of a configurable college
 *
 * @author dzo
 */
public class PublicationsPageController implements MinimalisticController<PublicationsPageCommand> {

	private String college;
	private LogicInterface logic;

	@Override
	public PublicationsPageCommand instantiateCommand() {
		return new PublicationsPageCommand();
	}

	@Override
	public View workOn(PublicationsPageCommand command) {
		final ListCommand<Post<GoldStandardPublication>> goldStandardPublications = command.getPublications();
		final PostQuery<GoldStandardPublication> query = new PostQuery<>(GoldStandardPublication.class);
		query.setCollege(this.college);
		final int start = goldStandardPublications.getStart();
		query.setStart(start);
		query.setEnd(start + goldStandardPublications.getEntriesPerPage());
		query.setSearch(command.getSearch());
		query.setOrder(Order.YEAR);
		final List<Post<GoldStandardPublication>> posts = this.logic.getPosts(query);
		goldStandardPublications.setList(posts);

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
