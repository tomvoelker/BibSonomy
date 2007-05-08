package org.bibsonomy.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Tag;

/**
 * Provides methods to build parameter-objects.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class ParamUtils {

	private static final Date date;

	static {
		final Calendar cal = Calendar.getInstance();
		cal.clear();
		date = cal.getTime();
	}

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
		param.setRequestedGroupName("kde");
		param.setDate(date);
		// param.setCaseSensitiveTagNames(true);
		param.addTagName("community");
	}

	/**
	 * Retrieves a GenericParam.
	 */
	public static GenericParam getDefaultGeneralParam() {
		final GenericParam rVal = getDefaultBookmarkParam();
		setDefaults(rVal);
		rVal.setIdsType(ConstantID.IDS_CONTENT_ID);
		return rVal;
	}

	/**
	 * Retrieves a BookmarkParam.
	 */
	public static BookmarkParam getDefaultBookmarkParam() {
		final BookmarkParam rVal = new BookmarkParam();
		setDefaults(rVal);
		rVal.setHash("0aea152798b8e95ce7a1bedb4ab8e7d7");
		rVal.setResource(ModelUtils.getBookmark());
		// rVal.setIdsType(ConstantID.IDS_CONTENT_ID);
		return rVal;
	}

	/**
	 * Retrieves a BibTexParam.
	 */
	public static BibTexParam getDefaultBibTexParam() {
		final BibTexParam rVal = new BibTexParam();
		setDefaults(rVal);
		rVal.setRequestedContentId(1924061);
		rVal.setHash("0000175071e6141a7d36835489f922ef");
		rVal.setResource(ModelUtils.getBibTex());
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

	/**
	 * Retrieve a UserParam.
	 */
	public static UserParam getDefaultUserParam() {
		final UserParam param = new UserParam();
		param.setUserName("grahl");
		param.setOffset(0);
		param.setLimit(5);
		param.setGroupId(3);
		param.setGroupType(ConstantID.GROUP_PUBLIC);
		param.setRequestedUserName("hotho");
		return param;
	}

	/**
	 * Retrieve a TagParam
	 */
	public static TagParam getDefaultTagParam() {
		final TagParam param = new TagParam();
		setDefaults(param);
		param.setNewContentId(5218);
		param.setTasId(213758);
		param.setTags(new ArrayList<Tag>());
		for (String s : new String[] { ParamUtils.class.getName(), "hurz", "trallalla", "---_-"}) {
			final Tag tag = new Tag();
			tag.setName(s);
			param.getTags().add(tag);
		}
		param.setGroups( Arrays.asList(new Integer[] { 1, 5 }) );
		param.setRegex("web");
		param.setCount(100);
		param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		param.setTagName("Test");
		return param;
	}

	/**
	 * Retrieve a GroupParam
	 */
	public static GroupParam getDefaultGroupParam() {
		final GroupParam param = new GroupParam();
		setDefaults(param);
		return param;
	}
}