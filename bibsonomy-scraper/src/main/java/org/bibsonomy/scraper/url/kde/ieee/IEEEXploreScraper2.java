package org.bibsonomy.scraper.url.kde.ieee;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * TODO: add documentation to this class
 *
 * @author Johannes
 */
public class IEEEXploreScraper2 extends AbstractUrlScraper{
	private static final String SITE_URL = "http://ieeexplore.ieee.org/";
	private static final String SITE_NAME = "IEEEXplore";
	private static final String info = "This scraper creates a BibTeX entry for the media at " + AbstractUrlScraper.href(SITE_URL, SITE_NAME) + ".";
	private static final Pattern URL_PATTERN_BKN      = Pattern.compile("bkn=([^&]*)");
	private static final Pattern URL_PATTERN_ARNUMBER = Pattern.compile("arnumber=([^&]*)");
	private static final Pattern URL_PATTERN_DOCUMENT = Pattern.compile("/document/([^&]*)");

	private static final String IEEE_HOST = "ieeexplore.ieee.org";
	private static final String IEEE_PATH = "xpl";
	//TODO: passt das pattern so, oder muss das noch irgendwie angepasst werden?
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + IEEE_HOST), Pattern.compile("/" + ".*")));



	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
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
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String recordIds = ExtractID(sc);
		
		if(recordIds != null){
			String downUrl = "http://ieeexplore.ieee.org/xpl/downloadCitations";

			//using own client because I do not want to configure any client to allow circular redirects
			HttpClient client = WebUtils.getHttpClient();
			client.getParams().setBooleanParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);

			String bibtex = null;
			try {
				//better get the page first
				WebUtils.getContentAsString(client, sc.getUrl().toExternalForm());

				//create a post method
				PostMethod method = new PostMethod(downUrl);
				method.addParameter("citations-format", "citation-abstract");
				method.addParameter("fromPage", "");
				method.addParameter("download-format", "download-bibtex");
				method.addParameter("recordIds", recordIds);

				//now get bibtex
				bibtex = WebUtils.getPostContentAsString(client, method);
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

			if(bibtex != null){
				// clean up
				bibtex = bibtex.replace("<br>", "");

				// append url
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());

				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex.toString().trim());
				return true;

//			}else{
//				log.debug("IEEEXploreStandardsScraper: direct bibtex download failed. Use JTidy to get bibliographic data.");
//				sc.setBibtexResult(ieeeStandardsScrape(sc));
//				return true;
//
			}
//		}else{
//			log.debug("IEEEXploreStandardsScraper use JTidy to get Bibtex from " + sc.getUrl().toString());
//			sc.setBibtexResult(ieeeStandardsScrape(sc));
//			return true;
		}

		return false;
	}
	
	private String ExtractID(ScrapingContext sc){
		String recordIds = null;
		Matcher matcher = URL_PATTERN_DOCUMENT.matcher(sc.getUrl().toString());

		if (matcher.find()){
			recordIds = matcher.group(1);
		} else {

			matcher = URL_PATTERN_ARNUMBER.matcher(sc.getUrl().toString());

			if (matcher.find()){
				recordIds = matcher.group(1);
			}else{
				matcher = URL_PATTERN_BKN.matcher(sc.getUrl().toString());
				if (matcher.find()){
					recordIds = matcher.group(1);
				}
			}

		}
		return recordIds;
	}
}
