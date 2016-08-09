/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.id.kde.doi;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import bibtex.parser.ParseException;

/**
 * if the original URL of the {@link ScrapingContext} did point to a dx.doi.org or was a DOI before redirected by
 * the {@link DOIScraper} this scraper sends an GET request to dx.doi.org with the given DOI and requests a BibTex
 * file.
 * 
 * So this Scraper gets BibTex information if the basic URL was a DOI URL and none of the URL scrapers did match the redirected URL
 * 
 * @author lha
 */
public class ContentNegotiationDOIScraper implements Scraper {

	private static final String SITE_NAME = "ContentNegotiationDOIScraper";
	private static final String SITE_URL = "http://www.doi.org/";
	private static final String INFO = "The ContentNegotiationDOIScraper resolves bibtex directly from a given " + AbstractUrlScraper.href("http://www.doi.org/", "DOI") +
			", if no URL scraper matched the previously redirected page.";
	
	/**
	 * takes the original DOI URL and resolves the BibTex by using content negotiation via dx.doi.org
	 * (sends GET Request with "Accept"-field of header set to "application/x-bibtex")
	 * 
	 */
	@Override
	public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
		/*
		 * first way: check DOI URL
		 */
		final URL url = scrapingContext.getDoiURL();
		final URL originalUrl = scrapingContext.getUrl();
		String bibtexResult = "";
		
		if ((url != null) && DOIUtils.isDOIURL(url)) {
			bibtexResult = this.getBibTexByCN(url);
		}
		
		/*
		 * second way: DOI URL was not present, check whether maybe the original URL is still
		 *             a DOI URL which was not redirected (should not happen in fact of the DOI
		 *             scraper should have redirected the current URL)
		 */
		else if ((originalUrl != null) && DOIUtils.isDOIURL(originalUrl)) {
			bibtexResult = this.getBibTexByCN(originalUrl);
		}
		
		/*
		 * third way: ScrapingContext contains supported selection which could be used for the
		 *            request --> generate dx.doi.org URL and request the BibTex
		 */
		else if(DOIUtils.isSupportedSelection(scrapingContext.getSelectedText())) {
			final String doi = DOIUtils.extractDOI(scrapingContext.getSelectedText());
			try {
				bibtexResult = this.getBibTexByCN(DOIUtils.getURL(doi));
			} catch (final MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		}
		
		// check result
		if (present(bibtexResult)) {
			scrapingContext.setScraper(this);
			scrapingContext.setBibtexResult(bibtexResult);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Send a Content Negotiation request to get the BibTex 
	 * 
	 * @param url the URL to request
	 * @return the resulting BibTex
	 */
	private String getBibTexByCN(final URL url) throws InternalFailureException{
		// create request with content negotiation
		final HttpMethod getBibTexMethod = new GetMethod(url.toExternalForm());
		getBibTexMethod.addRequestHeader("Accept", "application/x-bibtex");
			
		// send request to dx.doi.org and receive resulting bibtex
		try {
			final String content = WebUtils.getContentAsString(getBibTexMethod);
			/*
			 * Unfortunately, content negotiation does not always work (TODO: why?). 
			 * Hence, we here check, if we really got BibTeX.
			 */
			final SimpleBibTeXParser parser = new SimpleBibTeXParser(); // not thread-safe!
			
			parser.parseBibTeX(content);
			
			return content;
		} catch (final HttpException ex) {
			throw new InternalFailureException(ex);
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		} catch (ParseException e) {
			throw new InternalFailureException("Server did not return BibTeX during content negotiation. Scraping not supported.");
		}
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}
	
	/**
	 * checks whether DOI URL has been set by the {@link DOIScraper}
	 */
	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
		return (scrapingContext.getDoiURL() != null) || 
				DOIUtils.isDOIURL(scrapingContext.getUrl()) || 
				DOIUtils.isSupportedSelection(scrapingContext.getSelectedText());
	}

	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return SITE_NAME;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return SITE_URL;
	}
}
