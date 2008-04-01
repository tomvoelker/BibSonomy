package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ClassifierTest {

	/**
	 * tests getClassifier
	 */
	@Test
	public void getClassifier() {
		assertEquals(Classifier.CLASSIFIER, Classifier.getClassifier(0));
		assertEquals(Classifier.ADMIN, Classifier.getClassifier(1));

		for (final int id : new int[] { -12, 23, 42 }) {
			assertNull(Classifier.getClassifier(id));
		}
	}
}