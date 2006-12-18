package org.bibsonomy.ibatis;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.ibatis.params.GenericParam;

public class TestHelper {

	/**
	 * The ByTagNames-bean is used at various locations and has been refactored
	 * to this method.
	 */
	public static void setDefaults(final GenericParam param) {
		param.setGroupType(ConstantID.GROUP_PUBLIC);
		param.setLimit(10);
		param.setOffset(0);
		// btn.setCaseSensitive(true);
		// btn.addTagName("web");
		// btn.addTagName("online");
		param.addTagName("community");
	}

	public static BookmarkParam getDefaultBookmarkParam() {
		final BookmarkParam rVal = new BookmarkParam();
		setDefaults(rVal);
		rVal.setUserName("stumme");
		rVal.setFriendUserName("hotho");
		return rVal;
	}

	public static BibTexParam getDefaultBibTexParam() {
		final BibTexParam rVal = new BibTexParam();
		setDefaults(rVal);
		rVal.setRequBibtex("0000175071e6141a7d36835489f922ef");
		rVal.setUserName("hotho");
		rVal.setFriendUserName("stumme");
		return rVal;
	}
}