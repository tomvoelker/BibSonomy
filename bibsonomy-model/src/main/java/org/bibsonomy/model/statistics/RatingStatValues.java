package org.bibsonomy.model.statistics;

/**
 * @author stefani
 * @version $Id$
 */
public class RatingStatValues {

	/**
	 * Rating value (0, 0.5, ... 4.5, 5) 
	 */
	private float rating;
	
	/**
	 * amount of ratings with value "rating"
	 */
	private int count;

	/**
	 * relative amount of ratings with value "rating" with base 100
	 */
	private float percent;

	/**
	 * @return the rating
	 */
	public float getRating() {
		return this.rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(float rating) {
		this.rating = rating;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the percent
	 */
	public float getPercent() {
		return this.percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(float percent) {
		this.percent = percent;
	}


}
