/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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

package org.bibsonomy.scraper.url.kde.webofknowledge;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Lukas
 */
public class WebOfKnowledgeScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "Web of Knowledge";
	private static final String SITE_URL = "http://apps.webofknowledge.com/";
	private static final String INFO = "Scrapes publications from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + "apps.webofknowledge.com"), 
			Pattern.compile("/full_record.do" + ".*")
	));

	//patterns to select ids which are essential for the download
	private static final Pattern sidPattern = Pattern.compile("SID=([^\\&]*)");
	private static final Pattern selectedIdsPattern = Pattern.compile("<(?=[\\w\\s=\\\"]*name=\\\"selectedIds\\\")(?=[\\w\\s=\\\"]*id=\\\"selectedIds\\\")[\\w\\s=\\\"]*value=\\\"(\\d+)\\\"[\\w\\s=\\\"/]*>");
	private static final Pattern qidPattern = Pattern.compile("qid=(\\d+)");
	private static final Pattern recordIDPattern = Pattern.compile("<(?=[\\w\\s=\\\":]*name=\\\"recordID\\\")[\\w\\s=\\\"]*value=\\\"([A-Z]+:\\d+)\\\"[\\w\\s=\\\"=/]*>");
	private static final Pattern downloadQidPattern = Pattern.compile("<(?=[\\w\\s=\\\"']*name=\\\"qid\\\")(?=[\\w\\s=\\\"']*id=\\\"qid\\\")[\\w\\s=\\\"']*value=[\\\"'](\\d+)[\\\"'][\\w\\s=\\\"'/]*>");
	
	//url to get pages with download information
	private static final String BASE_URL_1 = "http://apps.webofknowledge.com/OutboundService.do?action=go";
	//base url for bibtex download
	private static final String BASE_URL_2 = "http://ets.webofknowledge.com/ETS/saveDataToFile.do";

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		
		try {

			final URL pageUrl = sc.getUrl();
			// get cookie
			final String cookie = WebUtils.getCookies(new URL("http://webofknowledge.com/?DestApp=UA"));
			
			// get sid from url
			final Matcher sidMatcher = sidPattern.matcher(pageUrl.getQuery());
			final String sid;
			if(sidMatcher.find()) {
				sid = sidMatcher.group(1);
			} else {
				throw new ScrapingFailureException("article ID not found in URL");
			}

			//get qid from url
			final Matcher qidMatcher = qidPattern.matcher(pageUrl.getQuery());
			final String qid;
			if(qidMatcher.find()) {
				qid = qidMatcher.group(1);
			} else {
				throw new ScrapingFailureException("record ID not found in URL");
			}
			
			//get the publications page to extract more needed ids
			final String currentContent = WebUtils.getContentAsString(pageUrl, cookie);
			
			// get selectedIds from given page 
			final Matcher selectedIdsMatcher = selectedIdsPattern.matcher(currentContent);
			final String selectedIds;
			if(selectedIdsMatcher.find())
				selectedIds = selectedIdsMatcher.group(1);
			else
				throw new ScrapingFailureException("selected publications not found (selectedIds is missing)");

			//get the recordID from the publications page
			final Matcher recordIDMatcher = recordIDPattern.matcher(currentContent);
			final String recordID;
			if(recordIDMatcher.find()) {
				recordID = recordIDMatcher.group(1);
			} else {
				throw new ScrapingFailureException("record ID not found in URL");
			}
			
			// call post request to reach the download page
			final String content = WebUtils.getPostContentAsString(cookie,  new URL(BASE_URL_1), createPostParamString(recordID, sid, qid, selectedIds));
			
			//get the qid for the bibtex download from the download page (it's not the same qid as on the publications page extracted above!)
			final Matcher downloadQidMatcher = downloadQidPattern.matcher(content);
			String bibtex = null;
			if(downloadQidMatcher.find()) {
				//get the bibtex by a new post request using the extracted downloadQID
				bibtex = WebUtils.getPostContentAsString(cookie,  new URL(BASE_URL_2), getDownloadPostString(downloadQidMatcher.group(1), sid));
			} else {
				throw new ScrapingFailureException("Bibtex not found");
			}

			//the result bibtex is not correctly formatted so we remove all duplicate brackets
			bibtex = StringUtils.replaceEach(bibtex, new String[]{"{{",  "}}"}, new String[]{"{","}"});
			
			//return bibtex if everything worked successfully
			if(bibtex != null){
				sc.setBibtexResult(bibtex);
				return true;
			}
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	
		return false;		
	}

	/**
	 * creates a string of post parameters for access to the download information page
	 * 
	 * @param recordID
	 * @param sid
	 * @param qid
	 * @param selectedIds
	 * @return the post parameter string
	 */
	private String createPostParamString(final String recordID, final String sid, final String qid, final String selectedIds) {
			return "viewType=fullRecord&" +
			"product=UA&" +
			"mark_id=UA&" +
			"colName=WOS&" +
			"search_mode=GeneralSearch&" +
			"locale=go&" +
			"recordID=" + recordID.replace(":", "%3A") +"&" +
			"sortBy=PY.D%3BLD.D%3BSO.A%3BVL.D%3BPG.A%3BAU.A&" +
			"mode=outputService&" +
			"qid=" + qid + "&" +
			"SID=" + sid + "&" +
			"format=saveToFile&filters=USAGEIND+AUTHORSIDENTIFIERS+ACCESSION_NUM+FUNDING+SUBJECT_CATEGORY+JCR_CATEGORY+LANG+IDS+PAGEC+SABBR+CITREFC+ISSN+PUBINFO+KEYWORDS+CITTIMES+ADDRS+CONFERENCE_SPONSORS+DOCTYPE+ABSTRACT+CONFERENCE_INFO+SOURCE+TITLE+AUTHORS++&" +
			"selectedIds=" + selectedIds + "&" +
			"mark_to=&" +
			"mark_from=&" +
			"count_new_items_marked=0&" +
			"value%28record_select_type%29=selrecords&" +
			"marked_list_candidates=10&" +
			"LinksAreAllowedRightClick=CitedRefList.do&" +
			"LinksAreAllowedRightClick=CitingArticles.do&" +
			"LinksAreAllowedRightClick=OneClickSearch.do&" +
			"LinksAreAllowedRightClick=full_record.do&" +
			"bib_fields_option=ABSTRACT++&" +
			"fields_selection=USAGEIND+AUTHORSIDENTIFIERS+ACCESSION_NUM+FUNDING+SUBJECT_CATEGORY+JCR_CATEGORY+LANG+IDS+PAGEC+SABBR+CITREFC+ISSN+PUBINFO+KEYWORDS+CITTIMES+ADDRS+CONFERENCE_SPONSORS+DOCTYPE+ABSTRACT+CONFERENCE_INFO+SOURCE+TITLE+AUTHORS++&" +
			"save_options=bibtex";

	}
	
	/**
	 * creates a string of post parameters for access to the bibtex download
	 * 
	 * @param qid
	 * @param sid
	 * @return the post parameter string
	 */
	private String getDownloadPostString(final String qid, final String sid) {
		return "locale=go&"+
				"fileOpt=bibtex&"+
				"colName=WOS&"+
				"startYear=&"+
				"endYear=&"+
				"action=saveDataToFile&"+
				"qid="+qid+"&"+
				"parentQid=1&"+
				"sortBy=PY.D;LD.D;SO.A;VL.D;PG.A;AU.A&"+
				"filters=USAGEIND AUTHORSIDENTIFIERS ACCESSION_NUM FUNDING SUBJECT_CATEGORY JCR_CATEGORY LANG IDS PAGEC SABBR CITREFC ISSN PUBINFO KEYWORDS CITTIMES ADDRS CONFERENCE_SPONSORS DOCTYPE ABSTRACT CONFERENCE_INFO SOURCE TITLE AUTHORS&"+
				"numRecsToRetrieve=500&"+
				"SID="+sid+"&"+
				"product=UA&"+
				"numRecords=1&"+
				"subType=&"+
				"recNum=1&"+
				"mark_to=1";
	}
	
	@Override
	public String getInfo() {
		return INFO;
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

