package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;


public class AIFBScraper implements Scraper {
	
	private static final String info = "AIFB Scraper: This scraper parses institute, research group and people-specific pages from the " +
									   "<a href=\"http://www.aifb.uni-karlsruhe.de/\">Institute AIFB Universität Karlsruhe</a>. Author: KDE";
	
	private static final String AIFB_HOST_NAME = "http://www.aifb.uni-karlsruhe.de";
	private static final String AIFB_DEPT_PATH = "/Forschungsgruppen/";
	private static final String AIFB_PERSON_PATH = "/Personen/viewPerson";	
	private static final String AIFB_PUBL_PATH = "/Publikationen/showPublikation";
	private static final String AIFB_PUBL_PATH_ENGLISH = "/Publikationen/showPublikation_english";
	private static final String AIFB_PUBL_PERSON_PATH = "/Publikationen/showPublikationen";
	private static final String AIFB_PUBL_EXPORT_PATH = "/Publikationen/exportPublikation.bib";
	private static final String AIFB_PUBL_PERSON_EXPORT_PATH = "/Publikationen/exportPublikationenPerson.bib";
	private static final String AIFB_PUBL_DEPT_EXPORT_PATH = "/Publikationen/exportPublikationenFG.bib";

    //person id param
	private static final String AIFB_PARAM_PERSON_ID = "person_id=";
	//dept. id param
	private static final String AIFB_PARAM_DEPT_ID = "fg_id=";
    //year of publication param
	private static final String AIFB_PARAM_YEAR = "jahr=";
	
	//dept. code of  Betriebliche Informations- und Kommunikationssysteme
	private static final String AIFB_DEPT_CODE_BIK = "BIK";
    //	dept. code of  Betriebliche Informations- und Kommunikationssysteme
	private static final int AIFB_DEPT_ID_BIK = 1;
	
	//dept. code of  Effiziente Algorithmen
	private static final String AIFB_DEPT_CODE_EFFALG = "EffAlg";
    //	dept. code of  Effiziente Algorithmen
	private static final int AIFB_DEPT_ID_EFFALG = 2;
	
	//dept. code of Wissensmanagement
	private static final String AIFB_DEPT_CODE_WBS = "WBS";
    //dept. id of Wissensmanagement
	private static final int AIFB_DEPT_ID_WBS = 3;
	
	//dept. code of Komplexitätsmanagement
	private static final String AIFB_DEPT_CODE_COM = "CoM";
    //dept. id of Komplexitätsmanagement
	private static final int AIFB_DEPT_ID_COM = 4;

    
	
	
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		if(sc.getUrl() != null && sc.getUrl().toString().startsWith(AIFB_HOST_NAME) && sc.getSelectedText() == null){
			/*
			 * returns itself to know, which scraper scraped this
			 */
			sc.setScraper(this);

		
			if(sc.getUrl().getPath().contains(AIFB_PUBL_EXPORT_PATH)){
				/* We are scraping a bibtex page!
				 * URL must be some like this:
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikation.bib?publ_id=867
				 * Just set the content as the result, because it contains just a bibtex entry.
				 */
				sc.setBibtexResult(sc.getPageContent());	
				return true;
			}else if(AIFB_PUBL_PERSON_EXPORT_PATH.equals(sc.getUrl().getPath())){
				/* We are scraping a bibtex page!
				 * URL must be some like this:
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationenPerson.bib?person_id=3
				 * Just set the content as the result, because it already contains bibtex entries.
				 */
				sc.setBibtexResult(sc.getPageContent());	
				return true;
			}else if(AIFB_PUBL_DEPT_EXPORT_PATH.equals(sc.getUrl().getPath())){
				/* We are scraping a bibtex page!
				 * URL must be some like this:
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationenFG.bib?fg_id=3&jahr=2006 (publ. of specific dept.) or
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationenFG.bib?jahr=2006 (publ. of all depts.)
				 * Just set the content as the result, because it already contains bibtex entries.
				 */
				sc.setBibtexResult(sc.getPageContent());	
				return true;
			}else if(AIFB_PUBL_PATH.equals(sc.getUrl().getPath()) || AIFB_PUBL_PATH_ENGLISH.equals(sc.getUrl().getPath())){
				/* We are scraping a publication page. Just extract the parameter publ_id
				 * to call export publication page.
				 * URL must be like this: 
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/showPublikation?publ_id=867
				 * Scrape following URL to get the adequate bibtex entry:
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationen.bib?publ_id=867
				 */
				try {
					URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_EXPORT_PATH + "?" + sc.getUrl().getQuery());
					sc.setBibtexResult(sc.getContentAsString(expURL));
					return true;
				} catch (MalformedURLException me) {
					throw new InternalFailureException(me);
				}
				
			}else if(sc.getUrl().getPath().contains(AIFB_PERSON_PATH)){
				/* We are scraping a person page. Just extract id_db and call the export page.
				 * URL must be like this: 
				 * http://www.aifb.uni-karlsruhe.de/Personen/viewPerson?id_db=29 or
				 * http://www.aifb.uni-karlsruhe.de/Personen/viewPersonenglish?id_db=29
				 * Scrape following URL to get the adequate bibtex entries:
				 * http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationenPerson.bib?person_id=29
				 */
				try {					
					URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_PERSON_EXPORT_PATH + "?" +  
										 AIFB_PARAM_PERSON_ID + extractParamValue(sc.getUrl().getQuery()));
					sc.setBibtexResult(sc.getContentAsString(expURL)); 
					return true;
				} catch (MalformedURLException me) {
					throw new InternalFailureException(me);
				}
				
			}else if(sc.getUrl().getPath().contains(AIFB_PUBL_PERSON_PATH)){
				/*
				 *URL must be like this: 
				 *http://www.aifb.uni-karlsruhe.de/Publikationen/showPublikationen?id_db=2096 or
				 *http://www.aifb.uni-karlsruhe.de/Publikationen/showPublikationen_english?id_db=2096
				 *Scrape following URL to get the adequate bibtex entry:
				 *http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationenPerson.bib?person_id=2096
				 */
				try {					
					URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_PERSON_EXPORT_PATH + "?" +  
										 AIFB_PARAM_PERSON_ID + extractParamValue(sc.getUrl().getQuery()));
					sc.setBibtexResult(sc.getContentAsString(expURL)); 
					return true;
				} catch (MalformedURLException me) {
					throw new InternalFailureException(me);
				}
				
			}else if(sc.getUrl().getPath().toString().startsWith(AIFB_DEPT_PATH)){
				Calendar cal = new GregorianCalendar(); 
				String currYear = Integer.toString(cal.get(Calendar.YEAR)); 
				//handle all dept. urls
				if(sc.getUrl().getPath().toString().contains(AIFB_DEPT_CODE_WBS)){
					try {
						//if param year is not set, use current year
						if(sc.getUrl().getQuery() != null && sc.getUrl().getQuery().contains(AIFB_PARAM_YEAR)){
							currYear = extractParamValue(sc.getUrl().getQuery());
						}
						//http://www.aifb.uni-karlsruhe.de/Publikationen/exportPublikationenFG.bib?fg_id=3&jahr=2006
						URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_DEPT_EXPORT_PATH + "?" 
								           + AIFB_PARAM_DEPT_ID + AIFB_DEPT_ID_WBS + "&"
								           + AIFB_PARAM_YEAR + currYear);
						sc.setBibtexResult(sc.getContentAsString(expURL));
						return true;
					} catch (MalformedURLException me) {
						throw new InternalFailureException(me);
					}	
				}else if(sc.getUrl().getPath().toString().contains(AIFB_DEPT_CODE_BIK)){
					try {
						//if param year is not set, use current year
						if(sc.getUrl().getQuery() != null && sc.getUrl().getQuery().contains(AIFB_PARAM_YEAR)){
							currYear = extractParamValue(sc.getUrl().getQuery());
						}
						URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_DEPT_EXPORT_PATH + "?" 
								           + AIFB_PARAM_DEPT_ID + AIFB_DEPT_ID_BIK + "&"
								           + AIFB_PARAM_YEAR + currYear);
						sc.setBibtexResult(sc.getContentAsString(expURL));
						return true;
					} catch (MalformedURLException me) {
						throw new InternalFailureException(me);
					}
				}else if(sc.getUrl().getPath().toString().contains(AIFB_DEPT_CODE_EFFALG)){
					try {
						//if param year is not set, use current year
						if(sc.getUrl().getQuery() != null && sc.getUrl().getQuery().contains(AIFB_PARAM_YEAR)){
							currYear = extractParamValue(sc.getUrl().getQuery());
						}
						URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_DEPT_EXPORT_PATH + "?" 
								           + AIFB_PARAM_DEPT_ID + AIFB_DEPT_ID_EFFALG + "&"
								           + AIFB_PARAM_YEAR + currYear);
						sc.setBibtexResult(sc.getContentAsString(expURL));
						return true;
					} catch (MalformedURLException me) {
						throw new InternalFailureException(me);
					}
				}else if(sc.getUrl().getPath().toString().contains(AIFB_DEPT_CODE_COM)){
					try {
						//if param year is not set, use current year
						if(sc.getUrl().getQuery() != null && sc.getUrl().getQuery().contains(AIFB_PARAM_YEAR)){
							currYear = extractParamValue(sc.getUrl().getQuery());
						}
						URL expURL = new URL(AIFB_HOST_NAME + AIFB_PUBL_DEPT_EXPORT_PATH + "?" 
								           + AIFB_PARAM_DEPT_ID + AIFB_DEPT_ID_COM + "&"
								           + AIFB_PARAM_YEAR + currYear);
						sc.setBibtexResult(sc.getContentAsString(expURL));
						return true;
					} catch (MalformedURLException me) {
						throw new InternalFailureException(me);
					}
				}
			}
			
		}		
		return false;
	}
	
	/**
	 * 
	 * @param param String representing a parameter including the value. E.g. year=2006 or id_db=2096
	 * @return extracted value
	 */
	private String extractParamValue(String param){
		return param.trim().substring(param.indexOf("=")+1);
	}


	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}


}
