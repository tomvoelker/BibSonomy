package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibtexByHash;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Tag;


/**
 * TESTCLASS
 * 
 * initialize BibTexbHash parameters 
 * 
 * @author mgr
 *
 */
public class BibtexByBibhashTest extends AbstractSqlMapTest {
	
	
	public BibtexByHash getDefaultBibtexbyHash() {
		final BibtexByHash bibVal = new BibtexByHash();
		bibVal.setRequBibtex("0000175071e6141a7d36835489f922ef");
		bibVal.setRequSim("1");
		bibVal.setItemCount(5);
		bibVal.setStartBib(0);
		bibVal.setSimValue(ConstantID.SIM_HASH);
		bibVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bibVal;
	}

	@SuppressWarnings("unchecked")
	public void testGetBibtexbyHash() {
			final BibtexByHash btn = this.getDefaultBibtexbyHash();

			List<BibTex> bibtexs=new LinkedList();
			try {
				bibtexs = this.sqlMap.queryForList("getBibtexByHash", btn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}

			for (final BibTex bibtex : bibtexs) {
				System.out.println("Address  : " + bibtex.getAddress());
				System.out.println("Annote   : " + bibtex.getAnnote());
				System.out.println("Author : " + bibtex.getAuthor());
				System.out.println("BibTexAbstract   : " +bibtex.getBibtexAbstract());
				System.out.println("BibTexKey       : " + bibtex.getBibtexKey());
				System.out.println("Bkey        : " + bibtex.getBkey());
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
				for (final Tag tag : bibtex.getBibtextags()) {
					System.out.print(tag.getName() + " ");
				}
				System.out.println("\n");
			}
		 
	}
}