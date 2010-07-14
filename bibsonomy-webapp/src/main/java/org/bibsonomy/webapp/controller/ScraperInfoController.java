package org.bibsonomy.webapp.controller;

import java.util.Collection;

import org.bibsonomy.scraper.KDEScraperFactory;
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
	 */
	private static final Collection<Scraper> scraperList = new KDEScraperFactory().getScraper().getScraper();
	
	@Override
	public View workOn(final ScraperInfoCommand command) {
		command.setScraperList(scraperList);
		return Views.SCRAPER_INFO;			
	}
	
	@Override
	public ScraperInfoCommand instantiateCommand() {
		return new ScraperInfoCommand();
	}

}
