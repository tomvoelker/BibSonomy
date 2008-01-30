/*
 * Created on 05.09.2006
 */
package scraper.url;

import scraper.CompositeScraper;
import scraper.url.kde.acm.ACMBasicScraper;
import scraper.url.kde.aip.AipScitationScraper;
import scraper.url.kde.amazon.AmazonScraper;
import scraper.url.kde.arxiv.ArxivScraper;
import scraper.url.kde.bibsonomy.BibSonomyScraper;
import scraper.url.kde.blackwell.BlackwellSynergyScraper;
import scraper.url.kde.citebase.CiteBaseScraper;
import scraper.url.kde.citeseer.CiteseerBasicScraper;
import scraper.url.kde.highwire.HighwireScraper;
import scraper.url.kde.ieee.IEEEComputerSocietyScraper;
import scraper.url.kde.ieee.IEEEXploreScraper;
import scraper.url.kde.ingenta.IngentaconnectScraper;
import scraper.url.kde.iop.IOPScraper;
import scraper.url.kde.karlsruhe.AIFBScraper;
import scraper.url.kde.karlsruhe.UBKAScraper;
import scraper.url.kde.l3s.L3SScraper;
import scraper.url.kde.librarything.LibrarythingScraper;
import scraper.url.kde.mathscinet.MathSciNetScraper;
import scraper.url.kde.multiple.ScrapingService;
import scraper.url.kde.nasa.ads.NasaAdsScraper;
import scraper.url.kde.nature.NatureScraper;
import scraper.url.kde.plos.PlosScraper;
import scraper.url.kde.prola.ProlaScraper;
import scraper.url.kde.pubmed.PubMedScraper;
import scraper.url.kde.pubmedcentral.PubMedCentralScraper;
import scraper.url.kde.science.ScienceDirectScraper;
import scraper.url.kde.spires.SpiresScraper;
import scraper.url.kde.springer.SpringerLinkScraper;
import scraper.url.kde.wiley.intersience.WileyIntersienceScraper;

public class URLCompositeScraper extends CompositeScraper {

	public URLCompositeScraper() {
		addScraper(new CiteBaseScraper());
		addScraper(new IEEEXploreScraper());
		addScraper(new SpringerLinkScraper());
		addScraper(new ScienceDirectScraper());
		addScraper(new PubMedScraper());
		addScraper(new PubMedCentralScraper());
		addScraper(new SpiresScraper());
		addScraper(new L3SScraper());
		addScraper(new ACMBasicScraper());
		addScraper(new CiteseerBasicScraper());
		addScraper(new AIFBScraper());
		addScraper(new UBKAScraper());
		addScraper(new ArxivScraper());
		addScraper(new IngentaconnectScraper());
		addScraper(new LibrarythingScraper());
		addScraper(new NasaAdsScraper());
		addScraper(new AipScitationScraper());
		addScraper(new MathSciNetScraper());
		addScraper(new WileyIntersienceScraper());
		addScraper(new IOPScraper());
		addScraper(new ProlaScraper());
		addScraper(new BibSonomyScraper());
		addScraper(new IEEEComputerSocietyScraper());
		addScraper(new AmazonScraper());
		addScraper(new PlosScraper());
		addScraper(new NatureScraper());
		addScraper(new BlackwellSynergyScraper());
		addScraper(new ScrapingService());
	}
}
