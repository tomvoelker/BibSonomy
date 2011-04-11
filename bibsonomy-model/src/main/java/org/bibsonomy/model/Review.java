package org.bibsonomy.model;

import java.util.Date;

/**
 * @author dzo
 * @version $Id$
 */
public class Review {
	
	/**
	 * the max value of a review rating
	 */
	public static int MAX_REVIEW_RATING = 5;
	
	/**
	 * the min value of a review rating
	 */
	public static int MIN_REVIEW_RATING = 0;
	
	private int rating;
	
	private int helpful;

	private int notHelpful;
	
	private String text;
	
	private User user;
	
	private Date date;

	/**
	 * @return the rating
	 */
	public int getRating() {
		return this.rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(final int rating) {
		this.rating = rating;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(final Date date) {
		this.date = date;
	}
	
	/**
	 * @return the helpful
	 */
	public int getHelpful() {
		return this.helpful;
	}

	/**
	 * @param helpful the helpful to set
	 */
	public void setHelpful(int helpful) {
		this.helpful = helpful;
	}

	/**
	 * @return the notHelpful
	 */
	public int getNotHelpful() {
		return this.notHelpful;
	}

	/**
	 * @param notHelpful the notHelpful to set
	 */
	public void setNotHelpful(int notHelpful) {
		this.notHelpful = notHelpful;
	}
}
