package org.bibsonomy.testutil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;

/**
 * Provides methods to build parameter-objects.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public final class ParamUtils {

	/** username of a user who doesn't exist */
	public static final String NOUSER_NAME = "this-user-doesnt-exist";
	/** groupname of a group who doesn't exist */
	public static final String NOGROUP_NAME = "this-group-doesnt-exist";
	/** testgroup1 */
	public static final int TESTGROUP1 = 3;
	/** testgroup2 */
	public static final int TESTGROUP2 = 4;
	/** testgroup3 */
	public static final int TESTGROUP3 = 5;
	/** Holds the date of the epoch which is 1970-01-01 00:00:00 */
	private static final Date date;
	/** example.com */
	public static URL EXAMPLE_URL;

	static {
		final Calendar cal = Calendar.getInstance();
		cal.clear();
		date = cal.getTime();

		try {
			EXAMPLE_URL = new URL("http://example.com");
		} catch (MalformedURLException ignore) {
		}
	}

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private ParamUtils() {
	}

	/**
	 * The defaults for every parameter-object are set here.
	 */
	private static void setDefaults(final GenericParam param) {
		param.setLimit(10);
		param.setOffset(0);
		param.setGroupId(3);
		param.setUserName("hotho");
		param.setRequestedUserName("stumme");
		param.setRequestedGroupName("kde");
		param.setDate(date);
		// param.setCaseSensitiveTagNames(true);
		param.addTagName("community");
		param.addGroup(GroupID.PUBLIC.getId());
	}

	/**
	 * Retrieves a GenericParam.
	 * @return GenericParam
	 */
	public static GenericParam getDefaultGeneralParam() {
		final GenericParam rVal = getDefaultBookmarkParam();
		setDefaults(rVal);
		rVal.setIdsType(ConstantID.IDS_CONTENT_ID);
		return rVal;
	}

	/**
	 * Retrieves a BookmarkParam.
	 * @return BookmarkParam
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
	 * @return BibTexParam
	 */
	public static BibTexParam getDefaultBibTexParam() {
		final BibTexParam rVal = new BibTexParam();
		setDefaults(rVal);
		rVal.setRequestedContentId(1924061);
		rVal.setHash("0000175071e6141a7d36835489f922ef"); // from user dblp
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
	 * @return UserParam
	 */
	public static UserParam getDefaultUserParam() {
		final UserParam param = new UserParam();
		setDefaults(param);
		final User user = ModelUtils.getUser();
		user.setName("testuser1");
		user.setRealname("Test User 1");
		user.setEmail("testuser1@bibsonomy.org");
		user.setGender("m");
		user.setOpenURL("http://sfxserv.rug.ac.be:8888/rug");
		user.setSettings(new UserSettings());
		param.setUser(user);
		return param;
	}

	/**
	 * Retrieve a TagParam
	 * @return TagParam
	 */
	public static TagParam getDefaultTagParam() {
		final TagParam param = new TagParam();
		setDefaults(param);
		param.setNewContentId(5218);
		param.setTasId(213758);
		param.setTags(new HashSet<Tag>());
		for (final String tagName : new String[] { ParamUtils.class.getName(), "hurz", "trallalla", "---_-" }) {
			param.getTags().add(new Tag(tagName));
		}
		param.setGroups(Arrays.asList(new Integer[] { 1, 5 }));
		param.setRegex("web");
		param.setCount(100);
		param.setTagName("Test");
		// nee to set the default resourcetype to all so bibtex and bookmark tags will be displayed
		param.setContentType(ConstantID.ALL_CONTENT_TYPE);
		return param; 
	}

	/**
	 * @return TagRelationParam
	 */
	public static TagRelationParam getDefaultTagRelationParam() {
		return new TagRelationParam();
	}

	/**
	 * Retrieve a GroupParam
	 * @return GroupParam
	 */
	public static GroupParam getDefaultGroupParam() {
		final GroupParam param = new GroupParam();
		setDefaults(param);
		return param;
	}

	/**
	 * @return StatisticsParam
	 */
	public static StatisticsParam getDefaultStatisticsParam() {
		final StatisticsParam param = new StatisticsParam();
		// FIXME: adapt to new test db
		param.setRequestedContentId(1924061);
		param.setHash("0000175071e6141a7d36835489f922ef"); // from user dblp
		return param;
	}
}