/*
 * Created on 16.12.2005
 */
package recommender.model;

/**
 * Interface für Ähnlichkeiten eines Tags mit der TagID von getRightTagID
 * zum Tag mit der TagID von getLeftTagId().
 * 
 * @author Jens Illig
 */
public interface TagSimilarity extends HalfTagSimilarity {
	public int getRightTagID();
}
