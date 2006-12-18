package org.bibsonomy.ibatis;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.junit.Test;

/**
 * Tests related to BibTex.
 * 
 * @author mgr
 * @author Christian Schenk
 */
public class BibTexTest extends AbstractSqlMapTest {

	@Test
	public void getBibTexByHash() {
		bibtexTemplate("getBibTexByHash");
	}

	@Test
	public void getBibTexByTagNames() {
		bibtexTemplate("getBibTexByTagNames");
	}

	@Test
	public void getBibTexByUserFriends() {
		this.bibtexParam.setGroupType(ConstantID.GROUP_FRIENDS);
		bibtexTemplate("getBibTexByUserFriends");
	}

	@Test
	public void getBibTexByDownload() {
		this.bibtexParam.setUserName("grahl");
		bibtexTemplate("getBibTexByDownload");
	}

	@Test
	public void getHomePageBibTex() {
		this.bibtexParam.setGroupType(ConstantID.GROUP_FRIENDS);
		bibtexTemplate("getHomePageBibTex");
	}

	@Test
	public void getBibTexPopular() {
		bibtexTemplate("getBibTexPopular");
	}

	@Test
	public void getBibTexSearch() {
		this.bibtexParam.setSearch("test");
		bibtexTemplate("getBibTexSearch");
		this.bibtexParam.setUserName(null);
		bibtexTemplate("getBibTexSearch");
	}

	@Test
	public void getBibTexSearchCount() throws SQLException {
		this.bibtexParam.setSearch("test");
		Integer count = -1;
		count = (Integer) this.sqlMap.queryForObject("getBibTexSearchCount", this.bibtexParam);
		assertTrue(count >= 0);

		this.bibtexParam.setUserName(null);
		count = -1;
		count = (Integer) this.sqlMap.queryForObject("getBibTexSearchCount", this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexViewable() {
		bibtexTemplate("getBibTexViewable");
	}
}