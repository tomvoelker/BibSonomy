package org.bibsonomy.memento;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Interaction with Memento TimeGates, cf. http://www.mementoweb.org/
 * 
 * Given a URL and a timestamp, Memento tries to find a copy of that
 * URL in a web archive that is closest to the timestamp.
 * 
 * @author rja
 *
 */
public class MementoServlet extends HttpServlet {

	private static final long serialVersionUID = 59874985723483145L;
	private MementoService memento;
	
	/**
	 * Initialize the TimeGate that shall be used.
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		final String initParameter = config.getInitParameter("timegate");
		if (present(initParameter)) {
			// cast to URL
			this.memento = new MementoService(new URL(initParameter));
		}
		// TODO Auto-generated method stub
		super.init(config);
	}
	
	/**
	 * We need two parameters:
	 * <ul>
	 * <li><pre>url</pre>: the URL for which we want to find a copy
	 * <li><pre>datetime</pre>: the timestamp at which we want to find a copy
	 * </ul>
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String url = request.getParameter("url");
		final String datetime = request.getParameter("datetime");
		// check for valid parameters
		if (! (present(url) && present(datetime))) { 
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		// query TimeGet
		final URL redirectUrl = this.memento.getMementoUrl(url, datetime);
		// check URL
		if (!present(redirectUrl)) {
			response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
			return;
		}
		// send redirect
		response.sendRedirect(redirectUrl.toExternalForm());
	}
	
}
