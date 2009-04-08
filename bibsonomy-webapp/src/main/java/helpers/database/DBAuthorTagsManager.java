package helpers.database;

import helpers.constants;

import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;
import resources.SplittedAuthors;
import resources.SystemTags;
import resources.TagConcept;

/**
 * Gets the tags for the authors tag cloud from the database in sorted order.
 */
public class DBAuthorTagsManager extends DBManager{
	
	private static final Log log = LogFactory.getLog(DBAuthorTagsManager.class);
	
	private static final String SQL_SELECT_TAS_PUBLIC       = "	SELECT tag_name, count(tag_name) AS tag_count "
														+ "		FROM search_bibtex s, tas t, bibtex b " 
														+ "		WHERE MATCH(s.author) AGAINST (? IN BOOLEAN MODE) "
														+ " 	AND s.group = " + constants.SQL_CONST_GROUP_PUBLIC
														+ "		AND s.content_id = t.content_id "
														+ "		AND s.content_id = b.content_id ";
	private static final String SQL_GROUP_STMT		      	= " GROUP BY t.tag_name "; 	
	private static final String SQL_SELECT_TAS_FREQ         = "ORDER BY tag_count DESC, t.tag_name COLLATE utf8_unicode_ci"; 
	private static final String SQL_SELECT_TAS_ALPH         = "ORDER BY tag_name COLLATE utf8_unicode_ci";
	
	
	/**
	 * Gets the tags for the AUTHORS tag cloud in sorted order from the database. 
	 *
	 * @param requAuthor  the author for which to get the tags 
	 * @param sortOrder   in which order the tags should be returned 
	 *   <ul>
	 *     <li><code>freq</code> - sorted by frequency
	 *     <li><code>alph</code> - sorted lexicographicaly
	 *   </ul> 	
	 * @return a sorted list of tags
	 */
	public static SortedSet<TagConcept> getSortedTagsForAuthors(String requAuthor, int sortOrder) {
		
		log.fatal("DEPRECATED: getSortedTagsForAuthors(String requAuthor, int sortOrder) should never be called");
		
		DBContext c = new DBContext();		
		SortedSet<TagConcept> tags  = new TreeSet<TagConcept>();
		int argCtr = 1;
		try {		
			
			c.init();
			SystemTags systemTags = new SystemTags(requAuthor);
			
			// show only public tags
			String selectPart = SQL_SELECT_TAS_PUBLIC 
								+ systemTags.generateSqlQuery(SystemTags.BIBTEX_YEAR, "b") 
								+ systemTags.generateSqlQuery(SystemTags.USER_NAME, "s")
								+ systemTags.generateSqlQuery(SystemTags.GROUP_NAME, "s")
								+ SQL_GROUP_STMT;
			
			if (sortOrder == 1) 
				c.stmt = c.conn.prepareStatement(selectPart + SQL_SELECT_TAS_FREQ);
			else 
				c.stmt = c.conn.prepareStatement(selectPart + SQL_SELECT_TAS_ALPH);
						
			SplittedAuthors authors = new SplittedAuthors(requAuthor);
			String subquery = authors.getQuery();
			c.stmt.setString(argCtr++, subquery);	
			if (systemTags.isUsed(SystemTags.USER_NAME))
				c.stmt.setString(argCtr++, systemTags.getValue(SystemTags.USER_NAME));	
			if (systemTags.isUsed(SystemTags.GROUP_NAME))
				c.stmt.setString(argCtr++, systemTags.getValue(SystemTags.GROUP_NAME));
			
			c.rst = c.stmt.executeQuery();			
			
			while (c.rst.next()) {
				String 	tag 		= c.rst.getString("tag_name");
				int 	tag_count 	= c.rst.getInt("tag_count");
				
				tags.add(new TagConcept(tag,tag_count,false,false,sortOrder));
			}				
			
		} catch (SQLException e) {			
			log.fatal("could not get tags for author(s) " + e.getMessage());
		} finally {
			c.close();
		}				
		return tags;
	}
}
