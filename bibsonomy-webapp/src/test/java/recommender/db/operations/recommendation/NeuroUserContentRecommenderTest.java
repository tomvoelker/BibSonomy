/*
 * Created on 03.04.2006
 */
package recommender.db.operations.recommendation;

import junit.framework.TestCase;

public class NeuroUserContentRecommenderTest extends TestCase {
	public void testBuildTagCSVFromString() {
		String result = NeuroUserContentTagRecommendation.buildTagCSVFromString("www.domain.de - Produkte|Hardware|Krempel/Zeug: super,toll. Bitte kauf's!");
		System.out.println(result);
		assertEquals("'www.domain.de','produkte','Produkte','hardware','Hardware','krempel','Krempel','zeug','Zeug','super','toll','bitte','Bitte','kauf\\'s'", result);
		
		result = NeuroUserContentTagRecommendation.buildTagCSVFromString("Rock 'em, sock 'em Robocode: Round 2");
		System.out.println(result);
		assertEquals("'rock','Rock','\\'em','sock','\\'em','robocode','Robocode','round','Round','2'", result);
		
		result = NeuroUserContentTagRecommendation.buildTagCSVFromString("The Rise of ``Worse is Better''");
		System.out.println(result);
		assertEquals("'rise','Rise','``worse','``Worse','better\\'\\'','Better\\'\\''", result);

		result = NeuroUserContentTagRecommendation.buildTagCSVFromString("`` ` ' '' '");
		System.out.println(result);
		assertEquals("'``','`','\\'','\\'\\'','\\''", result);
	}
}
