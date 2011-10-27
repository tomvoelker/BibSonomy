package org.bibsonomy.android.loggingservice;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author dzo
 * @version $Id$
 */
public class AndroidLoggingServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 2749672697194930068L;
	
	private static final Log log = LogFactory.getLog(AndroidLoggingServiceServlet.class);
	
	
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		// do nothing
	}
	
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final String message = request.getParameter("message");
		
		if (present(message)) {
			log.warn(message);
		}
	}
}
