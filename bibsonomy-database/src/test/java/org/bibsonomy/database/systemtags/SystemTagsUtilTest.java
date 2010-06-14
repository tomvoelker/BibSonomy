package org.bibsonomy.database.systemtags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Tag;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * Tests SystemTagsUtil
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class SystemTagsUtilTest {
	
	
	/**
	 * Test all methods, that analyse tagNames, i. e.
	 * <ul>
	 * <li> extract type (e.g. "for", "send", "author"...)
	 * <li> extract argument (e.g. "xyz" from "send:xyz")
	 * </ul>
	 *
	 */
	@Test
	public void analyzeSystemTags() {
		// identify tagType
		assertEquals( "send", SystemTagsUtil.extractName("send") );
		assertEquals( "send", SystemTagsUtil.extractName("send:sdo") );
		assertEquals( "send", SystemTagsUtil.extractName("sys:send:sdo") );
		assertEquals( "send", SystemTagsUtil.extractName("system:send:sdo") );
		assertEquals( ":send:sdo", SystemTagsUtil.extractName(":send:sdo") );
	
		// identify tagArgument
		assertNull( SystemTagsUtil.extractArgument("send") );
		assertEquals( "sdo", SystemTagsUtil.extractArgument("send:sdo") );
		assertEquals( "sdo", SystemTagsUtil.extractArgument("sys:send:sdo") );
		assertEquals( "sdo", SystemTagsUtil.extractArgument("system:send:sdo") );
		assertEquals( "bar", SystemTagsUtil.extractArgument("foo:bar") );
	}

	
	@Test
	public void identifySystemTags() {
		// some searchSystemTags
		String[] tagNames = {"sys:author:sdo", "system:year:2010"};
		for (int i = 0; i<tagNames.length; i++) {
			assertTrue(SystemTagsUtil.isSearchSystemTag(tagNames[i]));
		}
		
		// some executable systemTags
		String[] executables = {"send:sdo", "sys:send:sdo", "system:send:sdo", "for:kde", "sys:for:kde", "system:for:kde"};
		for (int i = 0; i<executables.length; i++) {
			assertTrue(SystemTagsUtil.isExecutableSystemTag(executables[i]));
		}
		
		// some non-SystemTags
		String[] nonSystemTags = {"syst:author:sdo", "send:", "foo", "sys:foo:bar"};
		for (int i = 0; i<nonSystemTags.length; i++) {
			assertFalse(SystemTagsUtil.isSystemTag(nonSystemTags[i]));
		}
	}
	
	/**
	 * tests to remove or extract and count systemTags and non-systemTags
	 * removeAllSystemTags()
	 * removeAllNonSystemTags()
	 * extractNonSystemTags()
	 * countNonSystemTags()
	 */
	@Test
	public void removeAllSystemTags() {
		// tagNames: 3 systemTags and 5 nonSystemTags
		String[] tagNames = {"normalTag", "for:someGroup", "send:someUser", "anotherNormalTag", "sys:author:sdo", "sys:someSystemTagThing", "system:someOtherSystemTag:Thing", "yetOneMoreNormalTag"};
		// test countNonSystemTags()
		assertEquals(5, SystemTagsUtil.countNonSystemTags( Arrays.asList(tagNames) ));
		// test extractSystemTags()
		assertEquals(3, SystemTagsUtil.extractSystemTags(Arrays.asList(tagNames)).size());
		// test removeAllSystemTags()
		Set<Tag> tags = ModelUtils.getTagSet(tagNames);
		SystemTagsUtil.removeAllSystemTags(tags);
		assertEquals(5, tags.size());
		// test removeAllNonSystemTags()
		tags = ModelUtils.getTagSet(tagNames);
		ArrayList<String> tagNameList = new ArrayList<String>();
		for (int i=0; i<tagNames.length; i++) {
			tagNameList.add(tagNames[i]);
		}
		assertEquals(5, SystemTagsUtil.removeAllNonSystemTags(tagNameList));
	}

	
	/**
	 * tests extractSysetmTagsFromString
	 */
	@Test
	public void extractSystemTagsFromString() {
		// check for a given string
		StringBuilder test = new StringBuilder("This is a test string conaining sys:user:dbenz some system tags sys:days:10 .");
		List<String> sysTags = SystemTagsUtil.extractSystemTagsFromString(test.toString(), " ");
		assertEquals(2, sysTags.size());
		assertEquals("sys:user:dbenz", sysTags.get(0));
		assertEquals("sys:days:10", sysTags.get(1));
		
		// check all possible system tags
		test = new StringBuilder();
		for (SystemTags s : SystemTags.values()) {
			test.append(" ").append(s.getPrefix()).append(SystemTags.SYSTAG_DELIM).append("foo");
		}
		test.append(" ");
		sysTags = SystemTagsUtil.extractSystemTagsFromString(test.toString(), " ");
		assertEquals(SystemTags.values().length, sysTags.size());
	}

}
