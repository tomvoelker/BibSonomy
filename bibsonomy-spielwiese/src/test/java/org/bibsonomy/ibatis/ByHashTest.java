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
public class ByHashTest extends AbstractSqlMapTest {

	public static BibTexByHash getDefaultBibTexByHash() {
		final BibTexByHash bibVal = new BibTexByHash();
		bibVal.setRequBibtex("0000175071e6141a7d36835489f922ef");
		bibVal.setLimit(5);
		bibVal.setOffset(0);
		bibVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bibVal;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBibtexbyHash() {
		try {
			final BibTexByHash btn = getDefaultBibTexByHash();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByHash", btn);
			printBibTex(bibtexs);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}
}