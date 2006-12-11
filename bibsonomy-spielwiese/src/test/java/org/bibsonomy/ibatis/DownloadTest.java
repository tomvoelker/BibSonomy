package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.bibtex.BibTexByDownload;
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

	public BibTexByDownload getDefaultBibtexByDownload() {
		final BibTexByDownload bibVal = new BibTexByDownload();
		bibVal.setUser("grahl");
		return bibVal;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBibtexByDownload() {
		try {
			final BibTexByDownload btn = this.getDefaultBibtexByDownload();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByDownload", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}