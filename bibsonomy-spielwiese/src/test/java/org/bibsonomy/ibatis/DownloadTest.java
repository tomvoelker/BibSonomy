package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibtexByDownload;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * TESTCLASS
 * 
 * initialize BibtexDownloadTest parameters
 * 
 * @author mgr
 * 
 */
public class DownloadTest extends AbstractSqlMapTest {

	public BibtexByDownload getDefaultBibtexByDownload() {
		final BibtexByDownload bibVal = new BibtexByDownload();
		bibVal.setUser("grahl");
		bibVal.setSimValue(ConstantID.SIM_HASH);
		return bibVal;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBibtexByDownload() {
		try {
			final BibtexByDownload btn = this.getDefaultBibtexByDownload();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByDownload", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}