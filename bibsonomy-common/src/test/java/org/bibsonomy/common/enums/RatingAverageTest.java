package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class RatingAverageTest {

	@Test
	public void testToString() {
		for (final RatingAverage ratingAverage : RatingAverage.values()) {
			assertEquals(ratingAverage.name().toLowerCase(), RatingAverage.ARITHMETIC_MEAN.toString());
		}
	}

}
