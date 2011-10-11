package org.bibsonomy.model.statistics;

/**
 * Statistics about certain elements of the model, e.g., posts of a user.
 * 
 * @author rja
 * @version $Id$
 */
public class Statistics {

	private int count = 0;
	private double min = 0.0;
	private double max = 0.0;
	private double rating = 0.0;
	private double percentage = 0.0;
	private double average = 0.0;

	/**
	 * constructor
	 */
	public Statistics() {
	}

	
	/**
	 * constructor. set count value while constructing object
	 * @param count
	 */
	public Statistics(int count) {
		super();
		this.count = count;
	}

	

	/**
	 * get count value
	 * @return count value
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * set count value
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * get minimum value
	 * @return minimum value
	 */
	public double getMin() {
		return this.min;
	}

	/**
	 * set minimum value
	 * @param min
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * get maximum value
	 * @return maximum value
	 */
	public double getMax() {
		return this.max;
	}

	/**
	 * set maximum value
	 * @param max
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}


	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}


	/**
	 * @param percentage the rating to set
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	
	/**
	 * @return the percentage
	 */
	public double getPercentage() {
		return percentage;
	}


	/**
	 * get average value
	 * @return average value
	 */
	public double getAverage() {
		return this.average;
	}

	
	/**
	 * set average value
	 * @param average
	 */
	public void setAverage(double average) {
		this.average = average;
	}
	
}
