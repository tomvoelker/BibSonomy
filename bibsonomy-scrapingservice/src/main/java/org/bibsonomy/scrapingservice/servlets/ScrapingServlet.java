/**
 *  
 *  BibSonomy-Scrapingservice - Web application to test the BibSonomy web page scrapers (see
 * 		bibsonomy-scraper)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.scraper.KDEScraperFactory;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.exceptions.UseageFailureException;
import org.bibsonomy.scrapingservice.beans.ScrapingResultBean;
import org.bibsonomy.scrapingservice.writers.RDFWriter;

import bibtex.parser.ParseException;


/**
 * Servlet implementation class for Servlet: ScrapingServlet
 *
 */
public class ScrapingServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5145534846771334947L;

	private static final Logger log = Logger.getLogger(ScrapingServlet.class);

	public ScrapingServlet() {
		super();
	}   	

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String urlString = request.getParameter("url");
		final String selection = request.getParameter("selection");
		final String format = request.getParameter("format");

		log.info("Scraping service called with url " + urlString);

		if (urlString != null && !urlString.trim().equals("")) {
			final ScrapingResultBean bean = new ScrapingResultBean();

			try {
				final URL url = new URL(urlString);
				bean.setUrl(url);
				bean.setSelection(selection);

				final ScrapingContext context = new ScrapingContext(url);
				context.setSelectedText(selection);

				final Scraper compositeScraper = new KDEScraperFactory().getScraper();

				if (compositeScraper.scrape(context)) {
					bean.setBibtex(context.getBibtexResult());
					bean.setErrorMessage(null);
					
					/*
					 * handle special output formats
					 */
					if ("bibtex".equals(format)) {
						/* *******************************************
						 * text/x-bibtex
						 * *******************************************/
						// should be: text/x-bibtex (according to /etc/mime.types)
						response.setContentType("text/plain");
						response.getOutputStream().write(bean.getBibtex().getBytes("UTF-8"));
						return;
					} else if ("rdf+xml".equals(format)) {
						/* *******************************************
						 * application/rdf+xml
						 * *******************************************/
						response.setContentType("application/rdf+xml");
						/*
						 * BibTeX -> model
						 */
						final SimpleBibTeXParser parser = new SimpleBibTeXParser();
						final BibTex bibtex = parser.parseBibTeX(bean.getBibtex());
						/*
						 * model -> RDF
						 */
						final RDFWriter writer = new RDFWriter(response.getOutputStream());
						writer.write(url.toURI(), bibtex);
						return;
					}
					
				} else {
					bean.setBibtex(null);
					bean.setErrorMessage("Given host is not supported by scraping service.");
				}

			} catch (final MalformedURLException e) {
				log.info("URL is malformed.", e);
				bean.setErrorMessage("URL is malformed.");
			} catch (final InternalFailureException e) {
				// internal failure 
				log.fatal("Internal error occurred.", e);
				bean.setErrorMessage("Internal error occurred: " + e.getMessage());
			} catch (final UseageFailureException e) {
				// a user has used a scraper in a wrong way
				log.info("Usage error.", e);
				bean.setErrorMessage(e.getMessage());
			} catch (final PageNotSupportedException e) {
				// a scraper can't scrape a page but the host is supported
				log.error("Given page is not supported.", e);
				bean.setErrorMessage("Given page is not supported.");
			} catch (final ScrapingFailureException e) {
				// getting bibtex failed (conversion failed)
				log.fatal("Failure during scraping occurred.", e);
				bean.setErrorMessage("Failure during scraping occurred: " + e.getMessage());
			}  catch (final ScrapingException e) {
				// something else
				log.error("General Error.", e);
				bean.setErrorMessage(e.getMessage());
			} catch (URISyntaxException e) {
				log.info("URL is URI.", e);
				bean.setErrorMessage("URL is no URI.");
			} catch (ParseException e) {
				log.info("Could not parse BibTeX.", e);
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
			if ("bibtex".equals(format)) {
				response.setContentType("text/plain");
				response.getOutputStream().write("".getBytes("UTF-8"));
				return;
			}
			request.setAttribute("bean", bean);
		}



		getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
	}   	  	    
}