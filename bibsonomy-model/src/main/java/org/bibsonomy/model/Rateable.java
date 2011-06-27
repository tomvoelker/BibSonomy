package org.bibsonomy.model;


/**
 * @author dzo
 * @version $Id$
 */
public interface Rateable {

	/**
	 * @return the rating
	 */
	public Double getRating();
	
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating);
	
	/**
	 * @return the number of ratings
	 */
	public Integer getNumberOfRatings();
	
	/**
	 * @param numberOfRatings the number of ratings to set
	 */
	public void setNumberOfRatings(int numberOfRatings);
}
