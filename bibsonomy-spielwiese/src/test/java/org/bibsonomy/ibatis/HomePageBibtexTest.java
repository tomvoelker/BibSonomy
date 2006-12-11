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

	@SuppressWarnings("unchecked")
	public void testGetHomePageBibtexTest() {
		try {
			final HomePageBibtex btn = this.getDefaultHomePageBibtex();

			final List<BibTex> bibtexs = this.sqlMap.queryForList("getHomePageBibtex", btn);

			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}