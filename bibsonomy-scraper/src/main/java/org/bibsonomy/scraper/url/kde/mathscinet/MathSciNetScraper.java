package org.bibsonomy.scraper.url.kde.mathscinet;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;


/**
 * Scraper for ams.org/mathscinet.
 * Reference can be entered as posts(bibtex page and overview page) and as snippet.
 * @author tst
 *
 */
public class MathSciNetScraper extends UrlScraper {
	
	private static final String INFO = "MathSciNetScraper: Extracts publications from " + href("http://www.ams.org/mathscinet/" , "MathSciNet") + 
	". Publications can be entered as a marked bibtex snippet or by posting the page of the reference.";

	
	/*
	 * URL components
	 */
	private static final String URL_MATHSCINET_HOST = "ams.org";
	private static final String URL_MATHSCINET_PATH = "/mathscinet";
	private static final String URL_MATHSCINET_FMT_PARAMETER = "fmt=bibtex";
		
	/*
	 * important HTML elements for bibtex link 
	 */
	private static final String HTML_INPUT_NAME_PG1 = "pg1";
	private static final String HTML_INPUT_NAME_S1 = "s1";
	
	/*
	 * regualar expressions for complete elements
	 */
	private static final Pattern PATTERN_COMPLETE_DIV = Pattern.compile("<div id=\"selectAlternative\".*</div>");
	private static final Pattern prePattern   = Pattern.compile("<pre>.*</pre>", Pattern.DOTALL);
	private static final Pattern inputPattern = Pattern.compile("<input(.*)/>");
	
	/*
	 * regualar expressions for start tags
	 */
	private static final Pattern divPattern  = Pattern.compile("<div(.*)>");
	private static final Pattern formPattern = Pattern.compile("<form(.*)>");
	
	/*
	 * regualar expressions for attributes
	 */
	private static final Pattern valuePattern   = Pattern.compile("value=\"[^\"]*\"");
	private static final Pattern namePattern    = Pattern.compile("name=\"[^\"]*\"");
	private static final Pattern idPattern      = Pattern.compile("id=\"[^\"]*\"");
	private static final Pattern actionPattern  = Pattern.compile("action=\"[^\"]*\"");

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile("^.*" + URL_MATHSCINET_HOST), Pattern.compile(URL_MATHSCINET_PATH + ".*")));

	/**
	 * Extract a reference from a bibtex page as post and snippet. It also extracts a single references from its overview page (get its bibtex link and download it). 
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			String urlToBibtex = null;
			/*
			 * check of snippet
			 */			
			if(sc.getSelectedText() != null && sc.getUrl().toString().contains(URL_MATHSCINET_FMT_PARAMETER)){
				sc.setBibtexResult(sc.getSelectedText());
				return true;
				
			/*
			 * no snippet, check content from url
			 */
			}else if(sc.getUrl().toString().contains(URL_MATHSCINET_FMT_PARAMETER)){
				// is bibtex page
				urlToBibtex = sc.getUrl().toString();
				
			}else{
				try{
					// html page, extract bibtex URL
					String div = getDiv(sc.getPageContent());
					
					/*
					 * get all elements for bibtex url
					 * - path
					 * - parameter pg1
					 * - parameter s1
					 */
					String path = getFormAction(div);
					String s1 = null;
					String pg1 = null;
					
					// search input field for s1 and pg1
					final Matcher inputMatcher = inputPattern.matcher(div);
					
					while(inputMatcher.find()){
						String input = inputMatcher.group();
						
						final Matcher nameMatcher = namePattern.matcher(input);
						
						while(nameMatcher.find()){
							String name = nameMatcher.group();
							name = name.substring(6, name.length()-1);
							
							// get values for s1 and pg1
							if(name.equals(HTML_INPUT_NAME_PG1)){
								pg1 = getInputValue(input, HTML_INPUT_NAME_PG1);							
							}else if(name.equals(HTML_INPUT_NAME_S1)){
								s1 = getInputValue(input, HTML_INPUT_NAME_S1);
							}
						}
					}
					
					// build link to bibtex
					if(path != null && pg1 != null && s1 != null){
						urlToBibtex = "http://www." + URL_MATHSCINET_HOST + path + "?" + URL_MATHSCINET_FMT_PARAMETER + "&" + pg1 + "&" + s1;

					// values for URL are missing
					}else
						throw new PageNotSupportedException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
					
				}catch(UnsupportedEncodingException uee){
					throw new InternalFailureException(uee);
				}
			}
			
			/*
			 * download bibtex page and extract bibtex
			 */
			if(urlToBibtex != null){
				try {
					String bibtexPage = sc.getContentAsString(new URL(urlToBibtex));
					
					//search pre element, which contains the bibtex reference
					Matcher preMatcher = prePattern.matcher(bibtexPage);
					
					// extract reference
					if(preMatcher.find()){
						String bibtex = preMatcher.group();
						bibtex = bibtex.substring(5, bibtex.length()-6);
						
						sc.setBibtexResult(bibtex);
						return true;
						
					// can't find bibtex
					}else
						throw new PageNotSupportedException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
					
				} catch (MalformedURLException e) {
					throw new InternalFailureException(e);
				}
			// can't find url for bibtex
			}else
				throw new PageNotSupportedException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
	}
	
	/**
	 * Search form and extract its action attribute to get the path for the bibtex link.
	 * @param div HTML div element and its content, which contains the form.
	 * @return The path for bibtex link.
	 */
	private String getFormAction(String div){
		String actionValue = null;
		
		// find form start tag
		final Matcher formMatcher = formPattern.matcher(div);
		
		if(formMatcher.find()){
			String formStartTag = formMatcher.group();
			
			// find action attribute
			Matcher actionAttributeMatcher = actionPattern.matcher(formStartTag);
			
			if(actionAttributeMatcher.find()){
				String actionAttribute = actionAttributeMatcher.group();
				// store value of action attribute
				actionValue = actionAttribute.substring(8, actionAttribute.length()-1);
			}
		}
		
		return actionValue;
	}
	
	/**
	 * Search div element with id = selectAlternative
	 * @param content HTML page from MathSciNet as String
	 * @return content HTML div element and its content as String
	 * @throws ScrapingException if div element could not found
	 */
	private String getDiv(String content)throws ScrapingException {
		String resultDiv = null;
		
		Matcher divMatcher = divPattern.matcher(content);
		
		// search div element with id="selectAlternative"
		if(divMatcher.find()){
			resultDiv = divMatcher.group();
		}
		
		if(resultDiv == null)
			throw new ScrapingException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
		
		return resultDiv;
	}
	
	/**
	 * Extract the value of the "value" attribute.
	 * @param input The input element as string.
	 * @param inputName The value of the name attribute.
	 * @return The value of the "value" attribute.
	 * @throws UnsupportedEncodingException
	 */
	private String getInputValue(String input, String inputName) throws UnsupportedEncodingException{
		String result = null;
		
		// search value attribute
		final Matcher valueMatcher = valuePattern.matcher(input);
		if(valueMatcher.find()){
			
			String value = valueMatcher.group();
			value = value.substring(7, value.length()-1);
			
			// value must be encoded to be used in url
			value = URLEncoder.encode(value, "UTF-8");
			
			// build parameter for url
			result = inputName + "=" + value;
		}
		
		return result;
	}

	public String getInfo() {
		return INFO;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
	
}
