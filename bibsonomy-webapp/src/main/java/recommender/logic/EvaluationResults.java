/*
 * Created on 01.03.2006
 */
package recommender.logic;

public class EvaluationResults {
	private static final int NUM_VALUES = 21;
	private final double[] requiredBufLength = new double[NUM_VALUES];
	private double highestRequiredBufLength = 0d;
	private final double[] precision = new double[NUM_VALUES];
	private final boolean[] valuesSet = new boolean[NUM_VALUES];
	//private final double[] maxPrecision = new double[NUM_VALUES];
	private int valueCount = 0;
	
	public String algorithmName;
	
	public String toString() {
		StringBuilder sb = new StringBuilder( (algorithmName != null) ? algorithmName : "?");
		final double div = (valueCount > 0) ? valueCount : 1d;
		sb.append(":\n\trecall level:\t");
		for (double lvl = 0d; lvl <= 1d; lvl += 0.05d) {
			sb.append('\t').append(lvl);
		}
		sb.append(":\n\trequired results:");
		int maxVal = 0;
		for (double val : requiredBufLength) {
			sb.append('\t').append(val / div);
		}
		sb.append(":\n\tprecision:\t");
		for (double val : precision) {
			sb.append('\t').append(val / div);
		}
		sb.append("\n\n");
		return sb.toString();
	}
	
	public void addPoint(double p, double r, int rank) {
		int recallLevel = (int) (r/0.05d); 
		if (precision[recallLevel] <= p) {
			precision[recallLevel] = p;
			valuesSet[recallLevel] = true;
			requiredBufLength[recallLevel] = rank;
			if (rank > highestRequiredBufLength) {
				highestRequiredBufLength = rank;
			}
		}
	}
	
	public double getPrecision(double recall) {
		int recallLevel = (int) (recall/0.05d);
		final double div = (valueCount > 0) ? valueCount : 1d;
		return precision[recallLevel]/div;
	}
	
	public double getRequiredBufLength(double recall) {
		int recallLevel = (int) (recall/0.05d);
		final double div = (valueCount > 0) ? valueCount : 1d;
		return requiredBufLength[recallLevel]/div;
	}
	
	public void interpolate() {
		double highestFollowingPrecision = 0d;
		double lowestFollowingRequiredBuffLength = highestRequiredBufLength;
		for (int i = NUM_VALUES-1; i >= 0 ; --i) {
			if (valuesSet[i] == true) {
				if (precision[i] > highestFollowingPrecision) {
					highestFollowingPrecision = precision[i];
				}
				if (requiredBufLength[i] < lowestFollowingRequiredBuffLength) {
					lowestFollowingRequiredBuffLength = requiredBufLength[i];
				}
			} else {
				requiredBufLength[i] = lowestFollowingRequiredBuffLength;
				precision[i] = highestFollowingPrecision;
			}
		}
	}
	
	public void addToAvg(EvaluationResults er) {
		for (int i = NUM_VALUES-1; i >= 0 ; --i) {
			requiredBufLength[i] += er.requiredBufLength[i];
			if (requiredBufLength[i] > highestRequiredBufLength) {
				highestRequiredBufLength = requiredBufLength[i];
			}
			precision[i] += er.precision[i];
			valuesSet[i] = true;
		}
		valueCount++;
	}
}