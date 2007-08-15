package helpers.database;

import helpers.database.DBManager.DBContext;

import java.sql.*;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import beans.RelationBean;

import resources.ExtendedTagRelation;
import resources.TagRelation;


/**
 * returns the relations for a user (for the corresponding bean)
 *
 */
public abstract class DBRelationGetManager {

	
	/**
	 * Gets all (shown) relations for the given user from the database.
	 * 
	 * @param user user whose relations to get
	 * @param shownSuperTags if <code>true</code> get shown relations, else get all relations of that user
	 * @return (shown) relations of the user
	 */
	public static LinkedList<TagRelation> getRelations(String user, boolean shownSuperTags) {
		LinkedList<TagRelation> relations = new LinkedList<TagRelation>();
		DBContext c = new DBContext();
		try {
			c.init(); // initialize database
			/*
			 * choose, which relations to get 
			 */
			if (shownSuperTags) {
				/*
				 * shown relations
				 */
				c.stmt = c.conn.prepareStatement("SELECT r.lower, r.upper FROM tagtagrelations r, picked_concepts p " +
						                         "  WHERE r.user_name=? AND r.user_name=p.user_name AND r.upper=p.upper ORDER BY r.upper COLLATE utf8_unicode_ci, lower COLLATE utf8_unicode_ci");
			} else {
				/*
				 * all relations
				 */
				c.stmt = c.conn.prepareStatement("SELECT lower, upper FROM tagtagrelations WHERE user_name=? ORDER BY upper COLLATE utf8_unicode_ci, lower COLLATE utf8_unicode_ci ");
			}
			c.stmt.setString(1, user);
			c.rst = c.stmt.executeQuery();
			
			while(c.rst.next()){
				relations.add(new TagRelation(c.rst.getString("lower"), c.rst.getString("upper")));
			}
			
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationGetManager.class);
			log.fatal("could not get relations for user : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return relations;
	}
	
	/** 
	 * Selects <code>limit</code> of users tag relations, starting at relation <code>start</code>. 
	 * @param user
	 * @param start
	 * @param limit
	 * @return
	 */
	public static void getRelations (RelationBean bean) {
		LinkedList<TagRelation>  relations = new LinkedList<TagRelation>();
		DBContext c = new DBContext();
		try {
			c.init();
			c.stmt = c.conn.prepareStatement("SELECT r.lower, r.upper" +
											 "  FROM tagtagrelations r, (SELECT upper FROM tagtagrelations WHERE user_name=? GROUP BY upper COLLATE utf8_unicode_ci ORDER BY upper COLLATE utf8_unicode_ci LIMIT ? OFFSET ?) AS tab " +
											 "  WHERE r.upper = tab.upper AND r.user_name=? " +
											 "  ORDER BY r.upper COLLATE utf8_unicode_ci, lower COLLATE utf8_unicode_ci");
			c.stmt.setString(1, bean.getRequUser());
			c.stmt.setInt(2, bean.getItems() + 1); // add one, so that we can throw one row away and see, if there are more rows to get
			c.stmt.setInt(3, bean.getStartRel());
			c.stmt.setString(4, bean.getRequUser());
			
			c.rst = c.stmt.executeQuery();
			
			int count = 0;
			String oldupper = "";
			
			while(c.rst.next()) {
				/* get supertag */
				String upper = c.rst.getString("upper");
				
				/* check, if supertag has changed */
				if (!oldupper.equals(upper)) {
					oldupper = upper;
					count ++;
				}
				
				/* check, how many supertags we have */
				if (count <= bean.getItems()) {
					/* build new relation */
					relations.add(new TagRelation(c.rst.getString("lower"), upper));
					
					/* if this is the last relation, remember this */
					if (c.rst.isLast()) {
						bean.setAllRelRows(true);
					}
				}
			}
			
			// Get number of upperconcepts
			c.stmt = c.conn.prepareStatement("SELECT count(distinct upper) AS count FROM tagtagrelations WHERE user_name = ?");
			c.stmt.setString(1, bean.getRequUser());
			c.rst = c.stmt.executeQuery();
			if (c.rst.next()) {
				bean.setTotal(c.rst.getInt("count"));
			}
			bean.setRelations(relations);
						
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationGetManager.class);
			log.fatal("could not get relations for user : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
	}
	
	
	
	/**
	 * Get top 50 relations. Sorted by number of users who use the supertag as a supertag.
	 * @return
	 */
	public static LinkedList<TagRelation> getPopularRelations () {
		LinkedList<TagRelation>  relations = new LinkedList<TagRelation>();
		
		DBContext c = new DBContext();
		try {
			c.init();
			c.stmt = c.conn.prepareStatement("SELECT r.upper, r.lower, tagCount.usercount FROM tagtagrelations r, " +
					                         "  (" +
					                         "    SELECT upper, count(distinct user_name) AS usercount, count(upper COLLATE utf8_unicode_ci) AS count " +
					                         "       FROM tagtagrelations r GROUP BY upper COLLATE utf8_unicode_ci ORDER BY usercount DESC,count DESC LIMIT 50" +
					                         "  ) AS tagCount " +
											 "  WHERE tagCount.upper=r.upper " +
											 "  GROUP BY tagCount.upper, r.lower" + 
 										     "  ORDER BY tagCount.upper COLLATE utf8_unicode_ci, r.lower COLLATE utf8_unicode_ci");
			c.rst = c.stmt.executeQuery();
						
			while (c.rst.next()) {
				relations.add(new ExtendedTagRelation(c.rst.getString("lower"), c.rst.getString("upper"), c.rst.getInt("usercount")));
			}
			
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationGetManager.class);
			log.fatal("could not get all (top 50) relations :" + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return relations;
	}
}
