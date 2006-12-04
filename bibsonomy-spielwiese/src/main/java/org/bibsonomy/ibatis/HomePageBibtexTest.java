package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.HomePageBibtex;
import org.bibsonomy.model.BibTex;


/**
 * TESTCLASS
 * 
 * initialize HomePageforBibtex parameters 
 * 
 * @author mgr
 *
 */



public class HomePageBibtexTest extends AbstractSqlMapTest {
	
	
	public HomePageBibtex getDefaultHomePageBibtex() {
		final HomePageBibtex bibVal = new HomePageBibtex();
		bibVal.setGroupType(ConstantID.GROUP_FRIENDS);
		bibVal.setSimValue(ConstantID.SIM_HASH);
		bibVal.setItemCount(15);
		bibVal.setStartBib(0);
		return bibVal;
	}

	public void testGetHomePageBibtexTest() {
		try {
			final HomePageBibtex btn = this.getDefaultHomePageBibtex();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getHomePageBibTex", btn);
			

			for (final BibTex bibtex : bibtexs) {
				System.out.println("Address  : " + bibtex.getAddress());
				System.out.println("Annote   : " + bibtex.getAnnote());
				System.out.println("Author : " + bibtex.getAuthor());
				System.out.println("BibTexAbstract   : " +bibtex.getBibtexAbstract());
				System.out.println("BibTexKey       : " + bibtex.getBibtexKey());
				System.out.println("BKey        : " + bibtex.getBKey());
				System.out.println("Booktitle    : " + bibtex.getBooktitle());
				System.out.println("Chapter   : " + bibtex.getChapter());
				System.out.println("Crossref    : " + bibtex.getCrossref());
				System.out.println("Day   : " + bibtex.getDay());
				System.out.println("Description    : " + bibtex.getDescription());
				System.out.println("Edition    : " + bibtex.getEdition());
				System.out.println("Editor    : " + bibtex.getEditor());
				System.out.println("Entrytype    : " + bibtex.getEntrytype());
				System.out.println("Group    : " + bibtex.getGroup());
				System.out.println("HowPublished    : " + bibtex.getHowPublished());
				System.out.println("Instution    : " + bibtex.getInstitution());
				System.out.println("Journal    : " + bibtex.getJournal());
				System.out.println("Misc    : " + bibtex.getMisc());
				System.out.println("Month    : " + bibtex.getMonth());
				System.out.println("Note    : " + bibtex.getNote());
				System.out.println("Number    : " + bibtex.getNumber());
				System.out.println("Organization    : " + bibtex.getOrganization());
				System.out.println("Pages    : " + bibtex.getPages());
				System.out.println("Publisher    : " + bibtex.getPublisher());
				System.out.println("School    : " + bibtex.getSchool());
				System.out.println("Series    : " + bibtex.getSeries());
				System.out.println("Tagname    : " + bibtex.getTagname());
				System.out.println("Title    : " + bibtex.getTitle());
				System.out.println("UserName    : " + bibtex.getUserName());
				System.out.println("Volume    : " + bibtex.getVolume());
				System.out.println("Year    : " + bibtex.getYear());
				System.out.println("Url    : " + bibtex.getUrl());
				
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}