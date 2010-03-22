package tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.util.EnumUtils;
import org.junit.Test;

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
	
	public static Integer computeTagFontsize(final Integer tagFrequency, final Integer tagMaxFrequency, final String tagSizeMode) {
		// round(log(if(tag_anzahl>100, 100, tag_anzahl+6)/6))*60+40
		if ("home".equals(tagSizeMode)) {
			Double t = (tagFrequency > 100 ? 100.0 : tagFrequency.doubleValue() + 6);
			t /= 5;
			t = Math.log(t) * 100 + 30;
			
			return (t.intValue()<100) ? 100 : t.intValue();
		}
		return new Double(100 + (tagFrequency.doubleValue() / tagMaxFrequency * 200)).intValue();
	}
	
	@Test
	public void testComputeTagFontSize() throws Exception {
		final int[] tagFrequencies = new int[] {1416,1241,3035,1150,1548,1796,1069,4446,2024,2401,2119,1157,6325,1132,1714,1135,1666,1098,1332,2283,1009,2985,1119,1593,2041,1076,1642,1656,2380,1579,1146,976,5751,1808,1248,1367,1904,1790,2311,1369,3348,2203,1231,1107,2218,1672,2570,1057,1038,1268,2228,974,1115,1558,2056,1644,1183,2792,1672,1325,1301,2457,1904,2243,1038,6709,2125,1099,1586,2497,2919,1243,1666,1817,3177,1104,1970,2126,2894,1655,6444,1105,3661,2822,1135,2102,1298,5088,1141,1743,8333,1282,4952,5674,1500,2371,1062,1237,1914,1084};

		for (final int tagFrequency : tagFrequencies) {
			final int fontSize = Functions.computeTagFontsize(tagFrequency, 8333, "popular");
		}
		
	}
}
