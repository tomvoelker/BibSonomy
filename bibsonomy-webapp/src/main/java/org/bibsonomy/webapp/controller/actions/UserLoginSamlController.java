/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.filter.SamlLoginFilter;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * This controller is neccessary to process the success of a saml login
 * TODO: document why we are not using the {@link AuthenticationSuccessHandler} provided
 * by spring security @see {@link SamlLoginFilter}
 * 
 * @author nilsraabe
 */
public class UserLoginSamlController implements MinimalisticController<BaseCommand> {

	@Override
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}

	@Override
	public View workOn(final BaseCommand command) {
		return new ExtendedRedirectView("/");
	}

}
