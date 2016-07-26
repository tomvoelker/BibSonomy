/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.enums;

/**
 * Different algorithms for classification
 * 
 * @author Beate Krause
 */
public enum ClassifierAlgorithm {
	
	/** Rule learner */
	IBK ("weka.classifiers.lazy.IBk"),
	/** Good old naive bayes */
	NAIVE_BAYES ("weka.classifiers.bayes.NaiveBayes"),
	/** Classification tree */
	J48 ("weka.classifiers.trees.J48"),
	/** Rule learner */
	ONER ("weka.classifiers.rules.OneR"),
	/** SVM (Thorsten Joachims */
	LIBSVM ("weka.classifiers.functions.LibSVM"),
	/** SVM (Weka) */
	SMO ("weka.classifiers.functions.SMO"),
	/** Logistic Regression */
	LOGISTIC ("weka.classifiers.functions.Logistic");
	
	private String description;

	ClassifierAlgorithm(final String description) {
		this.description = description;
	}
	
	/**
	 * @return abbreviation of the specified mode
	 */
	public String getDescription() {
		return description;
	}
}