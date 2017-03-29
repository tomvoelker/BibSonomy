/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

/**
 * @author dzo
 */
public class Review extends DiscussionItem {
	
	/**
	 * the max value of a review rating
	 */
	public static double MAX_REVIEW_RATING = 5;
	
	/**
	 * the min value of a review rating
	 */
	public static double MIN_REVIEW_RATING = 0;
	
	/**
	 * the max text length of a review  (approx. 10 pages lorem ipsum)
	 */
	public static int MAX_TEXT_LENGTH = 50000;
	
	/**
	 * rating from MIN to MAX_REVIEW_RATING (x.0 and x.5)
	 */
	private double rating;
	
	private String text;
	
	/**
	 * @return the rating
	 */
	public double getRating() {
		return this.rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(final double rating) {
		this.rating = rating;
	}
	
	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
}
