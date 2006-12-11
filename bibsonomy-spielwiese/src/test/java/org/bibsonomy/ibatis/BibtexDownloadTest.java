package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibtexByDownload;
import org.bibsonomy.model.BibTex;

/**
 * TESTCLASS
 * 
 * initialize BibtexDownloadTest parameters
 * 
 * @author mgr
 * 
 */
public class BibtexDownloadTest extends AbstractSqlMapTest {

	public BibtexByDownload getDefaultBibtexByDownload() {
		final BibtexByDownload bibVal = new BibtexByDownload();
		bibVal.setUser("grahl");
		bibVal.setSimValue(ConstantID.SIM_HASH);
		return bibVal;
	}

	@SuppressWarnings("unchecked")
	public void testBibtexByDownload() {
		try {
			final BibtexByDownload btn = this.getDefaultBibtexByDownload();

			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibtexByDownload", btn);

			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}