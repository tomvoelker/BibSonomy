package helpers.database;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.TagConcept;


/**
 * Gets the tags for the users tag cloud from the database in sorted order.
 *
 */
public class DBUserTagsManager extends DBManager {

	private static final Log log = LogFactory.getLog(DBUserTagsManager.class);

	//private static final String SQL_SELECT_TAS_ALL          = "SELECT tag_name, count(tag_name) AS tag_count FROM tas t WHERE t.user_name = ? AND tag_count > ? GROUP BY t.tag_name ";
	private static final String SQL_SELECT_TAS_ALL = 
			"SELECT sub.tag_name, tag_count " +
			"FROM (SELECT tag_name, count(tag_name) " +
			"AS tag_count FROM tas t " +
			"WHERE t.user_name = ? " +
			"GROUP BY t.tag_name) " +
			"AS sub where tag_count >= ? ";
	private static final String SQL_SELECT_TAS_PUBLIC       = 
			"SELECT sub.tag_name, tag_count " +
			"FROM (SELECT tag_name, count(tag_name) " +
			"AS tag_count FROM tas t " +
			"WHERE t.user_name = ? " +
			"AND t.group = 0 " +
			"GROUP BY t.tag_name) " +
			"AS sub where tag_count >= ? ";
	private static final String SQL_SELECT_TAS_FREQ         = "ORDER BY tag_count DESC, sub.tag_name COLLATE utf8_unicode_ci"; 
	private static final String SQL_SELECT_TAS_ALPH         = "ORDER BY sub.tag_name COLLATE utf8_unicode_ci";
	/**
	 * Gets the tags for the users tag cloud in sorted order from the database. 
	 * Additionally marks supertags, which the user has choosen to be shown.
	 * 
	 * @param requUser        the user for which to get the tags 
	 * @param currUser TODO
	 * @param sortOrder   in which order the tags should be returned 
	 *   <ul>
	 *     <li><code>freq</code> - sorted by frequency
	 *     <li><code>alph</code> - sorted lexicographicaly
	 *   </ul> 
	 * @param withMarkedSupertags decided if supertags should be marked as supertags
	 * @return a sorted list of tags
	 * 
	 */
	/**
	 * @param requUser
	 * @param currUser
	 * @param sortOrder
	 * @param withMarkedSupertags
	 * @param minfreq
	 * @return
	 */
	/**
	 * @param requUser
	 * @param currUser
	 * @param sortOrder
	 * @param withMarkedSupertags
	 * @param minfreq
	 * @return
	 */
	public static SortedSet<TagConcept> getSortedTagsForUser (String requUser, String currUser, int sortOrder, boolean withMarkedSupertags, int minfreq){
		
		DBContext c = new DBContext();
				
		SortedSet<TagConcept> tags     = new TreeSet<TagConcept>();
		HashSet<String> supertags      = new HashSet<String>();
		HashSet<String> shownSupertags = new HashSet<String>();
				
		try {
			c.init();
			
			/*
			 * TODO: again an extra sausage for DBLP ...
			 */
			if ("dblp".equalsIgnoreCase(requUser)) {
				c.stmt = c.conn.prepareStatement("SELECT tag_ctr FROM tags WHERE tag_name = 'dblp'");
				c.rst  = c.stmt.executeQuery();
				if (c.rst.next()) {
					tags.add(new TagConcept("dblp", c.rst.getInt("tag_ctr"), false, true, sortOrder));	
				} else {
					tags.add(new TagConcept("dblp", 1, false, true, sortOrder));
				}
				return tags;
			} 
			 
			
			/*
			 * get supertags and shown supertags only, if needed
			 */
			if (withMarkedSupertags && requUser.equals(currUser)) {
				/*
				 * get all supertags of user and put them into a set
				 */
				c.stmt = c.conn.prepareStatement("SELECT upper FROM tagtagrelations WHERE user_name = ? GROUP BY upper");
				c.stmt.setString(1, requUser);
				c.rst = c.stmt.executeQuery();
				while(c.rst.next()) {
					supertags.add(c.rst.getString("upper"));
				}
				
				/*
				 * get the supertags which the user wants to be shown and put them into a set
				 */
				c.stmt = c.conn.prepareStatement(("SELECT upper FROM picked_concepts WHERE user_name = ?"));
				c.stmt.setString(1, requUser);
				c.rst = c.stmt.executeQuery();
				while(c.rst.next()){
					shownSupertags.add(c.rst.getString("upper"));
				}
			}
			
			/*
			 * choose, if public or all tags to be shown
			 */
			String select_first_part = null;
			
			if (requUser.equals(currUser)) {
				select_first_part = SQL_SELECT_TAS_ALL;
			} else {
				select_first_part = SQL_SELECT_TAS_PUBLIC;
			}
			
			/*
			 * get all tas of user in sorted order (as choosen by sortOrder) 
			 */
			if (sortOrder == 1) {
				// sorted by tag count
				c.stmt = c.conn.prepareStatement(select_first_part + SQL_SELECT_TAS_FREQ);
			} else {
				// sorted lexicographically
				c.stmt = c.conn.prepareStatement(select_first_part + SQL_SELECT_TAS_ALPH);
			}
			c.stmt.setString(1, requUser);
			c.stmt.setInt(2, minfreq);
			c.rst = c.stmt.executeQuery();
			
			/* 
			 * iterate over result set and get tags
			 */
			while(c.rst.next()) {
				String tag = c.rst.getString("tag_name");
				
				/*
				 * check, if tag is a supertag
				 * note that the tag is removed from the set of supertags, so that 
				 * it is not regarded in the following iteration which adds remaining
				 * supertags to the tag set
				 */
				boolean supertag = withMarkedSupertags && supertags.remove(tag);
				/*
				 * check, if tag is supertag AND shown
				 * note that the tag is removed from the set of shown supertags, so that
				 * it is not regarded in the following iteration which adds remaining 
				 * supertags to the tag set
				 */
				boolean shown   = supertag && shownSupertags.remove(tag);

				/*
				 * add tag as last element in list
				 */
				tags.add(new TagConcept(tag, c.rst.getInt("tag_count"), supertag, shown, sortOrder));
			}			
			/*
			 * iterate over supertags which are not used as tags
			 */
			for (String tag: supertags) {
				/*
				 * add the supertag to the tags with:
				 * - 0 as count (it is really not used, otherwise it would be added above
				 * - true for "supertag" (it is one)
				 */
				TagConcept tagConcept = new TagConcept(tag, 0, true, shownSupertags.contains(tag), sortOrder);
				tags.add(tagConcept);
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			log.fatal("could not get tags for user " + e.getMessage());
		} finally {
			c.close();
		}
		
		return tags;
	}	
}