package org.bibsonomy.database.systemtags;

import java.util.List;

import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests SystemTagsUtil
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class SystemTagsUtilTest {
	
	
	/**
	 * tests extractSysetmTagsFromString
	 */
	@Test
	public void extractSystemTagsFromString() {
		// check for a given string
		String test = "This is a test string conaining sys:user:dbenz some system tags sys:days:10 .";
		List<String> sysTags = SystemTagsUtil.extractSystemTagsFromString(test, " ");
		assertEquals(2, sysTags.size());
		assertEquals("sys:user:dbenz", sysTags.get(0));
		assertEquals("sys:days:10", sysTags.get(1));
		
		// check all possible system tags
		test = "";
		for (SystemTags s : SystemTags.values()) {
			test += " " + s.getPrefix() + SystemTags.SYSTAG_DELIM + "foo";
		}
		test += " ";
		sysTags = SystemTagsUtil.extractSystemTagsFromString(test, " ");
		assertEquals(SystemTags.values().length, sysTags.size());
	}

}
