package org.bibsonomy.ibatis;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

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
		this.db.getBibTex().getBibTexByHash(this.bibtexParam);
	}

	@Test
	public void getBibTexByTagNames() {
		this.db.getBibTex().getBibTexByTagNames(this.bibtexParam);
	}

	@Test
	public void getBibTexByUserFriends() {
		this.db.getBibTex().getBibTexByUserFriends(this.bibtexParam);
	}

	@Test
	public void getBibTexByDownload() {
		this.bibtexParam.setUserName("grahl");
		this.db.getBibTex().getBibTexByDownload(this.bibtexParam);
	}

	@Test
	public void getHomePageBibTex() {
		this.db.getBibTex().getHomePageBibTex(this.bibtexParam);
	}

	@Test
	public void getBibTexPopular() {
		this.db.getBibTex().getBibTexPopular(this.bibtexParam);
	}

	@Test
	public void getBibTexSearch() {
		this.bibtexParam.setSearch("test");
		this.db.getBibTex().getBibTexSearch(this.bibtexParam);
		this.bibtexParam.setUserName(null);
		this.db.getBibTex().getBibTexSearch(this.bibtexParam);
	}

	@Test
	public void getBibTexSearchCount() throws SQLException {
		this.bibtexParam.setSearch("test");
		Integer count = -1;
		count = this.db.getBibTex().getBibTexSearchCount(this.bibtexParam);
		assertTrue(count >= 0);

		this.bibtexParam.setUserName(null);
		count = -1;
		count = this.db.getBibTex().getBibTexSearchCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexViewable() {
		this.db.getBibTex().getBibTexViewable(this.bibtexParam);
	}
}