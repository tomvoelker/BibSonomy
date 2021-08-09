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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.picture.PictureHandler;
import org.bibsonomy.webapp.util.picture.PictureHandlerFactory;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * this controller handles picture download
 *
 * - /picture/user/USER
 * 
 * @author wla, cut
 */
public class PictureController implements MinimalisticController<PictureCommand>, ErrorAware, RequestAware {

	static {
		/*
		 * set the headless mode for awt library FIXME does it work? Should we
		 * really do this here? Better in a Tomcat config file, right?!
		 */
		System.setProperty("java.awt.headless", "true");
	}

	private RequestLogic requestLogic;

	private Errors errors = null;

	private PictureHandlerFactory pictureHandlerFactory;

	@Override
	public PictureCommand instantiateCommand() {
		return new PictureCommand();
	}

	@Override
	public View workOn(final PictureCommand command) {
		final String method = this.requestLogic.getMethod();

		final String requestedUser = command.getRequestedUser();
		if (present(requestedUser) && "GET".equals(method)) {
			/*
			 * picture download
			 */
			return this.downloadPicture(command);
		}

		return Views.ERROR;
	}

	/**
	 * Returns a view with the requested picture.
	 * 
	 * @param command
	 * @return
	 */
	private View downloadPicture(final PictureCommand command) {
		final String requestedUserName = command.getRequestedUser();
		final PictureHandler handler = this.pictureHandlerFactory.getPictureHandler(requestedUserName, command.getLoginUser());
		return handler.getProfilePictureView(command);
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param requestLogic
	 *            the requestLogic to set
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Sets this controller's {@link PictureHandlerFactory} instance.
	 * 
	 * @param factory
	 */
	public void setPictureHandlerFactory(final PictureHandlerFactory factory) {
		this.pictureHandlerFactory = factory;
	}
}
