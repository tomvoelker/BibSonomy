package org.bibsonomy.webapp.command;


import java.util.Collection;

import org.bibsonomy.scraper.Scraper;

/**
 * @author ema
 * @version $Id$
 */
public class ScraperInfoCommand extends ResourceViewCommand {
	private Collection<Scraper> scraperList;

	/**
	 * @return the scraperList
	 */
	public Collection<Scraper> getScraperList() {
		return this.scraperList;
	}

	/**
	 * @param scraperList the scraperList to set
	 */
	public void setScraperList(final Collection<Scraper> scraperList) {
		this.scraperList = scraperList;
	}

}
