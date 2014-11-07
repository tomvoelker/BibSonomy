/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

/**
 * 
 */
package org.bibsonomy.scraper.url.kde.karlsruhe;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;


/**
 * @author sre
 *
 */
public class UBKAScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "University Library (UB) Karlsruhe";
	private static final String UBKA_HOST_NAME = "http://www.ubka.uni-karlsruhe.de";
	private static final String SITE_URL = UBKA_HOST_NAME+"/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String UBKA_HOST = "ubka.uni-karlsruhe.de";
	private static final String UBKA_SEARCH_NAME = "http://www.ubka.uni-karlsruhe.de/hylib-bin/suche.cgi";
	private static final String UBKA_SEARCH_PATH = "/hylib-bin/suche.cgi";


	// bibtex id (fix value)
	private static final String UBKA_PARAM_BIBTEX = "bibtex=1";
	// opac id (fix value)
	private static final String UBKA_PARAM_OPACDB = "opacdb=UBKA_OPAC";
	// output id (free value, must be set)
	private static final String UBKA_PARAM_PRINTMAB = "printMAB=1";
	// query id (user dependent value)
	private static final String UBKA_PARAM_ND = "nd";

	private static final Pattern UBKA_BIB_PATTERN   = Pattern.compile(".*<td valign=\"top\"\\s*>\\s*(@[A-Za-z]+&nbsp;\\s*\\{.+}\\s).*", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern UBKA_COMMA_PATTERN = Pattern.compile("(.*keywords\\s*=\\s*\\{)(.*?)(\\},?<br>.*)", Pattern.MULTILINE | Pattern.DOTALL);	
	private static final Pattern UBKA_SPACE_PATTERN = Pattern.compile("&nbsp;");
	private static final Pattern UBKA_BREAK_PATTERN = Pattern.compile("<br>");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + UBKA_HOST), AbstractUrlScraper.EMPTY_PATTERN));


	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		if (UBKA_SEARCH_PATH.equals(sc.getUrl().getPath())) {
			/* URL looks some like this:
			 * http://www.ubka.uni-karlsruhe.de/hylib-bin/suche.cgi?opacdb=UBKA_OPAC&nd=256943346
			 * &session=1147556008&use_cookie_session=1&returnTo=http%3A%2F%2Fwww.ubka.uni-karlsruhe.de%2Fhylib%2Fka_opac.html
			 */	
			final String result;

			if (sc.getUrl().getQuery().contains(UBKA_PARAM_BIBTEX)){
				//current publication must be published as bibtex
				result = this.extractBibtexFromUBKA(sc.getPageContent());
			} else {
				//publication is not published as bibtex
				try {
					final URL expURL = new URL(UBKA_SEARCH_NAME + "?" + 
							UBKA_PARAM_OPACDB + "&" +
							UBKA_PARAM_ND + "=" + this.extractQueryParamValue(sc.getUrl().getQuery(), UBKA_PARAM_ND) + "&" +
							UBKA_PARAM_PRINTMAB + "&" + 
							UBKA_PARAM_BIBTEX);
					//download page and extract bibtex
					result = this.extractBibtexFromUBKA(WebUtils.getContentAsString(expURL));
				} catch (final IOException me) {
					throw new InternalFailureException(me);
				}
			}
			if (present(result)) {
				/*
				 * append URL and
				 * add downloaded BibTeX to result 
				 */
				sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(result, "url", sc.getUrl().toString()));

				/*
				 * returns itself to know, which scraper scraped this
				 */
				sc.setScraper(this);

				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		}
		
		throw new PageNotSupportedException("This UBKA URL is not supported!");
	}

	/**
	 * This method extracts bibtex entries from 
	 * @param pageContent The page content in a string.
	 * @return Extracted bibtex entry as a string.
	 * @throws ScrapingException
	 */
	private String extractBibtexFromUBKA(final String pageContent) throws ScrapingException{
		try {
			// replace all <br>
			final Matcher m = UBKA_BIB_PATTERN.matcher(UBKA_BREAK_PATTERN.matcher(pageContent).replaceAll(""));	
			if (m.matches()) { // we got the entry
				// replace &nbsp; spaces
				String bib = UBKA_SPACE_PATTERN.matcher(m.group(1)).replaceAll(" ");
				// TODO: decode Tex Macros, Tex Entities. Also @see AandAScraper.
				// replace comma in keywords={bla, bla, bla bla}
				final Matcher m2 = UBKA_COMMA_PATTERN.matcher(bib);
				if (m2.matches()){
					return m2.group(1) + m2.group(2).replaceAll(",", " ") + m2.group(3);
				}

				return bib;
			}
		} catch (final PatternSyntaxException pse) {
			throw new InternalFailureException(pse);
		}
		return null;
	}

	/**
	 * This method extracts the value of a specific parameter from a query string.
	 * @param query String representing the query part of an url. E.g. opacdb=UBKA_OPAC&nd=256943346
	 * &session=1147556008&use_cookie_session=1&returnTo=http%3A%2F%2Fwww.ubka.uni-karlsruhe.de%2Fhylib%2Fka_opac.html
	 * @param name Name of param to extract the value from.
	 * @return extracted value
	 */
	private String extractQueryParamValue(final String query, final String name) throws ScrapingException{
		final StringTokenizer st = new StringTokenizer(query,"&=",true);
		final Properties params = new Properties();
		String previous = null;
		while (st.hasMoreTokens()) {
			final String currToken = st.nextToken();
			if ("?".equals(currToken) || "&".equals(currToken)) {
				//ignore
			} else if ("=".equals(currToken)) {
				params.setProperty(UrlUtils.safeURIDecode(previous), UrlUtils.safeURIDecode(st.nextToken()));
			} else {
				previous = currToken;
			}
		}

		return (String) params.get(name);
	}

	@Override
	public String getInfo() {
		return info;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
