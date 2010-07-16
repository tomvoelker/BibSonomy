package org.bibsonomy.database.systemtags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.RelevantForSystemTag;
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

	// hasPrefixNameAndArgument
	assertTrue(SystemTagsUtil.hasPrefixNameAndArgument("sys:send:sdo"));
	assertTrue(SystemTagsUtil.hasPrefixNameAndArgument("system:author:sdo"));
	assertTrue(SystemTagsUtil.hasPrefixNameAndArgument("sys:foo:bar"));
	assertFalse(SystemTagsUtil.hasPrefixNameAndArgument("send:sdo"));
	assertFalse(SystemTagsUtil.hasPrefixNameAndArgument("sys:send"));
	assertFalse(SystemTagsUtil.hasPrefixNameAndArgument("for"));
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
	
	// a markup systemTag
	assertTrue(SystemTagsUtil.isMarkUpSystemTag("sys:relevantFor:sdo"));
	
	// some non-SystemTags
	String[] nonSystemTags = {"syst:author:sdo", "send:", "foo", "sys:foo:bar", "send", "for", "relevantfor:sdo"};
	for (int i = 0; i<nonSystemTags.length; i++) {
	    assertFalse(SystemTagsUtil.isSystemTag(nonSystemTags[i]));
	}
	
	// identify systemtags of given type
	assertTrue(SystemTagsUtil.isSystemTag("sys:relevantFor:sdo", RelevantForSystemTag.NAME));
	assertFalse(SystemTagsUtil.isSystemTag("relevantFor:sdo", RelevantForSystemTag.NAME));
	assertFalse(SystemTagsUtil.isSystemTag("foo", RelevantForSystemTag.NAME));
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
     * tests extractSystemTagsFromString
     */
    @Test
    public void extractSystemTagsFromString() {
	// check for a given string
	StringBuilder test = new StringBuilder("This is a test string conaining sys:user:dbenz some system tags sys:days:10 .");
	List<String> sysTags = SystemTagsUtil.extractSearchSystemTagsFromString(test.toString(), " ");
	assertEquals(2, sysTags.size());
	assertEquals("sys:user:dbenz", sysTags.get(0));
	assertEquals("sys:days:10", sysTags.get(1));
    }
    
    /**
     * tests creation methods of systemTags
     */
    @Test
    public void createSystemTags() {
	Tag tag = new Tag("send:sdo");
	assertNotNull(SystemTagsUtil.createExecutableTag(tag));
	tag.setName("sys:for:foo");
	assertNotNull(SystemTagsUtil.createExecutableTag(tag));
	assertNotNull(SystemTagsUtil.createSearchSystemTag("sys:author:sdo"));
	assertNotNull(SystemTagsUtil.createSearchSystemTag("system:user:sdo"));
	tag.setName("for");
	assertNull(SystemTagsUtil.createExecutableTag(tag));
	tag.setName("sys:send");
	assertNull(SystemTagsUtil.createExecutableTag(tag));
	assertNull(SystemTagsUtil.createSearchSystemTag("sys:author"));
	assertNull(SystemTagsUtil.createSearchSystemTag("user:sdo"));	
    }
}
