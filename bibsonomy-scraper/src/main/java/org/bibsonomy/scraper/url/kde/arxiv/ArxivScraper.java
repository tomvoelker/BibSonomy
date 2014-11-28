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

package org.bibsonomy.scraper.url.kde.arxiv;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.OAIConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;


/** Scraper for arXiv.
 * 
 * @author rja
 *
 */
public class ArxivScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "arXiv";
	private static final String SITE_URL = "http://arxiv.org/";
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";
	private static final String ARXIV_HOST = "arxiv.org";
	
	private static final Pattern patternID = Pattern.compile("(abs|pdf)/([^?]*)");
	private static final Pattern patternVer = Pattern.compile("(.+?)v\\d+");
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(ARXIV_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(ARXIV_HOST)) {
			try {
				sc.setScraper(this);
				
				final Matcher matcherID = patternID.matcher(sc.getUrl().toString());
				if(matcherID.find()) {
					final String id = matcherID.group(2);
					
					/* 
					 	OAI interface supports only the notion of an arXiv article and not access to individual versions.
					  	If an id is with version(eg. 1304.7984v1), the version part has to be removed(eg. 1304.7984). 
					*/
					String vId = "";
					
					Matcher verID = patternVer.matcher(id);
					if(verID.find()) vId = verID.group(1);
					else vId = id;
									
					// build url for oai_dc export
					String exportURL = "http://export.arxiv.org/oai2?verb=GetRecord&identifier=oai:arXiv.org:" + vId + "&metadataPrefix=oai_dc";
					
					// download oai_dc reference
					String reference = WebUtils.getContentAsString(exportURL);
					
					String bibtex = OAIConverter.convert(reference);
					
					
					// add arxiv citation to note
					if(bibtex.contains("note = {"))
						bibtex = bibtex.replace("note = {", "note = {cite arxiv:" + id + "\n");
					// if note not exist
					else
						bibtex = bibtex.replaceFirst("},", "},\nnote = {cite arxiv:" + id + "},");
					
					// set result
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("no arxiv id found in URL");
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}
		}		
		return false;
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
