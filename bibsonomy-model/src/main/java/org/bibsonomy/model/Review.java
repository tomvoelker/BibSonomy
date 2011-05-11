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
	public static double MAX_REVIEW_RATING = 5;
	
	/**
	 * the min value of a review rating
	 */
	public static double MIN_REVIEW_RATING = 0;
	
	private double rating;
	
	private int helpfulCount;

	private int notHelpfulCount;
	
	private String text;
	
	private User user;
	
	private Date date;
	
	private Date changeDate;

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
	 * @return the changeDate
	 */
	public Date getChangeDate() {
		return this.changeDate;
	}

	/**
	 * @param changeDate the changeDate to set
	 */
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	/**
	 * @return the helpfulCount
	 */
	public int getHelpfulCount() {
		return this.helpfulCount;
	}

	/**
	 * @param helpfulCount the helpfulCount to set
	 */
	public void setHelpfulCount(int helpfulCount) {
		this.helpfulCount = helpfulCount;
	}

	/**
	 * @return the notHelpfulCount
	 */
	public int getNotHelpfulCount() {
		return this.notHelpfulCount;
	}

	/**
	 * @param notHelpfulCount the notHelpfulCount to set
	 */
	public void setNotHelpfulCount(int notHelpfulCount) {
		this.notHelpfulCount = notHelpfulCount;
	}
}
