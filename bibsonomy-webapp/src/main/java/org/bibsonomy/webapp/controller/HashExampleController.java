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

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.HashExampleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for
 * 		- /hashexample
 * 
 * TODO: adapt or delete http://www.bibsonomy.org/help/doc/inside.html 
 * 
 * @author janus
 */
public class HashExampleController implements MinimalisticController<HashExampleCommand> {

	@Override
	public HashExampleCommand instantiateCommand() {
		final HashExampleCommand command = new HashExampleCommand();
		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(new BibTex());
		command.setPost(post);
		return command;
	}

	@Override
	public View workOn(final HashExampleCommand command) {
		command.getPost().getResource().recalculateHashes();
		return Views.HASHEXAMPLE;
	}
}
