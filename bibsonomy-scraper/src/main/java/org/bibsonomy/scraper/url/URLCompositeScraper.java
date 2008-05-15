/*
 * Created on 05.09.2006
 */
package org.bibsonomy.scraper.url;

import org.bibsonomy.scraper.CompositeScraper;
import org.bibsonomy.scraper.url.kde.acm.ACMBasicScraper;
import org.bibsonomy.scraper.url.kde.acs.ACSScraper;
import org.bibsonomy.scraper.url.kde.aip.AipScitationScraper;
import org.bibsonomy.scraper.url.kde.amazon.AmazonScraper;
import org.bibsonomy.scraper.url.kde.anthrosource.AnthroSourceScraper;
import org.bibsonomy.scraper.url.kde.arxiv.ArxivScraper;
import org.bibsonomy.scraper.url.kde.bibsonomy.BibSonomyScraper;
import org.bibsonomy.scraper.url.kde.biomed.BioMedCentralScraper;
import org.bibsonomy.scraper.url.kde.blackwell.BlackwellSynergyScraper;
import org.bibsonomy.scraper.url.kde.bmj.BMJScraper;
import org.bibsonomy.scraper.url.kde.citebase.CiteBaseScraper;
import org.bibsonomy.scraper.url.kde.citeseer.CiteseerBasicScraper;
import org.bibsonomy.scraper.url.kde.dblp.DBLPScraper;
import org.bibsonomy.scraper.url.kde.editlib.EditLibScraper;
import org.bibsonomy.scraper.url.kde.ieee.IEEEComputerSocietyScraper;
import org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper;
import org.bibsonomy.scraper.url.kde.ingenta.IngentaconnectScraper;
import org.bibsonomy.scraper.url.kde.iop.IOPScraper;
import org.bibsonomy.scraper.url.kde.karlsruhe.AIFBScraper;
import org.bibsonomy.scraper.url.kde.karlsruhe.UBKAScraper;
import org.bibsonomy.scraper.url.kde.l3s.L3SScraper;
import org.bibsonomy.scraper.url.kde.librarything.LibrarythingScraper;
import org.bibsonomy.scraper.url.kde.mathscinet.MathSciNetScraper;
import org.bibsonomy.scraper.url.kde.nasa.ads.NasaAdsScraper;
import org.bibsonomy.scraper.url.kde.nature.NatureScraper;
import org.bibsonomy.scraper.url.kde.opac.OpacScraper;
import org.bibsonomy.scraper.url.kde.plos.PlosScraper;
import org.bibsonomy.scraper.url.kde.prola.ProlaScraper;
import org.bibsonomy.scraper.url.kde.pubmed.PubMedScraper;
import org.bibsonomy.scraper.url.kde.pubmedcentral.PubMedCentralScraper;
import org.bibsonomy.scraper.url.kde.science.ScienceDirectScraper;
import org.bibsonomy.scraper.url.kde.spires.SpiresScraper;
import org.bibsonomy.scraper.url.kde.springer.SpringerLinkScraper;
import org.bibsonomy.scraper.url.kde.springer.SpringerScraper;
import org.bibsonomy.scraper.url.kde.wiley.intersience.WileyIntersienceScraper;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;


public class URLCompositeScraper extends CompositeScraper {

	public URLCompositeScraper() {
		addScraper(new CiteBaseScraper());
		addScraper(new OpacScraper());
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
		addScraper(new DBLPScraper());
		addScraper(new BioMedCentralScraper());
		addScraper(new WorldCatScraper());
		addScraper(new SpringerScraper());
		addScraper(new ACSScraper());
		addScraper(new AnthroSourceScraper());
		addScraper(new BMJScraper());
		addScraper(new EditLibScraper());
	}
}
