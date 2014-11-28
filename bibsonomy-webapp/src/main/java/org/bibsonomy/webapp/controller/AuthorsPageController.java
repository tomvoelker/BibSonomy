/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.AuthorsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * page controller for
 *  - /authors
 * 
 * @author Christian Claus
 */
public class AuthorsPageController implements MinimalisticController<AuthorsCommand> {

	private LogicInterface logic;
	
	@Override
	public View workOn(final AuthorsCommand command) {
		// TODO: can't we use the PersonName instead of the author model?
		command.setAuthorList(this.logic.getAuthors(GroupingEntity.ALL, null, null, null, null, null, 0, Integer.MAX_VALUE, null));
		return Views.AUTHORSPAGE;
	}

	@Override
	public AuthorsCommand instantiateCommand() {
		return new AuthorsCommand();
	}
	
	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
