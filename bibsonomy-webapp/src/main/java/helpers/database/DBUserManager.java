package helpers.database;


import java.sql.SQLException;
import java.text.ParseException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import beans.SettingsBean;
import beans.UserBean;


public class DBUserManager extends DBManager {

	private final static String COL_TAGBOX_STYLE   = "tagbox_style";
	private final static String COL_TAGBOX_SORT    = "tagbox_sort";
	private final static String COL_TAGBOX_MINFREQ = "tagbox_minfreq";
	private final static String COL_TAGBOX_TOOLTIP = "tagbox_tooltip";
	private final static String COL_LIST_ITEMCOUNT = "list_itemcount";
	private final static String COL_DEFAULT_LANG   = "lang";
	private final static String COL_API_KEY   	   = "api_key";
	private final static String LOG_LEVEL   	   = "log_level";
	
	private final static Log log = LogFactory.getLog(DBUserManager.class); 
	
	/**
	 * Gets the settings for a user and saves them in bean.
	 * 
	 * @param bean used for input (user name) and output (email, homepage, real name ...)
	 */
	public static void getSettingsForUser (SettingsBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement(" 	SELECT user_email,user_homepage,user_realname,openurl," 
											+ "			birthday,gender,hobbies,place,profession,interests,place,profilegroup,confirmDelete," + LOG_LEVEL + " " 
											+ " 	FROM user WHERE user_name = ?");
				c.stmt.setString(1, bean.getName());
				c.rst = c.stmt.executeQuery();
				
				if (c.rst.next()) {
					// fill bean
					bean.setEmail(c.rst.getString("user_email"));
					bean.setHomepage(c.rst.getString("user_homepage"));
					bean.setRealname(c.rst.getString("user_realname"));
					bean.setOpenurl(c.rst.getString("openurl"));
					bean.setBirthday(c.rst.getDate("birthday"));
					bean.setGender(c.rst.getString("gender"));
					bean.setPlace(c.rst.getString("place"));
					bean.setProfession(c.rst.getString("profession"));
					bean.setInterests(c.rst.getString("interests"));
					bean.setHobbies(c.rst.getString("hobbies"));	
					bean.setProfileGroup(c.rst.getInt("profilegroup"));
					bean.setLogLevel(c.rst.getInt(LOG_LEVEL));
					bean.setConfirmDelete(c.rst.getBoolean("confirmDelete") ? "true" : "false");
				}
				
				// get friends of user
				c.stmt = c.conn.prepareStatement("SELECT f_user_name FROM friends WHERE user_name = ? ");
				c.stmt.setString(1, bean.getName());
				c.rst = c.stmt.executeQuery();
				
				while(c.rst.next()) {					
					bean.addFriend(c.rst.getString("f_user_name"));
				}
				
//				// get groups the user is in
//				c.stmt = c.conn.prepareStatement("(SELECT i.group_name,i.group FROM groups g, groupids i WHERE g.user_name = ? AND g.group=i.group) " 
//				   + " UNION " 
//				   + " (SELECT i.group_name,i.group FROM groupids i WHERE i.group < 3 AND i.group >= 0) " 
//				   + " ORDER BY `group` ");
//				c.stmt.setString(1, bean.getName());
//
//				while(c.rst.next()) {					
//					bean.addGroup(c.rst.getString("group_name"));
//				}

				
			}
		} catch (SQLException e) {
			log.fatal("Could not get settings for user " + bean.getName() + ": " + e);
		} catch (ParseException e) {
			log.fatal("Could not get settings for user " + bean.getName() + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		
	}
	
	
	/** Checks, if user exists in database and returns information about the user.
	 *  
	 *  TODO: get friends of user
	 *  
	 * @param username - the name of the user.
	 * @param password - the MD5 hash of the password of the user.
	 * 
	 * @return A bean containing information about the user (like its real name, 
	 *         email, style settings, groups).
	 */
	public static UserBean getSettingsForUser (String username, String password) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				c.stmt = c.conn.prepareStatement("SELECT user_name,user_email,user_homepage,user_realname," +
						                         "       spammer,openurl," +
						                                 COL_TAGBOX_STYLE + "," + 
						                                 COL_TAGBOX_SORT + "," +
						                                 COL_TAGBOX_MINFREQ + "," +
						                                 COL_TAGBOX_TOOLTIP + "," +
						                                 COL_LIST_ITEMCOUNT + "," +
						                                 LOG_LEVEL + ",confirmDelete," +
						                         "       GROUP_CONCAT(group_name SEPARATOR ' ') AS groups" +
						                         "  FROM user " +
						                         "  LEFT JOIN groups USING (user_name) " +
						                         "  LEFT JOIN groupids USING (`group`) " +
						                         "  WHERE user.user_name = ? " +
						                         "    AND user.user_password = ?" +
						                         "  GROUP BY user_name");
				c.stmt.setString(1, username.toLowerCase());
				c.stmt.setString(2, password);
				c.rst = c.stmt.executeQuery();
				/*
				 * fill bean
				 */
				if (c.rst.next()) {
					UserBean user = new UserBean();
					/*
					 * set general user infos
					 */
					user.setName(c.rst.getString("user_name"));					
					user.setEmail(c.rst.getString("user_email"));
					user.setHomepage(c.rst.getString("user_homepage"));
					user.setRealname(c.rst.getString("user_realname"));
					user.setOpenurl(c.rst.getString("openurl"));
					/*
					 * set settings
					 */
					user.setTagboxStyle(c.rst.getInt(COL_TAGBOX_STYLE));
					user.setTagboxSort(c.rst.getInt(COL_TAGBOX_SORT));
					user.setTagboxMinfreq(c.rst.getInt(COL_TAGBOX_MINFREQ));
					user.setTagboxTooltip(c.rst.getInt(COL_TAGBOX_TOOLTIP));
					user.setItemcount(c.rst.getInt(COL_LIST_ITEMCOUNT));
					user.setLogLevel(c.rst.getInt(LOG_LEVEL));
					user.setConfirmDelete(c.rst.getBoolean("confirmDelete") ? "true" : "false");
					/*
					 * set groups
					 */
					String groupsString = c.rst.getString("groups");
					if (groupsString != null) {
						StringTokenizer groups = new StringTokenizer(groupsString);
						while (groups.hasMoreTokens()) {
							user.addGroup(groups.nextToken());
						}
					}
					
					/*
					 * set number of posts in basket
					 */
					c.stmt = c.conn.prepareStatement("SELECT count(user_name) AS count FROM collector WHERE user_name = ?");
					c.stmt.setString(1, username);
					c.rst = c.stmt.executeQuery();
					if (c.rst.next()) {
						user.setPostsInBasket(c.rst.getInt("count"));
					}
					
					return user;

				}
				
				
			}
		} catch (SQLException e) {
			log.fatal("Could not get data for user " + username + ": " + e);
		} finally {
			c.close();
		}
		return null;
	}

	/**
	 * Sets the settings for a user.
	 * 
	 * @param bean used for input of settings like tagboxStyle, tagboxSort, ...
	 * @return <code>true</code> if exactly one database row got updated
	 */
	public static boolean setSettingsForUser (UserBean user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				
				// prepare Statement
				c.stmt = c.conn.prepareStatement("UPDATE user " +
						                         "  SET " +
						                              COL_TAGBOX_STYLE   + " = ?, " +
						                              COL_TAGBOX_SORT    + " = ?, " +
						                              COL_TAGBOX_MINFREQ + " = ?, " +
						                              COL_TAGBOX_TOOLTIP + " = ?, " +
 						                              COL_LIST_ITEMCOUNT + " = ?,  " +
 						                              COL_DEFAULT_LANG + " = ?, " +
 						                              COL_API_KEY + " = ?, " +
 						                              LOG_LEVEL + " = ?," +
 						                              "confirmDelete = ? " +
						                         "  WHERE user_name = ?");
				c.stmt.setInt(1, user.getTagboxStyle());
				c.stmt.setInt(2, user.getTagboxSort());
				c.stmt.setInt(3, user.getTagboxMinfreq());
				c.stmt.setInt(4, user.getTagboxTooltip());
				c.stmt.setInt(5, user.getItemcount());
				c.stmt.setString(6, user.getDefaultLanguage());
				c.stmt.setString(7, user.getApiKey());
				c.stmt.setInt(8, user.getLogLevel());
				c.stmt.setBoolean(9, "true".equals(user.getConfirmDelete()));
				c.stmt.setString(10, user.getName());
				
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			log.fatal("Could not set settings for user " + user.getName() + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
	
	/**
	 * Sets the settings for a user.
	 * 
	 * @param bean used for input of user name, email, ...
	 * @return <code>true</code> if exactly one database row got updated
	 */
	public static boolean setSettingsForUser (SettingsBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init() && bean.isValidCkey()) { // initialize database
				
				// prepare Statement
				c.stmt = c.conn.prepareStatement("	UPDATE user SET user_email = ?, user_homepage = ?, user_realname = ?, openurl = ?, birthday = ?," 
											+ "		gender = ?, place = ?, profession = ?, interests = ?, hobbies = ? , profilegroup = ?, log_level = ?, confirmDelete = ? "	
						 					+ "		WHERE user_name = ?");
				c.stmt.setString(1, bean.getEmail());
				c.stmt.setString(2, bean.getHomepage());
				c.stmt.setString(3, bean.getRealname());
				c.stmt.setString(4, bean.getOpenurl());
				c.stmt.setDate(5, bean.getBirthdayAsDate());
				c.stmt.setString(6, bean.getGender());
				c.stmt.setString(7, bean.getPlace());
				c.stmt.setString(8, bean.getProfession());
				c.stmt.setString(9, bean.getInterests());
				c.stmt.setString(10, bean.getHobbies());
				c.stmt.setInt(11, bean.getProfileGroup());
				c.stmt.setInt(12, bean.getLogLevel());
				c.stmt.setBoolean(13, "true".equals(bean.getConfirmDelete()));
				
				c.stmt.setString(14, bean.getName());
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			log.fatal("Could not set settings for user " + bean.getName() + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}

	/*
	 * TODO: rja, 2007-09-04, uncommented because it is unused
	 */
//	public static boolean isValidCredential (String user, String key) {
//		DBContext c = new DBContext();
//		try {
//			if (c.init()) { // initialize database
//				// prepare Statement
//				c.stmt = c.conn.prepareStatement("SELECT user_password FROM user WHERE user_name = ?");
//				c.stmt.setString(1, user);
//				c.rst = c.stmt.executeQuery();
//				return c.rst.next() && Functions.makeCredential(c.rst.getString("user_password")).equals(key);
//			}
//		} catch (SQLException e) {
//			log.fatal("Could not check credentials for user " + user + ": " + e);
//		} finally {
//			c.close(); // close database connection
//		}
//		return false;
//	}
	

	
	/**	
	 * checks if userprofile of requsted user is visible for given user 
	 * @param currUser name of the user who wants to see the profile
	 * @param requUser name of the user whose profile is requested
	 * @return <code>true</code> if the user is allowed to see the others userprofile
	 * 		   <code>false</code> user is not allowed to see the requested profile 
	 */
	public static boolean isProfileViewable(String currUser, String requUser) {
		if (currUser.equals(requUser)) return true;
		DBContext c = new DBContext();
		
		try {
			if (c.init()) {
				c.stmt = c.conn.prepareStatement("SELECT profilegroup FROM user WHERE user_name = ?");
				c.stmt.setString(1, requUser);
				c.rst = c.stmt.executeQuery();
				if (c.rst.next()) {
					int group = c.rst.getInt("profilegroup");
					if (group == 0) return true; // requested profile is public visible
					if (group == 1) return false; // requested profile is private
				}
				
				/*
				 * check if requUser has currUser as friend
				 * (the other way around is not allowed, since currUser could declare everybody as
				 * his friend!)
				 */
				c.stmt = c.conn.prepareStatement("SELECT friends_id FROM friends WHERE user_name = ? AND f_user_name = ?");
				c.stmt.setString(1, requUser);
				c.stmt.setString(2, currUser);
				c.rst = c.stmt.executeQuery();
				// if a row is returned, they're friends
				return c.rst.next();
			}
		} catch (SQLException e) {
			log.fatal("Cannot check if " + currUser + " is friend of " + requUser);
		}
		return false;
	}
}