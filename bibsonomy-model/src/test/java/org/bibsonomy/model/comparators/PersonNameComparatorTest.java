package org.bibsonomy.model.comparators;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class PersonNameComparatorTest {

	@Test
	public void testCompare() throws Exception {
		final PersonName p1 = new PersonName("Malcolm", "Reynolds");
		final PersonName p2 = new PersonName("Zoe Alleyne", "Washburne");
		final PersonName p3 = new PersonName("River", "Tam");
		final PersonName p4 = new PersonName("Hoban", "Washburne");
		
		final List<PersonName> all = Arrays.asList(p1, p2, p3, p4);
		Collections.sort(all, new PersonNameComparator());
		
		assertEquals(p1, all.get(0));
		assertEquals(p3, all.get(1));
		assertEquals(p4, all.get(2));
		assertEquals(p2, all.get(3));
	}

}
