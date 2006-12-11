package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibTexByHash;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * TESTCLASS
 * 
 * initialize BibTexbHash parameters
 * 
 * @author mgr
 * 
 */
public class ByBibHashTest extends AbstractSqlMapTest {

	public BibTexByHash getDefaultBibTexByHash() {
		final BibTexByHash bibVal = new BibTexByHash();
		bibVal.setRequBibtex("0000175071e6141a7d36835489f922ef");
		bibVal.setRequSim("1");
		bibVal.setItemCount(5);
		bibVal.setStartBib(0);
		bibVal.setSimValue(ConstantID.SIM_HASH);
		bibVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bibVal;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBibtexbyHash() {
		try {
			final BibTexByHash btn = this.getDefaultBibTexByHash();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByHash", btn);
			printBibTex(bibtexs);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}
}