/*
 * Created on 20.01.2006
 */
package recommender.model;

public class SimpleTagSimilarity extends SimpleHalfTagSimilarity implements TagSimilarity {
	private int rightTagId;

	/**
	 * @param tagID			von welchem tag geht die ähnlichkeit aus
	 * @param simTagId		wohin ist es ähnlich
	 * @param similarity	die ähnlichkeit
	 */
	public SimpleTagSimilarity(int simTagId, int tagID, double similarity) {
		super(simTagId, similarity);
		this.rightTagId = tagID;
	}

	public SimpleTagSimilarity(TagSimilarity sim) {
		super(sim);
		this.rightTagId = sim.getRightTagID();
	}

	public int getRightTagID() {
		return rightTagId;
	}

	public void setRightTagID(int rightTagId) {
		this.rightTagId = rightTagId;
	}

	public String toString() {
		return rightTagId + " -> " + getLeftTagID() + " " + getSimilarity();
	}
	
}
