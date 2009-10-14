package org.bibsonomy.webapp.controller;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static final Log log = LogFactory.getLog(PopularPageController.class);
	
	
	public View workOn(final ScraperInfoCommand command) {
		
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		KDEUrlCompositeScraper factory = new KDEUrlCompositeScraper();
		Collection<Scraper> scraperList = factory.getScraper();
		
		command.setScraperList(scraperList);
		
		
		this.endTiming();
		return Views.SCRAPER_INFO;			
	}
	
	public ScraperInfoCommand instantiateCommand() {
		return new ScraperInfoCommand();
	}

}
