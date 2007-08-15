/*
 * Created on 16.12.2005
 */
package recommender.model;

/**
 * Speichersparendes Etwas
 * 
 * @author Jens Illig
 */
public class SimpleHalfTagSimilarity implements HalfTagSimilarity {
	private final double similarity;
	private final int tagID;
	
	public SimpleHalfTagSimilarity(final int tagID, final double similarity) {
		this.tagID = tagID;
		this.similarity = similarity;
	}

	public SimpleHalfTagSimilarity(HalfTagSimilarity sim) {
		tagID = sim.getLeftTagID();
		similarity = sim.getSimilarity();
	}
	
	public int getLeftTagID() {
		return tagID;
	}

	public double getSimilarity() {
		return similarity;
	}
	
	public String toString() {
		return tagID + " -> " + similarity;
	}

}
