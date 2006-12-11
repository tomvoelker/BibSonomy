package org.bibsonomy.ibatis;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibTexByTagNames;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.ibatis.params.generic.ByTagNames;

public class TestHelper {

	/**
	 * The ByTagNames-bean is used at various locations and has been refactored
	 * to this method.
	 */
	public static void setDefaultsOnByTagNamesBean(final ByTagNames btn) {
		btn.setGroupType(ConstantID.GROUP_PUBLIC);
		btn.setLimit(5);
		btn.setOffset(0);
		// btn.setCaseSensitive(true);
		// btn.addTagName("web");
		// btn.addTagName("online");
		btn.addTagName("community");
	}

	public static BookmarkByTagNames getDefaultBookmarkByTagNames() {
		final BookmarkByTagNames rVal = new BookmarkByTagNames();
		setDefaultsOnByTagNamesBean(rVal);
		return rVal;
	}

	public static BibTexByTagNames getDefaultBibTexByTagNames() {
		final BibTexByTagNames rVal = new BibTexByTagNames();
		setDefaultsOnByTagNamesBean(rVal);
		return rVal;
	}
}