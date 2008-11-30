package org.bibsonomy.database.systemtags;

import java.util.Arrays;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.systemstags.SystemTagFactory;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemtagsTest {

	@Before
	public void setUp() {
		SystemTagFactory.renewSystemTagMap("src/test/resources/systemtags/systemtags.xml");
	}

	@Test
	public void testAttribute() {
		String groupingTag = "sys:grouping";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, "testuser", GroupingEntity.USER, "testuser", Arrays.asList(new String[] { groupingTag }), "", null, 0, 50, null, null, new User());
		Assert.assertEquals(param.getGrouping(), GroupingEntity.USER);
	}

	@Test
	public void testFormat() {
		String systemtag = "sys:date:12:03:1983";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, "testuser", GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User());
		Assert.assertEquals(param.getGrouping(), GroupingEntity.FRIEND);
	}

	@Test
	public void testFormatFalse() {
		String systemtag = "sys:date:12:03:3";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, "testuser", GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User());
		Assert.assertEquals(param.getGrouping(), GroupingEntity.USER);
	}

	@Test
	public void testBibtexKey() {
		String systemtag = "sys:bibtexkey:123456";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, "testuser", GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User());
		Assert.assertEquals(param.getBibtexKey(), "123456");
	}

	@Test
	public void testDays() {
		String systemtag = "sys:days:13";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, "testuser", GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User());
		Assert.assertEquals(param.getDays(), 13);
	}
}