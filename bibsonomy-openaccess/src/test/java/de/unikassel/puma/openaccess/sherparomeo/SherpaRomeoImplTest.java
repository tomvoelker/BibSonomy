package de.unikassel.puma.openaccess.sherparomeo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;


/**
 * @author rja
 * @version $Id$
 */
@Ignore
public class SherpaRomeoImplTest {

	@Test
	public void testGetPolicyForJournal() {
		final String start = "{\"publishers\":[{\"name\":\"Association for the Advancement of Artificial Intelligence\"";
		final SherpaRomeoImpl sr = new SherpaRomeoImpl();
		final String policy = sr.getPolicyForJournal("AI Magazine", null);
		
		assertEquals(start, policy.substring(0, start.length()));
	}
}
