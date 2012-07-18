package org.bibsonomy.lucene.ranking;

public class FolkRankInfo {

	// TODO save hash of resource, too?
	
	private float weight; // the FolkRank
	private int dim; // 0 = tag, 1 = user, (2 = resource)
	private String item; // name of preferred tag/user
	
	public FolkRankInfo(float weight, int dim, String item) {
		this.weight = weight;
		this.dim = dim;
		this.item = item;
	}
	
	public String getItem() {
		return item;
	}
	
	public int getDim() {
		return dim;
	}
	
	public float getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return item + " " + dim + " " + weight;
	}
}
