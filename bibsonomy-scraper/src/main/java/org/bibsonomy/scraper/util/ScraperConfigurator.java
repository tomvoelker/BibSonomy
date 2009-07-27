package org.bibsonomy.scraper.util;

import org.bibsonomy.scraper.url.kde.amazon.AmazonScraper;

/**
 * Configures (static) attributes of scrapers
 * 
 * Initially written to replace configuration of Amazon scraper to allow Spring
 * to configure the scraper instead of the scraper getting its configuration.
 * 
 *  TODO: When we use Spring to configure our scrapers this class will become
 *  obsolete. We can then just plug the scrapers together and configure them in 
 *  the deployment descriptor (using non-static attributes).
 *  
 * 
 * @author rja
 * @version $Id$
 */
public class ScraperConfigurator {

	public String getAmazonAccessKey() {
		return AmazonScraper.getAmazonAccessKey();
	}
	public void setAmazonAccessKey(String amazonAccessKey) {
		AmazonScraper.setAmazonAccessKey(amazonAccessKey);
	}
	public String getAmazonSecretKey() {
		return AmazonScraper.getAmazonSecretKey();
	}
	public void setAmazonSecretKey(String amazonSecretKey) {
		AmazonScraper.setAmazonSecretKey(amazonSecretKey);
	}
	
}
