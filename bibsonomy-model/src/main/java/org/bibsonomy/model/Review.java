package org.bibsonomy.model;

/**
 * @author dzo
 * @version $Id$
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
	 * the max text length of a review
	 */
	public static int MAX_TEXT_LENGTH = 140;
	
	/**
	 * rating from MIN to MAX_REVIEW_RATING (x.0 and x.5)
	 */
	private double rating;
	
	private String text;
	
	/**
	 * the user can decide if his username is published with the review
	 */
	private boolean anonymous;

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
	 * @return the anonymous
	 */
	public boolean isAnonymous() {
		return this.anonymous;
	}

	/**
	 * @param anonymous the anonymous to set
	 */
	public void setAnonymous(final boolean anonymous) {
		this.anonymous = anonymous;
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
