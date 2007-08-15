/*
 * Created on 19.02.2006
 */
package recommender.model;

import org.apache.log4j.Logger;

public class SimpleCosinusTagSimilarity extends SimpleTagSimilarity implements CosinusTagSimilarity, CombinableTagSimilarity<SimpleCosinusTagSimilarity> {
	private static final Logger log = Logger.getLogger(SimpleCosinusTagSimilarity.class);
	private final long leftQuadSum;
	private final long rightQuadSum;
	private final long scalarProd;
	
	public SimpleCosinusTagSimilarity(int simTagId, int tagID, long leftQuadSum, long rightQuadSum, long scalarProd) {
		super(simTagId, tagID, calculate(leftQuadSum, rightQuadSum, scalarProd));
		this.leftQuadSum = leftQuadSum;
		this.rightQuadSum = rightQuadSum;
		this.scalarProd = scalarProd;
	}

	public long getLeftQuadSum() {
		return leftQuadSum;
	}
	public long getRightQuadSum() {
		return rightQuadSum;
	}
	public long getScalarProd() {
		return scalarProd;
	}
	
	public static double calculate(long quadSumA, long quadSumB, long scalarProd) {
		if (scalarProd == 0) {
			return 0d;
		}
		double divisor = Math.sqrt(quadSumA * quadSumB);
		if (divisor == 0d) {
			log.error("division durch 0");
			return 0d;
		}
		return ((double)scalarProd) / divisor; 
	}

	public double getCombinedSimilarity(SimpleCosinusTagSimilarity simB) {
		SimpleCosinusTagSimilarity simA = this;
		long scalarprod = simA.getScalarProd() + simB.getScalarProd();
		if (scalarprod == 0) {
			return 0d;
		}
		double aTmp = simA.getRightQuadSum();
		double bTmp = simB.getRightQuadSum();
		double existingVectorLength = Math.sqrt( aTmp + bTmp );
		aTmp = simA.getLeftQuadSum();
		bTmp = simB.getLeftQuadSum();
		double newVectorLength = Math.sqrt( aTmp + bTmp);
		double divisor = newVectorLength * existingVectorLength;
		
		if (divisor == 0d) {
			log.error("division durch 0");
			return 0d;
			//return Double.NaN;
		}
		return ((double)scalarprod) / divisor;
	}
}
