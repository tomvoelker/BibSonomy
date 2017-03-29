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
package org.bibsonomy.webapp.controller.special;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * This controller only returns the configured view and else does nothing.
 * default view: {@link Views#ERROR}
 *
 * @author rja
 */
public class StaticViewController implements MinimalisticController<BaseCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(StaticViewController.class);

	private Views view = Views.ERROR;
	private Errors errors;

	@Override
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}

	@Override
	public View workOn(final BaseCommand command) {
		log.debug("returning view " + this.view);
		return this.view;
	}

	/**
	 * @return the view the controller returns
	 */
	public Views getView() {
		return this.view;
	}

	/** Set the view this controller shall return.
	 * @param view
	 */
	public void setView(final Views view) {
		this.view = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.webapp.util.ErrorAware#getErrors()
	 */
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.webapp.util.ErrorAware#setErrors(org.springframework.
	 * validation.Errors)
	 */
	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
}
