/*
 * Created on 20.03.2006
 */
package recommender.model;

public interface CombinableTagSimilarity<T extends CombinableTagSimilarity> extends TagSimilarity {
	/** kombiniert die Ähnlichkeiten zweier disjunkter Vektorräume */
	public double getCombinedSimilarity(T simB);
}
