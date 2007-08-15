package helpers.database;


import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import tags.Functions;
import beans.SettingsBean;
import beans.UserBean;


public class DBUserManager extends DBManager {

	private final static String COL_TAGBOX_STYLE   = "tagbox_style";
	private final static String COL_TAGBOX_SORT    = "tagbox_sort";
	private final static String COL_TAGBOX_MINFREQ = "tagbox_minfreq";
	private final static String COL_TAGBOX_TOOLTIP = "tagbox_tooltip";
	private final static String COL_LIST_ITEMCOUNT = "list_itemcount";
	
	private final static Logger log = Logger.getLogger(DBUserManager.class); 
	
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
											+ "			birthday,gender,country,profession,interests,hobbies,profilegroup " 
											+ " 	FROM user WHERE user_name = ?");
				c.stmt.setString(1, bean.getUsername());
				c.rst = c.stmt.executeQuery();
				
				if (c.rst.next()) {
					// fill bean
					bean.setEmail(c.rst.getString("user_email"));
					bean.setHomepage(c.rst.getString("user_homepage"));
					bean.setRealname(c.rst.getString("user_realname"));
					bean.setOpenurl(c.rst.getString("openurl"));
					bean.setBirthday(c.rst.getDate("birthday"));
					bean.setGender(c.rst.getString("gender"));
					bean.setCountry(c.rst.getString("country"));
					bean.setProfession(c.rst.getString("profession"));
					bean.setInterests(c.rst.getString("interests"));
					bean.setHobbies(c.rst.getString("hobbies"));	
					bean.setProfileGroup(c.rst.getInt("profilegroup"));
				}
			}
		} catch (SQLException e) {
			log.fatal("Could not get settings for user " + bean.getUsername() + ": " + e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
 						                              COL_LIST_ITEMCOUNT + " = ?  " +
						                         "  WHERE user_name = ?");
				c.stmt.setInt(1, user.getTagboxStyle());
				c.stmt.setInt(2, user.getTagboxSort());
				c.stmt.setInt(3, user.getTagboxMinfreq());
				c.stmt.setInt(4, user.getTagboxTooltip());
				c.stmt.setInt(5, user.getItemcount());
				c.stmt.setString(6, user.getName());
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
											+ "		gender = ?, country = ?, profession = ?, interests = ?, hobbies = ? , profilegroup = ?"	
						 					+ "		WHERE user_name = ?");
				c.stmt.setString(1, bean.getEmail());
				c.stmt.setString(2, bean.getHomepage());
				c.stmt.setString(3, bean.getRealname());
				c.stmt.setString(4, bean.getOpenurl());
				c.stmt.setDate(5, (Date) bean.getBirthdayAsSQLDate());
				c.stmt.setString(6, bean.getGender());
				c.stmt.setString(7, bean.getCountry());
				c.stmt.setString(8, bean.getProfession());
				c.stmt.setString(9, bean.getInterests());
				c.stmt.setString(10, bean.getHobbies());
				c.stmt.setInt(11, bean.getProfileGroup());
				
				
				c.stmt.setString(12, bean.getUsername());
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			System.out.println("fatal");
			log.fatal("Could not set settings for user " + bean.getUsername() + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}

	public static boolean isValidCredential (String user, String key) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("SELECT user_password FROM user WHERE user_name = ?");
				c.stmt.setString(1, user);
				c.rst = c.stmt.executeQuery();
				return c.rst.next() && Functions.makeCredential(c.rst.getString("user_password")).equals(key);
			}
		} catch (SQLException e) {
			log.fatal("Could not check credentials for user " + user + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
}
