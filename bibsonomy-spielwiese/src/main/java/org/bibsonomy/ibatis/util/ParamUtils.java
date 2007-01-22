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
		param.setGroupId(3);
		param.setUserName("hotho");
		param.setRequestedUserName("stumme");
        // param.setCaseSensitiveTagNames(true);
		param.addTagName("community");
	}

	/**
	 * Retrieves a BookmarkParam.
	 */
	public static BookmarkParam getDefaultBookmarkParam() {
		final BookmarkParam rVal = new BookmarkParam();
		setDefaults(rVal);
		rVal.setHash("0aea152798b8e95ce7a1bedb4ab8e7d7");
		rVal.setBookmark(ModelUtils.getBookmark());
		//rVal.setIdsType(ConstantID.IDS_CONTENT_ID);
		return rVal;
	}

	/**
	 * Retrieves a BibTexParam.
	 */
	public static BibTexParam getDefaultBibTexParam() {
		final BibTexParam rVal = new BibTexParam();
		setDefaults(rVal);
		rVal.setHash("0000175071e6141a7d36835489f922ef");
		rVal.setBibtex(ModelUtils.getBibTex());
		return rVal;
	}

	/**
	 * Adds some common tags to the provided param. This comes in handy if
	 * you're running a query which iterates over the tags and if some
	 * conditions are met (i.e. more tags) the query will be build differently
	 * and you would like to check whether this query executes too.
	 * 
	 * @param param
	 *            The tags are added to this param.
	 */
	public static void addTagsToParam(final GenericParam param) {
		param.addTagName("web");
		param.addTagName("online");
	}
	
	
	
	
}