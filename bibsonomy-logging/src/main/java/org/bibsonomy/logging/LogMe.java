/**
 * 
 */
package org.bibsonomy.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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

/**
 * @author sst
 * @version $Id$
 */
public class LogMe extends HttpServlet {
	private static final long serialVersionUID = 7035035642527692979L;

	private static final Log log = LogFactory.getLog(LogMe.class);

	public static List<String> getMatches(final Pattern pattern, final String text, final int splitAtSpace) {
		final List<String> matches = new ArrayList<String>();
		final Matcher m = pattern.matcher(text);
		while (m.find()) {
			if (m.group(1).contains(" ") && (splitAtSpace == 1)) {
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

	public static List<String> getMatches(final Pattern pattern, final String text) {
		return getMatches(pattern, text, 0);
	}

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
		/*
		 * Antwort am Anfang senden, damit Browser nicht warten muss
		 */
		response.setContentType("text/html");
		final PrintWriter pw = new PrintWriter(response.getOutputStream());
		pw.close();

		int dompath_length = 0;

		try {
			dompath_length = req.getParameter("dompath").length();
		} catch (final NullPointerException e) {
			// kein dompath angegeben
		}

		log.debug("dompath_lenght = " + dompath_length);

		// if dompath is empty
		if (dompath_length > 0) {
			// schreibe Cookies in Cookie-Array cookie
			final Cookie[] cookies = req.getCookies();

			@SuppressWarnings("unchecked")
			final Enumeration<String> headerNames = req.getHeaderNames();
			String cookieUsername = "";
			String cookieSessionId = "";
			String completeHeader = "";
			String logType = "";
			int separatorIndex = 0;

			while (headerNames.hasMoreElements()) {
				final String element = headerNames.nextElement();
				completeHeader += (element + ": " + req.getHeader(element) + "\n");
			}

			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					// FIXME: cookie name changed!! and content has changed too
					if (cookie.getName().equals("_currUser")) {
						separatorIndex = cookie.getValue().indexOf("%20");
						cookieUsername = cookie.getValue().substring(0, separatorIndex);
					}

					if (cookie.getName().equals("JSESSIONID")) {
						cookieSessionId = cookie.getValue();
					}
				}
			}

			// build an array for used ids with all char strings, beginning with
			// # and ending with / or .
			// (ending with non a-z, A-Z, 0-9 or -)
			// regular expression: /#([A-Za-z0-9\-]+)/
			Pattern p = Pattern.compile("#([a-zA-Z0-9-_]+)");
			String text = req.getParameter("dompath2");
			// TODO: id array unused
			final List<String> idArray = getMatches(p, text);
			
			// then build another array for used classes with all char strings,
			// beginning with . and ending with / or .
			// if class contains spaces, split it to multiple classes

			// regular expression: /\.[A-Za-z0-9\- ]+/

			p = Pattern.compile("\\.([a-zA-Z0-9- _]+)");
			text = req.getParameter("dompath2");
			final List<String> classArray = getMatches(p, text, 1);

			// System.out.println (classArray.toString());

			// logType is the type of logging information
			// where in page has user clicked? Bookmark area,...
			logType = req.getParameter("dompath").replaceFirst("^[^#]+#", "");
			logType = logType.replaceFirst("/.*$", "");

			// if class tagcloud exists, add to type with blank inbetween
			// if class bmown set bmown-value to 1 otherwise to 0

			String abmown = "0";
			// if classArray contains class bmown, then link is users own
			// bookmark
			if (classArray.contains("bmown")) {
				abmown = "1";
			} else {
				abmown = "0";
			}

			final org.bibsonomy.logging.Log LogData = new org.bibsonomy.logging.Log();

			LogData.setAhref(req.getParameter("ahref"));
			LogData.setAcontent(req.getParameter("acontent"));
			LogData.setAnumberofposts(req.getParameter("numberofposts"));
			LogData.setDompath(req.getParameter("dompath"));
			LogData.setDompath2(req.getParameter("dompath2"));
			LogData.setType(logType);
			LogData.setPageurl(req.getParameter("pageurl"));
			LogData.setUseragent(req.getHeader("user-agent"));

			if (req.getParameter("username").isEmpty()) {
				LogData.setUsername(cookieUsername);
			} else {
				LogData.setUsername(req.getParameter("username"));
			}

			LogData.setSessionid(cookieSessionId);
			LogData.setHost(req.getHeader("host"));
			LogData.setCompleteheader(completeHeader);
			LogData.setXforwardedfor(req.getHeader("X-Forwarded-For"));
			LogData.setListpos(req.getParameter("listpos"));
			LogData.setWindowsize(req.getParameter("windowsize"));
			LogData.setMouseclientpos(req.getParameter("mouseclientpos"));
			LogData.setMousedocumentpos(req.getParameter("mousedocumentpos"));
			LogData.setAbmown(abmown);
			LogData.setReferer(req.getParameter("referer"));

			log.debug("LogData to insert:\n" + LogData.toString());
			log.info("Clicked at anchor with shown text: " + LogData.getAcontent());

			try {
				QueryDB.getInstance().insertLogdata(LogData);
				log.info("Database access: insertLogdata ok");

			} catch (final SQLException e) {
				log.error("Database error: insertLogdata", e);
			}
		}
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
		log.debug("POST-Request");
		this.doGet(req, response);
	}
}