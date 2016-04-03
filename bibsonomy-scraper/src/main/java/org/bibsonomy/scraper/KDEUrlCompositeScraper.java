/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.scraper;

import org.bibsonomy.scraper.url.kde.Econstor.EconstorScraper;
import org.bibsonomy.scraper.url.kde.RWTH.RWTHAachenScraper;
import org.bibsonomy.scraper.url.kde.aaai.AAAIScraper;
import org.bibsonomy.scraper.url.kde.aanda.AandAScraper;
import org.bibsonomy.scraper.url.kde.acl.AclScraper;
import org.bibsonomy.scraper.url.kde.acm.ACMBasicScraper;
import org.bibsonomy.scraper.url.kde.acs.ACSScraper;
import org.bibsonomy.scraper.url.kde.aip.AipScitationScraper;
import org.bibsonomy.scraper.url.kde.amazon.AmazonScraper;
import org.bibsonomy.scraper.url.kde.ams.AmsScraper;
import org.bibsonomy.scraper.url.kde.annualreviews.AnnualreviewsScraper;
import org.bibsonomy.scraper.url.kde.anthrosource.AnthroSourceScraper;
import org.bibsonomy.scraper.url.kde.apa.APAScraper;
import org.bibsonomy.scraper.url.kde.apha.APHAScraper;
import org.bibsonomy.scraper.url.kde.aps.ApsScraper;
import org.bibsonomy.scraper.url.kde.arxiv.ArxivScraper;
import org.bibsonomy.scraper.url.kde.asm.AsmScraper;
import org.bibsonomy.scraper.url.kde.ats.ATSScraper;
import org.bibsonomy.scraper.url.kde.bibsonomy.BibSonomyScraper;
import org.bibsonomy.scraper.url.kde.biologists.BiologistsScraper;
import org.bibsonomy.scraper.url.kde.biomed.BioMedCentralScraper;
import org.bibsonomy.scraper.url.kde.blackwell.BlackwellSynergyScraper;
import org.bibsonomy.scraper.url.kde.bmj.BMJOpenScraper;
import org.bibsonomy.scraper.url.kde.bmj.BMJScraper;
import org.bibsonomy.scraper.url.kde.cambridge.CambridgeScraper;
import org.bibsonomy.scraper.url.kde.casesjournal.CasesJournalScraper;
import org.bibsonomy.scraper.url.kde.cell.CellScraper;
import org.bibsonomy.scraper.url.kde.citeseer.CiteseerxScraper;
import org.bibsonomy.scraper.url.kde.citeulike.CiteulikeScraper;
import org.bibsonomy.scraper.url.kde.cshlp.CSHLPScraper;
import org.bibsonomy.scraper.url.kde.dblp.DBLPScraper;
import org.bibsonomy.scraper.url.kde.degruyter.DeGruyterScraper;
import org.bibsonomy.scraper.url.kde.dlib.DLibScraper;
import org.bibsonomy.scraper.url.kde.editlib.EditLibScraper;
import org.bibsonomy.scraper.url.kde.elsevierhealth.ElsevierhealthScraper;
import org.bibsonomy.scraper.url.kde.eric.EricScraper;
import org.bibsonomy.scraper.url.kde.faseb.FASEBJournalScraper;
import org.bibsonomy.scraper.url.kde.firstmonday.FirstMondayScraper;
import org.bibsonomy.scraper.url.kde.genome.GenomeBiologyScraper;
import org.bibsonomy.scraper.url.kde.googlebooks.GoogleBooksScraper;
import org.bibsonomy.scraper.url.kde.googlepatent.GooglePatentScraper;
import org.bibsonomy.scraper.url.kde.googlescholar.GoogleScholarScraper;
import org.bibsonomy.scraper.url.kde.hematologylibrary.HematologyLibraryScraper;
import org.bibsonomy.scraper.url.kde.hindawi.HindawiScraper;
import org.bibsonomy.scraper.url.kde.ieee.IEEEComputerSocietyScraper;
import org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper;
import org.bibsonomy.scraper.url.kde.igiglobal.IGIGlobalScraper;
import org.bibsonomy.scraper.url.kde.informaworld.InformaWorldScraper;
import org.bibsonomy.scraper.url.kde.ingenta.IngentaconnectScraper;
import org.bibsonomy.scraper.url.kde.inspire.InspireScraper;
import org.bibsonomy.scraper.url.kde.iop.IOPScraper;
import org.bibsonomy.scraper.url.kde.iucr.IucrScraper;
import org.bibsonomy.scraper.url.kde.iwap.IWAPonlineScraper;
import org.bibsonomy.scraper.url.kde.jap.JAPScraper;
import org.bibsonomy.scraper.url.kde.jcb.JCBScraper;
import org.bibsonomy.scraper.url.kde.jci.JCIScraper;
import org.bibsonomy.scraper.url.kde.jmlr.JMLRScraper;
import org.bibsonomy.scraper.url.kde.jneurosci.JNeurosciScraper;
import org.bibsonomy.scraper.url.kde.journalogy.JournalogyScraper;
import org.bibsonomy.scraper.url.kde.jstage.JStageScraper;
import org.bibsonomy.scraper.url.kde.jstor.JStorScraper;
import org.bibsonomy.scraper.url.kde.karlsruhe.AIFBScraper;
import org.bibsonomy.scraper.url.kde.karlsruhe.BibliographyScraper;
import org.bibsonomy.scraper.url.kde.karlsruhe.UBKAScraper;
import org.bibsonomy.scraper.url.kde.librarything.LibrarythingScraper;
import org.bibsonomy.scraper.url.kde.liebert.LiebertScraper;
import org.bibsonomy.scraper.url.kde.mathscinet.MathSciNetScraper;
import org.bibsonomy.scraper.url.kde.mdpi.MDPIScraper;
import org.bibsonomy.scraper.url.kde.mendeley.MendeleyScraper;
import org.bibsonomy.scraper.url.kde.metapress.MetapressScraper;
import org.bibsonomy.scraper.url.kde.morganclaypool.MorganClaypoolScraper;
import org.bibsonomy.scraper.url.kde.muse.ProjectmuseScraper;
import org.bibsonomy.scraper.url.kde.nasaads.NasaAdsScraper;
import org.bibsonomy.scraper.url.kde.nature.NatureScraper;
import org.bibsonomy.scraper.url.kde.nber.NberScraper;
import org.bibsonomy.scraper.url.kde.nejm.NEJMScraper;
import org.bibsonomy.scraper.url.kde.nowpublishers.NowPublishersScraper;
import org.bibsonomy.scraper.url.kde.opac.OpacScraper;
import org.bibsonomy.scraper.url.kde.openrepository.OpenrepositoryScraper;
import org.bibsonomy.scraper.url.kde.openuniversity.OpenUniversityScraper;
import org.bibsonomy.scraper.url.kde.osa.OSAScraper;
import org.bibsonomy.scraper.url.kde.oxfordjournals.OxfordJournalsScraper;
import org.bibsonomy.scraper.url.kde.phcogres.PharmacognosyResearchScraper;
import org.bibsonomy.scraper.url.kde.pion.PionScraper;
import org.bibsonomy.scraper.url.kde.plos.PlosScraper;
import org.bibsonomy.scraper.url.kde.pnas.PNASScraper;
import org.bibsonomy.scraper.url.kde.prola.ProlaScraper;
import org.bibsonomy.scraper.url.kde.psycontent.PsyContentScraper;
import org.bibsonomy.scraper.url.kde.pubmed.PubMedScraper;
import org.bibsonomy.scraper.url.kde.pubmedcentral.PubMedCentralScraper;
import org.bibsonomy.scraper.url.kde.rsc.RSCScraper;
import org.bibsonomy.scraper.url.kde.rsoc.RSOCScraper;
import org.bibsonomy.scraper.url.kde.sage.SageJournalScraper;
import org.bibsonomy.scraper.url.kde.scielo.SCIELOScraper;
import org.bibsonomy.scraper.url.kde.science.ScienceDirectScraper;
import org.bibsonomy.scraper.url.kde.sciencemag.ScienceMagScraper;
import org.bibsonomy.scraper.url.kde.scopus.ScopusScraper;
import org.bibsonomy.scraper.url.kde.spires.SpiresScraper;
import org.bibsonomy.scraper.url.kde.springer.SpringerLinkScraper;
import org.bibsonomy.scraper.url.kde.springer.SpringerScraper;
import org.bibsonomy.scraper.url.kde.ssrn.SSRNScraper;
import org.bibsonomy.scraper.url.kde.stanford.StanfordInfoLabScraper;
import org.bibsonomy.scraper.url.kde.taylorAndFrancis.TaylorAndFrancisScraper;
import org.bibsonomy.scraper.url.kde.thelancet.TheLancetScraper;
import org.bibsonomy.scraper.url.kde.usenix.UsenixScraper;
import org.bibsonomy.scraper.url.kde.wileyintersience.WileyIntersienceScraper;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.scraper.url.kde.worldscientific.WorldScientificScraper;
import org.bibsonomy.scraper.url.kde.wormbase.WormbaseScraper;

/**
 * Contains all active UrlScrapers.
 * 
 * @author rja
 * 
 */
public class KDEUrlCompositeScraper extends UrlCompositeScraper {

	/**
	 * Public constructor adding the active scrapers.
	 */
	public KDEUrlCompositeScraper() {
		addScraper(new OpacScraper());
		addScraper(new IEEEXploreScraper());
		addScraper(new SpringerLinkScraper());
		addScraper(new ScienceDirectScraper());
		addScraper(new PubMedScraper());
		addScraper(new PubMedCentralScraper());
		addScraper(new SpiresScraper());
		/*
		 * TODO: lha, 2013-04-29: new L3S web site layout not supported;
		 * publication lists on web site not in correct format, yet.
		 */
		//addScraper(new L3SScraper());
		addScraper(new ACMBasicScraper());
		addScraper(new AIFBScraper());
		addScraper(new UBKAScraper());
		addScraper(new ArxivScraper());
		addScraper(new IngentaconnectScraper());
		addScraper(new LibrarythingScraper());
		addScraper(new NasaAdsScraper());
		addScraper(new AipScitationScraper());
		addScraper(new HematologyLibraryScraper());
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
		addScraper(new InformaWorldScraper());
		addScraper(new CambridgeScraper());
		/*
		 * the scraper and the related information in the xml file are disabled because the website 
		 * http://www.isrl.uiuc.edu/ is not longer available
		 */
		//addScraper(new LangevScraper());
		addScraper(new LiebertScraper());
		addScraper(new NberScraper());
		addScraper(new UsenixScraper());
		addScraper(new IucrScraper());
		addScraper(new OSAScraper());
		addScraper(new PsyContentScraper());
		addScraper(new RSOCScraper());
		addScraper(new PNASScraper());
		addScraper(new ScienceMagScraper());
		addScraper(new JStorScraper());
		addScraper(new EricScraper());
		addScraper(new IWAPonlineScraper());
		addScraper(new JMLRScraper());
		addScraper(new AclScraper());
		addScraper(new JStageScraper());
		addScraper(new AnnualreviewsScraper());
		addScraper(new ProjectmuseScraper());
		addScraper(new SSRNScraper());
		addScraper(new ScopusScraper());
		addScraper(new MetapressScraper());
		addScraper(new CiteseerxScraper());
		addScraper(new OpenrepositoryScraper());
		addScraper(new PionScraper());
		addScraper(new CiteulikeScraper());
		addScraper(new AmsScraper());
		addScraper(new BibliographyScraper());
		addScraper(new WormbaseScraper());
		addScraper(new GoogleScholarScraper());
		addScraper(new GooglePatentScraper());
		addScraper(new SCIELOScraper());
		addScraper(new DLibScraper());
		/*
		 *Scientificcommons is reachable but shows default web page (apache)
		 *TODO: Enable Scraper if Scientificcommons works properly again
		 */
		//addScraper(new ScientificcommonsScraper());
		addScraper(new CellScraper());
		/*
		 * the scraper and the related information in the xml file are disabled because the version of the website 
		 * is session-based and hence, the scraping does not work.
		 */
		//addScraper(new WebOfKnowledgeScraper());
		addScraper(new CasesJournalScraper());
		addScraper(new ElsevierhealthScraper());
		addScraper(new AandAScraper());
		addScraper(new JournalogyScraper());
		addScraper(new InspireScraper());
		addScraper(new TaylorAndFrancisScraper());
		addScraper(new GoogleBooksScraper());
		addScraper(new JAPScraper());
		addScraper(new ATSScraper());
		addScraper(new NEJMScraper());
		addScraper(new SageJournalScraper());
		addScraper(new JCIScraper());
		addScraper(new StanfordInfoLabScraper());
		addScraper(new RSCScraper());
		addScraper(new FASEBJournalScraper());
		addScraper(new JNeurosciScraper());
		addScraper(new BiologistsScraper());
		addScraper(new OpenUniversityScraper());
		addScraper(new AAAIScraper());
		addScraper(new APAScraper());
		addScraper(new BMJOpenScraper());
		addScraper(new PharmacognosyResearchScraper());
		addScraper(new EconstorScraper());
		addScraper(new JCBScraper());
		addScraper(new ApsScraper());
		addScraper(new MendeleyScraper());
		addScraper(new HindawiScraper());
		addScraper(new AsmScraper());
		addScraper(new FirstMondayScraper());
		addScraper(new IGIGlobalScraper());
		addScraper(new DeGruyterScraper());
		addScraper(new MorganClaypoolScraper());
		addScraper(new MDPIScraper());
		addScraper(new OxfordJournalsScraper());
		addScraper(new TheLancetScraper());
		addScraper(new WorldScientificScraper());
		addScraper(new NowPublishersScraper());
		addScraper(new GenomeBiologyScraper());
		addScraper(new APHAScraper());
		addScraper(new CSHLPScraper());
		addScraper(new RWTHAachenScraper());
		
		/*
		 * it still under development
		 */
		//addScraper(new CopacScraper());
		//addScraper(new AkademiaiScraper()); error = 404
	}

}
