/**
 * BibSonomy-Logging - Logs clicks from users of the BibSonomy webapp.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.bibsonomy.logging;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;

/**
 * @author sst
 */
public class LoggingServlet extends HttpServlet {
	private static final long serialVersionUID = 7035035642527692979L;

	private static final Log log = LogFactory.getLog(LoggingServlet.class);

	private static List<String> getMatches(final Pattern pattern, final String text, final boolean splitAtSpace) {
		final List<String> matches = new ArrayList<String>();
		final Matcher m = pattern.matcher(text);
		while (m.find()) {
			if (m.group(1).contains(" ") && (splitAtSpace)) {
				final String[] tempMatchesM = m.group(1).split(" ");
				for (final String s : tempMatchesM) {
					matches.add(s);
				}
			} else {
				matches.add(m.group(1));
			}
		}
		return matches;
	}

	private static List<String> getMatches(final Pattern pattern, final String text) {
		return getMatches(pattern, text, false);
	}
	
	private LoggingDatabaseManager loggingDatabaseManager;

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
		/*
		 * send response immediately -> no wait time on the client side
		 */
		response.setContentType("text/html");
		final PrintWriter pw = new PrintWriter(response.getOutputStream());
		pw.close();

		/*
		 * extract informations from the request
		 */
		final String domPath = req.getParameter("dompath");
		if (present(domPath)) {
			// schreibe Cookies in Cookie-Array cookie
			final Cookie[] cookies = req.getCookies();

			@SuppressWarnings("unchecked")
			final Enumeration<String> headerNames = req.getHeaderNames();
			String cookieUsername = "";
			String cookieSessionId = "";
			final StringBuilder completeHeader = new StringBuilder();
			String logType = "";
			int separatorIndex = 0;

			while (headerNames.hasMoreElements()) {
				final String element = headerNames.nextElement();
				completeHeader.append(element).append(": ").append(req.getHeader(element)).append("\n");
			}

			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					// FIXME: cookie name has changed!! and content has changed too
					if (cookie.getName().equals("_currUser")) {
						separatorIndex = cookie.getValue().indexOf("%20");
						cookieUsername = cookie.getValue().substring(0, separatorIndex);
					}

					if (cookie.getName().equals("JSESSIONID")) {
						cookieSessionId = cookie.getValue();
					}
				}
			}

			/*
			 * build an array for used ids with all char strings, beginning with
			 * # and ending with / or .
			 * (ending with non a-z, A-Z, 0-9 or -)
			 * regular expression: /#([A-Za-z0-9\-]+)/
			 */
			Pattern p = Pattern.compile("#([a-zA-Z0-9-_]+)");
			String text = req.getParameter("dompath2");
			// TODO: id array unused
			final List<String> idArray = getMatches(p, text);
			
			/*
			 * then build another array for used classes with all char strings,
			 * beginning with . and ending with / or .
			 * if class contains spaces, split it to multiple classes
			 * regular expression: /\.[A-Za-z0-9\- ]+/
			 */
			p = Pattern.compile("\\.([a-zA-Z0-9- _]+)");
			text = req.getParameter("dompath2");
			final List<String> classArray = getMatches(p, text, true);

			/*
			 * logType is the type of logging information
			 * where in page has user clicked? Bookmark area,...
			 */
			logType = domPath.replaceFirst("^[^#]+#", "");
			logType = logType.replaceFirst("/.*$", "");

			/*
			 * if class tagcloud exists, add to type with blank in between
			 * if class bmown set bmown-value to 1 otherwise to 0
			 */
			String abmown = "0";
			
			/* 
			 * FIXME: does not work with the current layout
			 * if classArray contains class bmown, then link is users own
			 * bookmark
			 */
			if (classArray.contains("bmown")) {
				abmown = "1";
			} else {
				abmown = "0";
			}

			final LogData logData = new LogData();
			logData.setAhref(req.getParameter("ahref"));
			logData.setAcontent(req.getParameter("acontent"));
			logData.setAnumberofposts(req.getParameter("numberofposts"));
			logData.setDompath(domPath);
			logData.setDompath2(req.getParameter("dompath2"));
			logData.setType(logType);
			logData.setPageurl(req.getParameter("pageurl"));
			logData.setUseragent(req.getHeader("user-agent"));

			final String username = req.getParameter("username");
			if (!present(username)) {
				logData.setUsername(cookieUsername);
			} else {
				logData.setUsername(username);
			}

			logData.setSessionid(cookieSessionId);
			logData.setHost(req.getHeader("host"));
			logData.setCompleteheader(completeHeader.toString());
			logData.setXforwardedfor(req.getHeader("X-Forwarded-For"));
			logData.setListpos(StringUtils.cropToLengthAndMarkWithX(req.getParameter("listpos"), 20));
			logData.setWindowsize(StringUtils.cropToLengthAndMarkWithX(req.getParameter("windowsize"), 20));
			logData.setMouseclientpos(StringUtils.cropToLengthAndMarkWithX(req.getParameter("mouseclientpos"), 20));
			logData.setMousedocumentpos(StringUtils.cropToLengthAndMarkWithX(req.getParameter("mousedocumentpos"), 20));
			logData.setAbmown(abmown);
			logData.setReferer(req.getParameter("referer"));

			log.debug("LogData to insert:\n" + logData.toString());
			log.info("Clicked at anchor with shown text: " + logData.getAcontent());

			try {
				this.loggingDatabaseManager.insertLogdata(logData);
				log.info("Database access: insertLogdata ok");
			} catch (final Exception e) {
				log.error("Database error: insertLogdata", e);
			}
		}
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
		log.debug("POST-Request");
		this.doGet(req, response);
	}

	/**
	 * @param loggingDatabaseManager the loggingDatabaseManager to set
	 */
	public void setLoggingDatabaseManager(final LoggingDatabaseManager loggingDatabaseManager) {
		this.loggingDatabaseManager = loggingDatabaseManager;
	}
}