package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibTexByUserFriends;
import org.bibsonomy.model.BibTex;

/**
 * TESTCLASS
 * 
 * initialize BibTexUserFriends parameters
 * 
 * @author mgr
 * 
 */
public class BibtexByUsersFriendTest extends AbstractSqlMapTest {

	public BibTexByUserFriends getDefaultBibtexbyUserFriends() {
		final BibTexByUserFriends bibVal = new BibTexByUserFriends();
		bibVal.setUser("hotho");
		bibVal.setItemCount(10);
		bibVal.setStartBib(0);
		bibVal.setGroupType(ConstantID.GROUP_FRIENDS);
		bibVal.setSimValue(ConstantID.SIM_HASH);
		return bibVal;
	}

	@SuppressWarnings("unchecked")
	public void testGetBibtexByUsersFriend() {
		try {
			final BibTexByUserFriends btn = this.getDefaultBibtexbyUserFriends();

			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByUserFriends", btn);

			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}