package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.UserParam;
import org.junit.Test;

/**
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceHelperTest {

	/**
	 * tests buildParam
	 */
	@Test
	public void buildParam() {
		GenericParam param = null;

		param = LogicInterfaceHelper.buildParam(BookmarkParam.class, "", null, "", null, "", null, 0, 10, null, null);
		assertEquals(BookmarkParam.class, param.getClass());

		param = LogicInterfaceHelper.buildParam(BibTexParam.class, "", null, "", null, "", null, 0, 10, null, null);
		assertEquals(BibTexParam.class, param.getClass());

		param = LogicInterfaceHelper.buildParam(TagParam.class, "", null, "", null, "", null, 0, 10, null, null);
		assertEquals(TagParam.class, param.getClass());

		param = LogicInterfaceHelper.buildParam(UserParam.class, "", null, "", null, "", null, 0, 10, null, null);
		assertEquals(UserParam.class, param.getClass());

		param = LogicInterfaceHelper.buildParam(GroupParam.class, "", null, "", null, "", null, 0, 10, null, null);
		assertEquals(GroupParam.class, param.getClass());
	}
}