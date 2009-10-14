package org.bibsonomy.webapp.command;


import java.util.Collection;

import org.bibsonomy.scraper.Scraper;

/**
 * @author ema
 * @version $Id$
 */
public class ScraperInfoCommand extends ResourceViewCommand {
	Collection<Scraper> scraperList;

	public Collection<Scraper> getScraperList() {
		return this.scraperList;
	}

	public void setScraperList(Collection<Scraper> scraperList) {
		this.scraperList = scraperList;
	}

}
