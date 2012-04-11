/**
 *
 *  BibSonomy-Scrapingservice - Web application to test the BibSonomy web page scrapers (see
 * 		bibsonomy-scraper)
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scrapingservice.servlets;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.bibsonomy.scraper.KDEScraperFactory;
import org.bibsonomy.scraper.KDEUrlCompositeScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UrlCompositeScraper;
import org.bibsonomy.scraper.InformationExtraction.IEScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.exceptions.UsageFailureException;
import org.bibsonomy.scrapingservice.beans.ScrapingResultBean;
import org.bibsonomy.scrapingservice.writers.JSONWriter;
import org.bibsonomy.scrapingservice.writers.RDFWriter;

import bibtex.parser.ParseException;


/**
 * Servlet implementation class for Servlet: ScrapingServlet
 * @version $Id$
 */
public class ScrapingServlet extends HttpServlet {
	private static final long serialVersionUID = -5145534846771334947L;
	private static final Log log = LogFactory.getLog(ScrapingServlet.class);

	private static final String RESPONSE_ENCODING = "UTF-8";
	
	private static final String FORMAT_RDF = "rdf+xml";
	private static final String FORMAT_BIBTEX = "bibtex";
	private static final String FORMAT_XML = "xml";
	private static final String APPLICATION_XML_MIME_TYPE = RenderingFormat.APP_XML.getMimeType();

	
	private static final User XML_DUMMY_USER = new User("scrapingService");
	private static final XMLRenderer XML_RENDERER = new XMLRenderer(new UrlRenderer(""));
	
	/**
	 * Scrapers used in this servlet.
	 */
	private static final Scraper compositeScraper = new KDEScraperFactory().getScraperWithoutIE();
	private static final Scraper ieScraper = new IEScraper();
	private static final UrlCompositeScraper urlCompositeScraper = new KDEUrlCompositeScraper();

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final String urlString = request.getParameter("url");
		final String selection = request.getParameter("selection");
		final String format    = request.getParameter("format");
		final String action    = request.getParameter("action");
		final boolean doIE     = !"false".equals(request.getParameter("doIE")); // do information extraction, except when doIE=false

		final ScrapingResultBean bean = new ScrapingResultBean();
		request.setAttribute("bean", bean);

		log.info("Scraping service called with url " + urlString);

		if (present(urlString)) {
			/*
			 * url given -> try to scrape
			 */
		
			try {
				final URL url = new URL(urlString);
				bean.setUrl(url);
				bean.setSelection(selection);

				final ScrapingContext context = new ScrapingContext(url);
				context.setSelectedText(selection);

				/*
				 * Do IE only, if neccessary (i.e., compositeScraper can't handle it) and 
				 * when requested. 
				 */
				if (compositeScraper.scrape(context) || (doIE && ieScraper.scrape(context))) {
					bean.setBibtex(context.getBibtexResult());
					bean.setErrorMessage(null);
					bean.setScraper(context.getScraper());
					
					/*
					 * handle special output formats
					 */
					final String bibtexString = bean.getBibtex();
					if (FORMAT_BIBTEX.equals(format)) {
						/* *******************************************
						 * text/x-bibtex
						 * *******************************************/
						// should be: text/x-bibtex (according to /etc/mime.types)
						response.setContentType("text/plain");
						response.getOutputStream().write(bibtexString.getBytes(RESPONSE_ENCODING));
						return;
					} else if (FORMAT_RDF.equals(format)) {
						/* *******************************************
						 * application/rdf+xml
						 * *******************************************/
						response.setContentType("application/rdf+xml");
						/*
						 * BibTeX -> model
						 */
						final SimpleBibTeXParser parser = new SimpleBibTeXParser();
						final BibTex bibtex = parser.parseBibTeX(bibtexString);
						/*
						 * model -> RDF
						 */
						final RDFWriter writer = new RDFWriter(response.getOutputStream());
						writer.write(url.toURI(), bibtex);
						return;
					} else if (FORMAT_XML.equals(format)) {
						response.setContentType(APPLICATION_XML_MIME_TYPE);
						response.setCharacterEncoding(RESPONSE_ENCODING);
						
						/*
						 * parse post
						 */
						final Post<? extends Resource> post = new PostBibTeXParser().parseBibTeXPost(bibtexString);
						post.getResource().recalculateHashes();
						post.setUser(XML_DUMMY_USER);
						if (!present(post.getTags())) {
							post.getTags().add(TagUtils.getEmptyTag());
						}
						
						/*
						 * serialize to xml
						 * TODO: use EscapingPrintWriter?
						 */
						XML_RENDERER.serializePost(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), RESPONSE_ENCODING)), post, null);
						return;
					}
				} else {
					bean.setBibtex(null);
					bean.setErrorMessage("Given host is not supported by scraping service.");
				}

			} catch (final MalformedURLException e) {
				log.info("URL is malformed: " + e.getMessage() );
				bean.setErrorMessage("URL is malformed.");
			} catch (final InternalFailureException e) {
				// internal failure 
				log.fatal("Internal error occurred: " + e.getMessage());
				bean.setErrorMessage("Internal error occurred: " + e.getMessage());
			} catch (final UsageFailureException e) {
				// a user has used a scraper in a wrong way
				log.info("Usage error: " + e.getMessage());
				bean.setErrorMessage(e.getMessage());
			} catch (final PageNotSupportedException e) {
				// a scraper can't scrape a page but the host is supported
				log.error("Given page is not supported: " + e.getMessage());
				bean.setErrorMessage("Given page is not supported.");
			} catch (final ScrapingFailureException e) {
				// getting bibtex failed (conversion failed)
				log.fatal("Failure during scraping occurred.", e);
				bean.setErrorMessage("Failure during scraping occurred: " + e.getMessage());
			}  catch (final ScrapingException e) {
				// something else
				log.error("General Error: " + e.getMessage());
				bean.setErrorMessage(e.getMessage());
			} catch (final URISyntaxException e) {
				log.info("URL is not a URI: " + e.getMessage());
				bean.setErrorMessage("URL is no URI.");
			} catch (final ParseException e) {
				log.info("Could not parse BibTeX: " + e.getMessage());
				bean.setErrorMessage("Could not parse BibTeX.");
			}
			/*
			 * If the format is bibtex and we're still here, an error must have occurred.
			 * Nevertheless, we should not return a HTML page but rather an empty string
			 * such that bibtex parsers don't get rubbish input.  
			 * 
			 * To sum up: for format=bibtex the empty string means, we could get the 
			 * bibtex (for whatever reason) 
			 */
			if (FORMAT_BIBTEX.equals(format)) {
				response.setContentType("text/plain");
				response.getOutputStream().write("".getBytes(RESPONSE_ENCODING));
				return;
			}
			
			if (FORMAT_XML.equals(format)) {
				response.setContentType(APPLICATION_XML_MIME_TYPE);
				response.getOutputStream().write("".getBytes(RESPONSE_ENCODING));
				response.setStatus(HttpStatus.SC_NOT_FOUND);
				return;
			}
		} else if ("info".equals(action)) {
			log.info("action = info");
			/*
			 * print information about the available scrapers
			 * currently: patterns of url scrapers in JSON format
			 * TODO: use better parameter names 
			 */
			final List<Pair<Pattern, Pattern>> urlPatterns = urlCompositeScraper.getUrlPatterns();
			if ("json".equals(format)) {
				log.info("format = json");
				/*
				 * only supported format currently
				 */
				final JSONWriter writer = new JSONWriter(response.getOutputStream());
				response.setContentType("application/json");
				writer.write(0, "{\n");
				writer.write(1, "\"patterns\" : ");
				writer.write(1, urlPatterns);
				writer.write(0, "}\n");
				return;
			}
			
			bean.setErrorMessage("Requested format '" + format + "' not supported.");
		}



		getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
	}   	  	    
}