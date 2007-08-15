/*
 * Created on 10.04.2006
 */
package recommender.model;

public interface TagSimilarityListener {
	public void accept(SimilarityCategory c, CombinableTagSimilarity sim);
	public void allDone();
}
