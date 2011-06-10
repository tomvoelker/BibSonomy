package org.bibsonomy.pingback;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Ignore;

/**
 * @author rja
 * @version $Id$
 */
@Ignore
public class TestServlet extends HttpServlet {
	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CHAR_ENCODING = "UTF-8";
	private static final String URLHERE = "URLHERE";
	private static final String PINGBACK_HEADER = "X-Pingback";
	private static final String PINGBACK_PATH = "/pingback";
	private static final String PINGBACK_XMLRPC = "/xmlrpc";
	private static final String PINGBACK_HTML = "<link rel=\"pingback\" href=\"" + URLHERE + "\" />\n";

	private static final String TOP_OF_HTML_PAGE = "" +
	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\">\n" +
	"\n" +
	"    <head profile=\"http://gmpg.org/xfn/11\">\n" +
	"    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
	"\n" +
	"    <title>Something blah blah &laquo;  Ping tester</title>\n" +
	"\n";

	private static final long serialVersionUID = 8692283813700271210L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final String pathInfo = request.getPathInfo();
		final StringBuffer requestURL = request.getRequestURL();

		System.out.println("GET " + requestURL);

		final boolean body   = present(request.getParameter("body"));
		final boolean header = present(request.getParameter("header"));		

		response.setCharacterEncoding(CHAR_ENCODING);
		response.setContentType(CONTENT_TYPE_HTML);

		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), CHAR_ENCODING));
		out.write(TOP_OF_HTML_PAGE);

		if (pathInfo.startsWith(PINGBACK_PATH)) {
			if (header) {
				response.setHeader(PINGBACK_HEADER, requestURL.toString() + PINGBACK_XMLRPC);
			}
			if (body) {
				out.write(PINGBACK_HTML.replace(URLHERE, requestURL + PINGBACK_XMLRPC));
			}
		}
		out.write("\n");
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
		final StringBuffer requestURL = request.getRequestURL();
		System.out.println("POST " + requestURL);
		if (requestURL.toString().endsWith("/xmlrpc")) {
			/*
			 * IN
			 */
			final StringBuilder buf = new StringBuilder();
			final BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buf.append(line + "\n");
			}
			reader.close();
			System.out.println(buf);

			/*
			 * out
			 */
			response.setCharacterEncoding(CHAR_ENCODING);
			response.setContentType("application/xml");
			final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), CHAR_ENCODING));
			out.write("<?xml version=\"1.0\"?>\n" + 
					"<methodResponse>\n" +
					"<params>\n" +
					"<param>\n" +
					"<value><string>success</string></value>\n" +
					"</param>\n" +
					"</params>\n" +
			"</methodResponse>\n");
			out.flush();
		}
	};

}