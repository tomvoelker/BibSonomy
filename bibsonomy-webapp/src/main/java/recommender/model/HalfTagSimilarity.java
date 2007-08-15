/*
 * Created on 16.12.2005
 */
package recommender.model;

/**
 * Interface für Ähnlichkeiten eines Tags zum Tag mit der TagID
 * von getLeftTagId().
 * 
 * @author Jens Illig
 */
public interface HalfTagSimilarity {
	public int getLeftTagID();
	public double getSimilarity();
}
