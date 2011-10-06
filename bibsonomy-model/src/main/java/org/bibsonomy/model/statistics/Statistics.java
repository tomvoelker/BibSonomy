package org.bibsonomy.model.statistics;

/**
 * Statistics about certain elements of the model, e.g., posts of a user.
 * 
 * @author rja
 * @version $Id$
 */
public class Statistics {

	private int count = 0;
	private int min = 0;
	private int max = 0;
	private double average = 0.0;

	public Statistics() {
	}

	public Statistics(int count) {
		super();
		this.count = count;
	}

	

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMin() {
		return this.min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return this.max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public double getAverage() {
		return this.average;
	}

	public void setAverage(double average) {
		this.average = average;
	}
	
}
