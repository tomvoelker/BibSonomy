/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.RelationsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * TODO: rename to ConceptsPageController
 * 
 * Controller to display the most popular concepts
 * - /concepts
 * 
 * @author Christian Kramer
 */
public class RelationsController implements MinimalisticController<RelationsCommand> {

	/*
	 * the following concepts are unwanted on the relations page
	 * XXX: inject them using Spring?
	 */ 
	private static final String[] tagsToRemove = new String[]{
		"bookmarks_toolbar",
		"bookmarks_toolbar_folder",
		"forex",
		"from_internet_explorer",
		"how",
		"lesezeichen-symbolleiste",
		"personal_toolbar_folder",
		"the",
		"what"
	};

	private LogicInterface logic;
	
	@Override
	public RelationsCommand instantiateCommand() {	
		return new RelationsCommand();
	}

	@Override
	public View workOn(final RelationsCommand command) {
		/*
		 * request the concepts
		 */
		final List<Tag> tags = this.logic.getConcepts(Resource.class, GroupingEntity.ALL, null, null, null, ConceptStatus.ALL, 0, 50);

		for (final String string : tagsToRemove) {
			tags.remove(new Tag(string));
		}

		command.setTagRelations(tags);
		return Views.CONCEPTS;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
