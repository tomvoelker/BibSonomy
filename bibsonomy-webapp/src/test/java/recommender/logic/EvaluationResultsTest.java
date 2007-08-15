/*
 * Created on 01.03.2006
 */
package recommender.logic;

import junit.framework.TestCase;

public class EvaluationResultsTest extends TestCase {
	public void testIt() {
		EvaluationResults avgRes = new EvaluationResults();
		
		EvaluationResults resA = new EvaluationResults();
		resA.addPoint(0.95,0.01,1);
		resA.addPoint(0.80,0.21,15);
		resA.addPoint(0.11,0.63,500);
		resA.interpolate();
		assertEquals(0.95d, resA.getPrecision(0.0));
		assertEquals(1d, resA.getRequiredBufLength(0.0));
		assertEquals(0.8d, resA.getPrecision(0.1));
		assertEquals(15d, resA.getRequiredBufLength(0.1));
		assertEquals(0.8d, resA.getPrecision(0.21));
		assertEquals(15d, resA.getRequiredBufLength(0.21));
		assertEquals(0.11, resA.getPrecision(0.5));
		assertEquals(500d, resA.getRequiredBufLength(0.5));
		
		EvaluationResults resB = new EvaluationResults();
		resB.addPoint(0.95,0.1,12);
		resB.addPoint(0.80,0.31,15);
		resB.addPoint(0.11,0.53,500);
		resB.interpolate();
		
		avgRes.addToAvg(resA);
		avgRes.addToAvg(resB);
		assertEquals(0.95d, avgRes.getPrecision(0.0));
		assertEquals(6.5d, avgRes.getRequiredBufLength(0.0));
		assertEquals(0.875d, avgRes.getPrecision(0.1));
		assertEquals(13.5d, avgRes.getRequiredBufLength(0.1));
		
		System.out.println(resA);
		System.out.println(resB);
		System.out.println(avgRes);
	}
}
