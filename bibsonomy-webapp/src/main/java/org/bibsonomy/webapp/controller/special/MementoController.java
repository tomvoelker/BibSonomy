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
package org.bibsonomy.webapp.controller.special;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.services.memento.MementoService;
import org.bibsonomy.webapp.command.special.RedirectCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Interaction with Memento TimeGates, cf. http://www.mementoweb.org/
 * 
 * Given a URL and a timestamp, Memento tries to find a copy of that
 * URL in a web archive that is closest to the timestamp.
 * 
 * This controller is taking a URL and a timestamp as input and then 
 * calls the MementoService which queries the timegate for an appropriate
 * archived version.
 * 
 * The controller is available at /memento.
 * 
 * The links to /memento should be integrated with the rel="nofollow" 
 * attribute and the /memento URL should be excluded by robots.txt such 
 * that crawlers don't put too much load onto the time gate.
 * 
 * @author rja
 */
public class MementoController implements MinimalisticController<RedirectCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(MementoController.class);

	private MementoService mementoService;
	
	private Errors errors;

	/**
	 * We need two parameters:
	 * <ul>
	 * <li><pre>url</pre>: the URL for which we want to find an archived copy
	 * <li><pre>datetime</pre>: the timestamp for which we want to find an archived copy
	 * </ul>
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final RedirectCommand command) {
		log.debug("handling /memento URLs");
		final String url = command.getUrl();
		final Date datetime = command.getDatetime();
		// check for valid parameters
		if (!present(url)) {
			throw new MalformedURLSchemeException("parameter 'url' missing");
		}
		
		if (!present(datetime)) {
			throw new MalformedURLSchemeException("parameter 'datetime' missing");
		}
		// query timegate
		URL redirectUrl = null;
		try {
			redirectUrl = this.mementoService.getMementoUrl(url, datetime);
		} catch (final MalformedURLException e) {
			// ignore
		}
		// check result
		// TODO: handle case when timegate works well but there exists no archived version
		if (!present(redirectUrl)) {
			errors.reject("error.memento.notfound");
			return Views.ERROR;
		}
		// send redirect
		log.debug("finally redirecting to " + redirectUrl);
		return new ExtendedRedirectView(redirectUrl.toExternalForm());
	}

	/**
	 * @return the errors
	 */
	@Override
	public Errors getErrors() {
		return this.errors;
	}
	
	/**
	 * @param errors the errors to set
	 */
	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}



	@Override
	public RedirectCommand instantiateCommand() {
		return new RedirectCommand();
	}
	
	/**
	 * @param mementoService The Memento service that handles requests to the TimeGate.
	 */
	public void setMementoService(MementoService mementoService) {
		this.mementoService = mementoService;
	}
}