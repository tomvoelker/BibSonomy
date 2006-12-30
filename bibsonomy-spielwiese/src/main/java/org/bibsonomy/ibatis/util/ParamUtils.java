package org.bibsonomy.ibatis.util;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.ibatis.params.GenericParam;

/**
 * Provides methods to build parameter-objects.
 *
 * @author Christian Schenk
 */
public class ParamUtils {

	/**
	 * The defaults for every parameter-object are set here.
	 */
	private static void setDefaults(final GenericParam param) {
		param.setGroupType(ConstantID.GROUP_PUBLIC);
		param.setLimit(10);
		param.setOffset(0);
		// btn.setCaseSensitive(true);
		// btn.addTagName("web");
		// btn.addTagName("online");
		param.addTagName("community");
	}

	/**
	 * Retrieves a BookmarkParam.
	 */
	public static BookmarkParam getDefaultBookmarkParam() {
		final BookmarkParam rVal = new BookmarkParam();
		setDefaults(rVal);
		rVal.setUserName("stumme");
		rVal.setFriendUserName("hotho");
		return rVal;
	}

	/**
	 * Retrieves a BibTexParam.
	 */
	public static BibTexParam getDefaultBibTexParam() {
		final BibTexParam rVal = new BibTexParam();
		setDefaults(rVal);
		rVal.setRequBibtex("0000175071e6141a7d36835489f922ef");
		rVal.setUserName("hotho");
		rVal.setFriendUserName("stumme");
		return rVal;
	}
}