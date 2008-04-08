package helpers.database;

import helpers.ModifyGroupId;
import helpers.constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;

import beans.AdminBean;


/**
 * Provides methods only available for admins (like handling spammers, groups, api keys).
 * 
 * @author rja
 * @version $Id$
 */
public class DBAdminManager extends DBManager {
	
	private static final String[] spammerUpdateTables = {"bookmark", "bibtex", "tas", "search", "search_bibtex", "search_bookmark"};
	
	/*
	 * gets settings for this user and saves them in bean
	 */
	public static void addGroupToSystem (AdminBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// get next group id
				c.conn.setAutoCommit(false); // we do this in a transaction
				c.stmt = c.conn.prepareStatement("SELECT MAX(`group`) + 1 AS id FROM groupids");
				c.rst  = c.stmt.executeQuery();
				if (c.rst.next()) {
					int next_groupid = c.rst.getInt("id");
					// check, if user name exists
					c.stmt = c.conn.prepareStatement("SELECT user_name FROM user WHERE user_name = ?");
					c.stmt.setString(1, bean.getUser());
					c.rst  = c.stmt.executeQuery();
					if (c.rst.next()) {
						// user exists, check, if group exists
						c.stmt = c.conn.prepareStatement("SELECT group_name FROM groupids WHERE group_name = ?");
						c.stmt.setString(1, bean.getUser());
						c.rst  = c.stmt.executeQuery();
						if (c.rst.next()) {
							// group already exists --> do nothing
							bean.addError("This group already exists.");
						} else {
							// group does not exists --> add it
							c.stmt = c.conn.prepareStatement("INSERT INTO groupids (`group_name`, `group`, privlevel) VALUES (?,?,?)");
							c.stmt.setString(1, bean.getUser());
							c.stmt.setInt(2, next_groupid);
							c.stmt.setInt(3, bean.getPrivlevel());
							c.stmt.executeUpdate();
							c.stmt = c.conn.prepareStatement("INSERT INTO groups (`user_name`, `group`, `defaultgroup`) VALUES (?,?,?)");
							c.stmt.setString(1, bean.getUser());
							c.stmt.setInt(2, next_groupid);
							c.stmt.setInt(3, next_groupid);
							c.stmt.executeUpdate();
							bean.addInfo("Added group with id " + next_groupid + " to the system.");
						}
					} else {
						bean.addError("This user does not exist.");
					}
				}
				c.conn.commit(); // commit transaction
			}
		} catch (SQLException e) {
			try {
				c.conn.rollback();
			} catch (SQLException f) {
				/*
				 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
				 * Database backend classes
				 */
				final Log log = LogFactory.getLog(DBAdminManager.class);
				log.fatal("could not roll transaction back " + e.getMessage());
			}
			bean.addError("Sorry, an error occured: " + e);
		} finally {
			c.close(); // close database connection
		}

	}

	/**
	 * We flag a user as spammer and change group settings of 
	 * all his posts to make sure they do not show up.
	 *   
	 * @param bean
	 * @param spammer
	 */
	public static void flagSpammer (final AdminBean bean, final boolean spammer) {
		final DBContext c = new DBContext();
		try {
			if (c.init()) {//initialize database
				/*
				 * update user table
				 */
				if (spammer) {
					c.stmt = c.conn.prepareStatement("UPDATE user SET spammer = " + constants.SQL_CONST_SPAMMER_TRUE + ", updated_by = ?, updated_at = ?" +
							                         " WHERE user_name = ? AND spammer = " + constants.SQL_CONST_SPAMMER_FALSE);	
				} else {
					c.stmt = c.conn.prepareStatement("UPDATE user SET spammer = " + constants.SQL_CONST_SPAMMER_FALSE + ", updated_by = ?, updated_at = ?" +
							                         " WHERE user_name = ? AND spammer = " + constants.SQL_CONST_SPAMMER_TRUE);
				}
				/*
				 * update resource tables
				 */
				c.stmt.setString(1, bean.getCurrUser());
				c.stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
				c.stmt.setString(3, bean.getUser());
				if(c.stmt.executeUpdate() == 1) {
					/*
					 * user has been (un)flagged as spammer ... set info output and change his posts
					 */
					if (spammer) 
						bean.addInfo("user `" + bean.getUser() + "` flagged as spammer!"); 
					else 
						bean.addInfo("user `" + bean.getUser() + "` UNflagged as spammer!");  

					/*
					 * get old group ids and new group ids, depending upon if this user is a spammer or not 
					 */
					int newPriv = ModifyGroupId.getGroupId(constants.SQL_CONST_GROUP_PRIVATE, spammer);
					int newFrnd = ModifyGroupId.getGroupId(constants.SQL_CONST_GROUP_FRIENDS, spammer);
					int newPubl = ModifyGroupId.getGroupId(constants.SQL_CONST_GROUP_PUBLIC,  spammer);
					int oldPriv = ModifyGroupId.getGroupId(constants.SQL_CONST_GROUP_PRIVATE, !spammer);
					int oldFrnd = ModifyGroupId.getGroupId(constants.SQL_CONST_GROUP_FRIENDS, !spammer);
					int oldPubl = ModifyGroupId.getGroupId(constants.SQL_CONST_GROUP_PUBLIC,  !spammer);


					/*
					 * modify all groupids in tables
					 * 
					 * The updates are done with LOW_PRIORITY. This only affects MyISAM tables and helps 
					 * to prevent locking of SELECT statements.
					 * see also http://dev.mysql.com/doc/refman/5.1/en/table-locking.html
					 * 
					 * This may delay spammer flagging and therefore slow it down. However, this is really
					 * only relevant for the SpammerKickerButton and not for the admin page, since the 
					 * latter uses AJAX.   
					 */
					for (int i = 0; i < spammerUpdateTables.length; i++) {
						// private
						c.stmt = c.conn.prepareStatement("UPDATE LOW_PRIORITY " + spammerUpdateTables[i] + " SET `group` = "  + newPriv + " WHERE user_name = ? AND `group` = " + oldPriv);
						c.stmt.setString(1, bean.getUser());
						c.stmt.executeUpdate();
						// public
						c.stmt = c.conn.prepareStatement("UPDATE LOW_PRIORITY " + spammerUpdateTables[i] + " SET `group` = "  + newPubl + " WHERE user_name = ? AND `group` = " + oldPubl);
						c.stmt.setString(1, bean.getUser());
						c.stmt.executeUpdate();
						// friends
						c.stmt = c.conn.prepareStatement("UPDATE LOW_PRIORITY " + spammerUpdateTables[i] + " SET `group` = "  + newFrnd + " WHERE user_name = ? AND `group` = " + oldFrnd);
						c.stmt.setString(1, bean.getUser());
						c.stmt.executeUpdate();					
					}

				} else {
					if (spammer)
						bean.addError("user `" + bean.getUser() + " could not be flagged as spammer. Either he is already flagged or you misspelled the name.");
					else 
						bean.addError("user `" + bean.getUser() + " could not be UNflagged as spammer. Either he is already UNflagged or you misspelled the name.");
				}

			}
		}catch (final SQLException e) {
			bean.addError("Sorry, an error occured: " + e);
		} finally {
			c.close(); // close database connection
		}
	}

	/**
	 * add an user to the negative spammerlist. So he is marked NOT as a spammer and will not appear longer in any suggestion list
	 * @param bean the AdminBean reference
	 */
	public static void removeUserFromSpammerlist(AdminBean bean) {
		DBContext c = new DBContext();

		try {
			if (c.init()) {
				c.stmt = c.conn.prepareStatement("UPDATE user SET spammer_suggest = 0, updated_by = ?, updated_at = ? WHERE user_name = ?");
				c.stmt.setString(1, bean.getCurrUser());
				c.stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
				c.stmt.setString(3, bean.getUser());

				if(c.stmt.executeUpdate() == 1) {
					bean.addInfo("user '" + bean.getUser() + "' was removed from spammer suggestion list.");
				} else {
					bean.addError("user '" + bean.getUser() + "' could not be removed from the list. The user was not found.");
				}
			}
		} catch (SQLException e) {		
			bean.addError("Sorry, an error occured: " + e);
		}		
	}


	/**  
	 * add or remove a tagname on the 'blacklist' of tags used by spammers (
	 * or add a clean tag  to the list which won't be listed in the recommendation list in future
	 * 
	 * @param bean the AdminBean reference
	 * @param flag 
	 * 		<code>true</code>: flag tag as spammertag
	 * 		<code>false</code>: remove tag from spammertag list
	 * @param type
	 * 		if <code>0</code> tag is added to negative spammertag list so it is no longer in the suggestions lists
	 * 		
	 */
	public static void flagSpammerTag(AdminBean bean, boolean flag, int type) {
		DBContext c = new DBContext();

		try {
			if (c.init()) {

				// remove tag from list
				if (!flag) {				
					c.stmt = c.conn.prepareStatement("DELETE FROM spammer_tags WHERE tag_name = ?");				
					c.stmt.setString(1, bean.getTag());				
					if (c.stmt.executeUpdate() == 1) {					
						bean.addInfo("tag '" + bean.getTag() + "' removed from list.");
					} else {
						bean.addError("tag '" + bean.getTag() + "' could not be removed. It was not found in the list.");
					}				
				} else {  // add tag to list (1 = spammertag, 0 = clean tag from suggestion list)
					c.stmt = c.conn.prepareStatement("INSERT INTO spammer_tags (tag_name,spammer) VALUES (?,?)");
					c.stmt.setString(1, bean.getTag());
					c.stmt.setInt(2, type);
					if (c.stmt.executeUpdate() == 1) {
						if (type == 1) 
							bean.addInfo("tag '" + bean.getTag() + "' was added to the list.");
						else
							bean.addInfo("tag '" + bean.getTag() + "' was removed from recommendation list.");
					} else {
						if (type == 1)
							bean.addError("tag '" + bean.getTag() + "' is already in the list.");					
					}
				}		
			}
		} catch (SQLException e) {			
			bean.addError("Sorry, an error occured: " + e);
		}
	}


	public static void updateHighWireList(AdminBean bean) {
		DBContext c = new DBContext();

		try {
			if (c.init()) {
				// get the page content as string (method from ScrapingContext)
				ScrapingContext sc = new ScrapingContext(new URL("http://highwire.stanford.edu/lists/allsites.dtl"));
				StringBuffer _templist = new StringBuffer();

				//extract all link per regex
				Pattern p = Pattern.compile("(<A )CLASS=\"nolink\" TARGET=\"_top\" (HREF=\".*?\">)<FONT COLOR=\"#\\d+\" SIZE=\".*?\" FACE=\".*?\">(.+?</A>)");
				Matcher m = p.matcher(sc.getPageContent());

				//write every link into the buffer
				while (m.find()) {
					if (m.groupCount() == 3){
						_templist.append("<li>" + m.group(1) + m.group(2) + m.group(3) + "</li>");	
					}
				}

				c.stmt = c.conn.prepareStatement("UPDATE highwirelist SET list = ?, lastupdate=NOW() LIMIT 1");
				c.stmt.setString(1, _templist.toString());
				c.stmt.executeUpdate();
			}
		} catch (SQLException e) {			
			bean.addError("Sorry, an error occured: " + e);
		} catch (MalformedURLException e) {
			bean.addError("Sorry, an error occured: " + e);
		} catch (ScrapingException e) {
			bean.addError("Sorry, an error occured: " + e);
		}		
	}
	
	/**
	 * Generate an API key for the given user
	 * @param bean The AdminBean
	 * @author sts
	 */
	public static void updateApiKey(AdminBean bean) {
		DBContext c = new DBContext();
		String apiKey = UserUtils.generateApiKey();
		
		try {
			if (c.init()) {
				c.stmt = c.conn.prepareStatement("UPDATE user SET api_key = ? WHERE user_name = ?");
				c.stmt.setString(1, apiKey);
				c.stmt.setString(2, bean.getUser());
				if (c.stmt.executeUpdate() == 1)
					bean.addInfo("Generated api key '" + apiKey + "' for user " + bean.getUser());
			}
		} catch (SQLException ex) {
			bean.addError("Sorry, an error occured during api key generation: " + ex);
			ex.printStackTrace();
		}				
	}
}