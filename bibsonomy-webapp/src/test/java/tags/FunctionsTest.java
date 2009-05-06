package tags;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.util.EnumUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dbenz
 * @version $Id$
 */
public class FunctionsTest {

	@Test
	public void toggleUserSimilarity() {
		UserRelation rel = UserRelation.getUserRelationById(0); // jaccard
		String nextRelString = Functions.toggleUserSimilarity(rel.name());
		UserRelation nextRel = EnumUtils.searchEnumByName(UserRelation.values(), nextRelString);
		assertNotNull(nextRel);
		assertEquals(1, nextRel.getId());
		rel = UserRelation.getUserRelationById(3);
		nextRelString = Functions.toggleUserSimilarity(rel.name());
		nextRel = EnumUtils.searchEnumByName(UserRelation.values(), nextRelString);
		assertNotNull(nextRel);
		assertEquals(0, nextRel.getId());
	}
}
