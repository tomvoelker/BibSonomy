package org.bibsonomy.common.enums;

/**
 * Defines different possibilities of classifiers of a user
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public enum Classifier {
	/** An automatic classifier algorithm */
	CLASSIFIER(0),

	/** An administrator */
	ADMIN(1),
	
	/** Both */
	BOTH(2);
	
	/** the id */
	private int id;
	
	private Classifier(int id) {
		this.id = id;
	}

	/**
	 * @return the id for an enum
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @param id of the Classifier to retrieve
	 * @return the corresponding Classifier enum
	 */
	public static Classifier getClassifier(final int id) {
		switch(id) {
		case 0: 
			return CLASSIFIER;
		case 1:
			return ADMIN;
		case 2:
			return BOTH;
		default:
			return null;
		}
	}
}