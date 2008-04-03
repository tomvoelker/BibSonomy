package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class ClassifierSettingTest {
	
	/**
	 * tests getStatus
	 */
	@Test
	public void getStatus() {
		assertEquals(ClassifierSettings.ALGORITHM, ClassifierSettings.getClassifierSettings("algorithm"));
		assertEquals(ClassifierSettings.MODE, ClassifierSettings.getClassifierSettings("mode"));
		assertEquals(ClassifierSettings.CLASSIFY_PERIOD, ClassifierSettings.getClassifierSettings("classify_period"));
		assertEquals(ClassifierSettings.TRAINING_PERIOD, ClassifierSettings.getClassifierSettings("training_period"));
		assertEquals(ClassifierSettings.PROBABILITY_LIMIT, ClassifierSettings.getClassifierSettings("probability_limit"));		
	}
}