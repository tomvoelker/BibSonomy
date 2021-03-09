package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.PublicationsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller that lists all publications of a configurable college
 * - /publications
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
		final String search = command.getSearch();
		query.setSearch(search);
		final List<SortCriteria> sortCriteria = new LinkedList<>();
		if (present(search)) {
			sortCriteria.add(new SortCriteria(SortKey.RANK, SortOrder.ASC));
		} else {
			sortCriteria.add(new SortCriteria(SortKey.YEAR, SortOrder.DESC));
		}
		query.setSortCriteria(sortCriteria);
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
