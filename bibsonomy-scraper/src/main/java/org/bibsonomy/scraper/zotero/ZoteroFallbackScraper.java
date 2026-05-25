/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.scraper.zotero;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Runs Zotero first and falls back to the existing scraper chain during rollout.
 *
 * @author tvolker
 */
public class ZoteroFallbackScraper implements Scraper {

	private static final Log log = LogFactory.getLog(ZoteroFallbackScraper.class);

	private final ZoteroTranslationServerConfig config;
	private final ZoteroTranslationServerScraper zoteroScraper;
	private final Scraper legacyScraper;

	/**
	 * @param config runtime configuration
	 * @param legacyScraper legacy scraper chain
	 */
	public ZoteroFallbackScraper(final ZoteroTranslationServerConfig config, final Scraper legacyScraper) {
		this(config, new ZoteroTranslationServerScraper(config), legacyScraper);
	}

	/**
	 * @param config runtime configuration
	 * @param zoteroScraper Zotero scraper
	 * @param legacyScraper legacy scraper chain
	 */
	public ZoteroFallbackScraper(final ZoteroTranslationServerConfig config, final ZoteroTranslationServerScraper zoteroScraper, final Scraper legacyScraper) {
		this.config = config;
		this.zoteroScraper = zoteroScraper;
		this.legacyScraper = legacyScraper;
	}

	@Override
	public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
		boolean zoteroAttempted = false;
		ScrapingException zoteroException = null;

		if (this.config.isZoteroEnabled() && this.zoteroScraper.supportsScrapingContext(scrapingContext)) {
			zoteroAttempted = true;
			try {
				if (this.zoteroScraper.scrape(scrapingContext)) {
					log.info("Zotero translation-server scraper succeeded for " + scrapingContext.getUrl());
					return true;
				}
				log.info("Zotero translation-server scraper returned no result for " + scrapingContext.getUrl());
			} catch (final ScrapingException ex) {
				zoteroException = ex;
				log.warn("Zotero translation-server scraper failed for " + scrapingContext.getUrl(), ex);
			}
		}

		if (this.config.isLegacyFallbackEnabled() && this.legacyScraper.scrape(scrapingContext)) {
			if (zoteroAttempted) {
				log.info("Legacy scraper fallback succeeded after Zotero attempt for " + scrapingContext.getUrl());
			}
			return true;
		}

		if (zoteroException != null && !this.config.isLegacyFallbackEnabled()) {
			throw zoteroException;
		}

		return false;
	}

	@Override
	public String getInfo() {
		return "Zotero translation-server scraper with legacy scraper fallback.";
	}

	@Override
	public Collection<Scraper> getScraper() {
		final Collection<Scraper> scrapers = new LinkedList<Scraper>();
		scrapers.addAll(this.zoteroScraper.getScraper());
		scrapers.addAll(this.legacyScraper.getScraper());
		return scrapers;
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
		return (this.config.isZoteroEnabled() && this.zoteroScraper.supportsScrapingContext(scrapingContext))
						|| (this.config.isLegacyFallbackEnabled() && this.legacyScraper.supportsScrapingContext(scrapingContext));
	}
}
