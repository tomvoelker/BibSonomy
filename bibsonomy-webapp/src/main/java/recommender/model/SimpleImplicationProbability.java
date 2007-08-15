/*
 * Created on 19.02.2006
 */
package recommender.model;

import org.apache.log4j.Logger;

public class SimpleImplicationProbability extends SimpleTagSimilarity implements CombinableTagSimilarity<SimpleImplicationProbability> {
	private static final Logger log = Logger.getLogger(SimpleImplicationProbability.class);
	private final long leftQuadSum;
	private final long modifiedScalarProd;
	
	public SimpleImplicationProbability(int simTagId, int tagID, long leftQuadSum, long modifiedScalarProd) {
		this(simTagId, tagID, calculate(leftQuadSum, modifiedScalarProd), leftQuadSum, modifiedScalarProd);
	}

	public SimpleImplicationProbability(int simTagId, int tagID, double sim, long leftQuadSum, long modifiedScalarProd) {
		super(simTagId, tagID, sim);
		this.leftQuadSum = leftQuadSum;
		this.modifiedScalarProd = modifiedScalarProd;
	}
	
	public static double calculate(long quadSumA, long modifiedScalarProd) {
		if (modifiedScalarProd == 0) {
			return 0d;
		}
		if (quadSumA == 0l) {
			log.error("division durch 0");
			return 0d;
		}
		return  ((double)modifiedScalarProd) / ((double) quadSumA);
	}
	
	public long getLeftQuadSum() {
		return leftQuadSum;
	}
	public long getModifiedScalarProd() {
		return modifiedScalarProd;
	}

	public double getCombinedSimilarity(SimpleImplicationProbability simB) {
		long scalarprod = this.getModifiedScalarProd() + simB.getModifiedScalarProd();
		if (scalarprod == 0) {
			return 0d;
		}
		double divisor = this.getLeftQuadSum() + simB.getLeftQuadSum();
		
		if (divisor == 0d) {
			log.error("division durch 0");
			return 0d;
			//return Double.NaN;
		}
		return ((double)scalarprod) / divisor;
	}
}
