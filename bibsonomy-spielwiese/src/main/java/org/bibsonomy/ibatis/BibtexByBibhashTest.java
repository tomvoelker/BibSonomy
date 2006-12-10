package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibtexByHash;
import org.bibsonomy.model.BibTex;

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
		try {
			final BibtexByHash btn = this.getDefaultBibtexbyHash();

			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibtexByHash", btn);

			printBibTex(bibtexs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}