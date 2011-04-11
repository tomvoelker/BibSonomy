package org.bibsonomy.model;


/**
 * @author dzo
 * @version $Id$
 */
public interface Rateable {

	/**
	 * @return the rating
	 */
	public double getRating();
	
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating);
	
	/**
	 * @return the number of ratings
	 */
	public int getNumberOfRatings();
	
	/**
	 * @param numberOfRatings the number of ratings to set
	 */
	public void setNumberOfRatings(int numberOfRatings);
}
