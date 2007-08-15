/*
 * Created on 19.02.2006
 */
package recommender.model;

import org.apache.log4j.Logger;

public class SimpleSymTunedImplicationProbability extends SimpleTagSimilarity implements CombinableTagSimilarity<SimpleSymTunedImplicationProbability> {
	private static final Logger log = Logger.getLogger(SimpleSymTunedImplicationProbability.class);
	private final long leftQuadSum;
	private final long rightQuadSum;
	private final long modifiedScalarProd;
	private final long inverseModifiedScalarProd;
	
	public SimpleSymTunedImplicationProbability(int simTagId, int tagID, double sim, long leftQuadSum, long rightQuadSum, long modifiedScalarProd, long inverseModifiedScalarProd) {
		super(simTagId, tagID, sim );
		this.leftQuadSum = leftQuadSum;
		this.rightQuadSum = rightQuadSum;
		this.modifiedScalarProd = modifiedScalarProd;
		this.inverseModifiedScalarProd = inverseModifiedScalarProd;
	}
	
	public SimpleSymTunedImplicationProbability(int simTagId, int tagID, long leftQuadSum, long rightQuadSum, long modifiedScalarProd, long inverseModifiedScalarProd) {
		this(simTagId, tagID, calculate( leftQuadSum, rightQuadSum, modifiedScalarProd, inverseModifiedScalarProd), leftQuadSum, rightQuadSum, modifiedScalarProd, inverseModifiedScalarProd );
	}

	public static double calculate(long quadSumA, long quadSumB, long modifiedScalarProd, long inverseModifiedScalarProd) {
		return SimpleImplicationProbability.calculate(quadSumA, modifiedScalarProd) * Math.sqrt( SimpleImplicationProbability.calculate(quadSumB, inverseModifiedScalarProd) );
	}
	
	public long getLeftQuadSum() {
		return leftQuadSum;
	}
	public long getModifiedScalarProd() {
		return modifiedScalarProd;
	}

	public double getCombinedSimilarity(SimpleSymTunedImplicationProbability simB) {
		long scalarprod = (this.getModifiedScalarProd() + simB.getModifiedScalarProd()) * (this.inverseModifiedScalarProd + simB.inverseModifiedScalarProd);
		if (scalarprod == 0) {
			return 0d;
		}
		double divisor = (this.getLeftQuadSum() + simB.getLeftQuadSum()) * (this.rightQuadSum + simB.rightQuadSum);
		
		if (divisor == 0d) {
			log.error("division durch 0");
			return 0d;
			//return Double.NaN;
		}
		return ((double)scalarprod) / divisor;
	}
}
