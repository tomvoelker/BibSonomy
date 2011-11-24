/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
