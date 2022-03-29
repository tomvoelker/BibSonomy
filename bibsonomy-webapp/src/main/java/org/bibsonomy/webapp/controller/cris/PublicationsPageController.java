/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.SortUtils;
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
@Getter
@Setter
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

		// Set sort criteria
		List<SortCriteria> sortCriteria = SortUtils.singletonSortCriteria(SortKey.YEAR, SortOrder.DESC);
		List<SortKey> sortKeys = SortUtils.parseSortKeys(command.getSortPage());
		List<SortOrder> sortOrders = SortUtils.parseSortOrders(command.getSortPageOrder());
		if (present(sortKeys) && present(sortOrders)) {
			sortCriteria = SortUtils.generateSortCriteria(sortKeys, sortOrders);
		}

		final PostQueryBuilder queryBuilder = new PostQueryBuilder()
				.college(this.college)
				.entriesStartingAt(goldStandardPublications.getEntriesPerPage(), goldStandardPublications.getStart())
				.setSortCriteria(sortCriteria)
				.search(command.getSearch());

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
