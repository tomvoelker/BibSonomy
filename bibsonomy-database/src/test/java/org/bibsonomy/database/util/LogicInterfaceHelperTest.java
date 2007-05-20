package org.bibsonomy.database.util;

import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.TagParam;
import org.junit.Test;
import org.junit.Assert;

public class LogicInterfaceHelperTest {

	@Test
	public void buildParam() {
		GenericParam param = null;

		param = LogicInterfaceHelper.buildParam(BookmarkParam.class, "", null, "", null, "", null, 0, 10);
		Assert.assertEquals(BookmarkParam.class, param.getClass());

		param = LogicInterfaceHelper.buildParam(BibTexParam.class, "", null, "", null, "", null, 0, 10);
		Assert.assertEquals(BibTexParam.class, param.getClass());

		param = LogicInterfaceHelper.buildParam(TagParam.class, "", null, "", null, "", null, 0, 10);
		Assert.assertEquals(TagParam.class, param.getClass());
	}
}