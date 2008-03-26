package org.bibsonomy.common.enums;

/**
 * Defines different possibilities of 
 * classifiers of a user
 * 
 * @author Stefab Stützer
 * @version $Id$
 */
public enum Classifier {

	/** An automatic classifier algorithm */
	CLASSIFIER(0),
	
	/** An administrator */
	ADMIN(1);
	
	private int id;
	
	private Classifier(int id) {
		this.id = id;
	}
	
	public static Classifier getClassifier(int id) {
		switch(id) {
		case 0: 
			return CLASSIFIER;
		case 1:
			return ADMIN;
		}
		return null;
	}
}