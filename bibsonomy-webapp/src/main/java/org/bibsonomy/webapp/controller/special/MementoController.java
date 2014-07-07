package org.bibsonomy.webapp.controller.special;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.User;
import org.bibsonomy.services.memento.MementoService;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.special.RedirectCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.HeaderUtils;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.access.AccessDeniedException;
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
public class MementoController implements MinimalisticController<RedirectCommand> {
	private static final Log log = LogFactory.getLog(MementoController.class);
  
	private MementoService mementoService;

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
		final String datetime = command.getDatetime();
		// check for valid parameters
		if (!present(url)) { 
			throw new MalformedURLSchemeException("parameter 'url' missing");
		}
		if (!present(datetime)) { 
			throw new MalformedURLSchemeException("parameter 'datetime' missing");
		}
		// query timegate
		URL redirectUrl;
		try {
			redirectUrl = this.mementoService.getMementoUrl(url, datetime);
		} catch (final MalformedURLException e) {
			redirectUrl = null;
		}
		// check result
		// TODO: handle case when timegate works well but there exists no archived version
		if (!present(redirectUrl)) {
			throw new InternServerException("Could not retrieve archived version for " + url + ".");
		}
		// send redirect
		log.debug("finally redirecting to " + redirectUrl);
		return new ExtendedRedirectView(redirectUrl.toExternalForm());
	}

	@Override
	public RedirectCommand instantiateCommand() {
		return new RedirectCommand();
	}
	
	/**
	 * @return The Memento service
	 */
	public MementoService getMementoService() {
		return mementoService;
	}

	/**
	 * @param mementoService The Memento service that handles requests to the TimeGate.
	 */
	public void setMementoService(MementoService mementoService) {
		this.mementoService = mementoService;
	}
}