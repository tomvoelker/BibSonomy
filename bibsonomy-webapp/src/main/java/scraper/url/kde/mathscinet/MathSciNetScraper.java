package scraper.url.kde.mathscinet;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

/**
 * Scraper for ams.org/mathscinet.
 * Reference can be entered as posts(bibtex page and overview page) and as snippet.
 * @author tst
 *
 */
public class MathSciNetScraper implements Scraper {
	
	private static final String INFO = "MathSciNetScraper: Extracts publications from ams.org/mathscinet . Publications can be entered as a marked bibtex snippet or by posting the page of the reference.";

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
	private static final String PATTERN_COMPLETE_DIV = "<div id=\"selectAlternative\".*</div>";
	private static final String PATTERN_PRE = "<pre>.*</pre>";
	private static final String PATTERN_INPUT = "<input(.*)/>";
	
	/*
	 * regualar expressions for start tags
	 */
	private static final String PATTERN_DIV = "<div(.*)>";
	private static final String PATTERN_FORM = "<form(.*)>";
	
	/*
	 * regualar expressions for attributes
	 */
	private static final String PATTERN_VALUE = "value=\"[^\"]*\"";
	private static final String PATTERN_NAME = "name=\"[^\"]*\"";
	private static final String PATTERN_ID = "id=\"[^\"]*\"";
	private static final String PATTERN_ACTION = "action=\"[^\"]*\"";


	/**
	 * Extract a reference from a bibtex page as post and snippet. It also extracts a single references from its overview page (get its bibtex link and download it). 
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(URL_MATHSCINET_HOST) && sc.getUrl().getPath().startsWith(URL_MATHSCINET_PATH)){
			
			String urlToBibtex = null;
			/*
			 * check of snippet
			 */			
			if(sc.getSelectedText() != null && sc.getUrl().toString().contains(URL_MATHSCINET_FMT_PARAMETER)){
				sc.setBibtexResult(sc.getSelectedText());
				sc.setScraper(this);
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
					Pattern inputPattern = Pattern.compile(PATTERN_INPUT);
					Matcher inputMatcher = inputPattern.matcher(div);
					
					while(inputMatcher.find()){
						String input = inputMatcher.group();
						
						Pattern namePattern = Pattern.compile(PATTERN_NAME);
						Matcher nameMatcher = namePattern.matcher(input);
						
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
						throw new ScrapingException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
					
				}catch(UnsupportedEncodingException uee){
					throw new ScrapingException(uee);
				}
			}
			
			/*
			 * download bibtex page and extract bibtex
			 */
			if(urlToBibtex != null){
				try {
					String bibtexPage = sc.getContentAsString(new URL(urlToBibtex));
					
					//search pre element, which contains the bibtex reference
					Pattern prePattern = Pattern.compile(PATTERN_PRE, Pattern.DOTALL);
					Matcher preMatcher = prePattern.matcher(bibtexPage);
					
					// extract reference
					if(preMatcher.find()){
						String bibtex = preMatcher.group();
						bibtex = bibtex.substring(5, bibtex.length()-6);
						
						sc.setBibtexResult(bibtex);
						sc.setScraper(this);
						return true;
						
					// can't find bibtex
					}else
						throw new ScrapingException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
					
				} catch (MalformedURLException e) {
					throw new ScrapingException(e);
				}
			// can't find url for bibtex
			}else
				throw new ScrapingException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
		}
		
		return false;
	}
	
	/**
	 * Search form and extract its action attribute to get the path for the bibtex link.
	 * @param div HTML div element and its content, which contains the form.
	 * @return The path for bibtex link.
	 */
	private String getFormAction(String div){
		String actionValue = null;
		
		// find form start tag
		Pattern formPattern = Pattern.compile(PATTERN_FORM);
		Matcher formMatcher = formPattern.matcher(div);
		
		if(formMatcher.find()){
			String formStartTag = formMatcher.group();
			
			// find action attribute
			Pattern actionAttributePattern = Pattern.compile(PATTERN_ACTION);
			Matcher actionAttributeMatcher = actionAttributePattern.matcher(formStartTag);
			
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
		
		Pattern divPattern = Pattern.compile(PATTERN_COMPLETE_DIV, Pattern.DOTALL);
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
		Pattern valuePattern = Pattern.compile(PATTERN_VALUE);
		Matcher valueMatcher = valuePattern.matcher(input);
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

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
	
}
