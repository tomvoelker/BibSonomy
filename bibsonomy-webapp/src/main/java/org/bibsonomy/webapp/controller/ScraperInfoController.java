package org.bibsonomy.webapp.controller;

import java.util.Collection;

import org.bibsonomy.scraper.KDEUrlCompositeScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.webapp.command.ScraperInfoCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
/**
 * @author ema
 * @version $Id$
 */
public class ScraperInfoController extends MultiResourceListController implements MinimalisticController<ScraperInfoCommand>{

	/*
	 * TODO: inject the scraper list using Spring
	 * TODO: find a way to include the missing scrapers (ISBNScraper, DOIScraper, etc.)
	 */
	private static final Collection<Scraper> scraperList = new KDEUrlCompositeScraper().getScraper();
	
	
	public View workOn(final ScraperInfoCommand command) {
		command.setScraperList(scraperList);
		return Views.SCRAPER_INFO;			
	}
	
	public ScraperInfoCommand instantiateCommand() {
		return new ScraperInfoCommand();
	}

}
