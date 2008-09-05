package org.bibsonomy.scrapingservice.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.KDEScraperFactory;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.exceptions.UseageFailureException;
import org.bibsonomy.scrapingservice.beans.ScrapingResultBean;


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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ScrapingResultBean bean = new ScrapingResultBean();
		
		String url = request.getParameter("scrapingUrl");
		URL scrapingUrl;
		try {
			scrapingUrl = new URL(url);
			bean.setUrl(url);
			
			ScrapingContext context = new ScrapingContext(scrapingUrl);
			
			Scraper compositeScraper = new KDEScraperFactory().getScraper();
		
			if(compositeScraper.scrape(context)){
				bean.setBibtex(context.getBibtexResult());
				bean.setErrorMessage(null);
			}else{
				bean.setBibtex(null);
				bean.setErrorMessage("Given host is not supported from scraping service.");
			}
		} catch (MalformedURLException e) {
			log.info(e);
			bean.setErrorMessage("Given URL is malformed.");
		} catch (final InternalFailureException e) {
			// internal failure 
			log.fatal(e);
			bean.setErrorMessage("Internal error occurred: " + e.getMessage());
		} catch (final UseageFailureException e) {
			// a user has used a scraper in a wrong way
			log.info(e);
			bean.setErrorMessage(e.getMessage());
		} catch (final PageNotSupportedException e) {
			// a scraper can't scrape a page but the host is supported
			log.error(e);
			bean.setErrorMessage("Given page is not supported.");
		} catch (final ScrapingFailureException e) {
			// getting bibtex failed (conversion failed)
			log.fatal(e);
			bean.setErrorMessage("Failure druing scraping occurred: " + e.getMessage());
		}  catch (final ScrapingException e) {
			// something else
			log.error(e);
			bean.setErrorMessage(e.getMessage());
		}
		
		request.setAttribute("result", bean);
		getServletConfig().getServletContext().getRequestDispatcher("/").forward(request, response);
	}   	  	    
}