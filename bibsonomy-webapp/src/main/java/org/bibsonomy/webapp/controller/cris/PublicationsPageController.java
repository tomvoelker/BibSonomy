package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Calendar;
import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
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

	private final int PUB_ENTRIES = 10;

	private String college;
	private LogicInterface logic;

	@Override
	public PublicationsPageCommand instantiateCommand() {
		return new PublicationsPageCommand();
	}

	@Override
	public View workOn(final PublicationsPageCommand command) {
		ListCommand<Post<GoldStandardPublication>> goldStandardPublications = command.getPublications();
		goldStandardPublications.setEntriesPerPage(PUB_ENTRIES);
		final PostQueryBuilder queryBuilder = new PostQueryBuilder()
				.college(this.college)
				.entriesStartingAt(goldStandardPublications.getEntriesPerPage(), goldStandardPublications.getStart())
				.searchAndSortCriteria(command.getSearch(), new SortCriteria(SortKey.YEAR, SortOrder.DESC));

		if (!present(command.getSearch())) {
			/*
			 * If there is no search given, for example when the page is viewed for the first time.
			 * Show latest publications to current year without textual years like: to appear, submitted
			 */
			final Calendar calendar = Calendar.getInstance();
			queryBuilder.search(String.format("year:[* TO %s]", calendar.get(Calendar.YEAR)));
		}

		final List<Post<GoldStandardPublication>> posts = this.logic.getPosts(queryBuilder.createPostQuery(GoldStandardPublication.class));
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
