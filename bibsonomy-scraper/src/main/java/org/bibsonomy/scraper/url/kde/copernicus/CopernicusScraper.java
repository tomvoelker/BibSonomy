package org.bibsonomy.scraper.url.kde.copernicus;

import org.bibsonomy.scraper.UrlCompositeScraper;

public class CopernicusScraper extends UrlCompositeScraper {

	public CopernicusScraper(){
		this.addScraper(new CopernicusArticleScraper());
		this.addScraper(new CopernicusPreprintScraper());
	}

}
