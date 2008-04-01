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
	ADMIN(1);

	private final int id;

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
	 * @param id
	 * @return an enum for the given id, null if there's no such enum
	 */
	public static Classifier getClassifier(final int id) {
		switch (id) {
		case 0:
			return CLASSIFIER;
		case 1:
			return ADMIN;
		default:
			return null;
		}
	}
}