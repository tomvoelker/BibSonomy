/*
 * Created on 19.02.2006
 */
package recommender.model;

public interface CosinusTagSimilarity extends TagSimilarity {
	public long getLeftQuadSum();
	public long getRightQuadSum();
	public long getScalarProd();
}
