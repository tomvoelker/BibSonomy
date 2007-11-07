package org.bibsonomy.importer.event.iswc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;



public class TagCloudBuilder {

	private static final Logger log = Logger.getLogger(TagCloudBuilder.class);
	
	public static void main(String[] args) {
		
		final String[] tracks = new String[]{"fca", "todo", "text"};
		final String requUser = "jaeschke";
		final String username = args[0];
		final String password = args[1];
		final String dbUrl = "jdbc:mysql://gromit:3306/bibsonomy_clean?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
		
		/*
		 * build hashset for tracks
		 */
		final HashSet<String> trackSet = new HashSet<String>();
		for (String track:tracks) {
			trackSet.add(track);
		}
		
		
		
			
		try {
			Connection conn = null;
			PreparedStatement stmtP = null;
			ResultSet rst = null;
			/*
			 * connect to DB
			 */
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			conn = DriverManager.getConnection (dbUrl, username, password);
			log.info("Database connection established");
			
			if (conn != null){
				try {
					/*
					 * query db
					 * layout: data, date, host
					 */
					stmtP = conn.prepareStatement("SELECT t1.tag_name AS tag1, t2.tag_name AS tag2, COUNT(*) AS abcount FROM tas t1 JOIN tas t2 USING (content_id) " +
							                      "  WHERE t1.user_name = ? " +
							                      "    AND t1.content_type = 2 GROUP BY tag1, tag2");
					stmtP.setString(1, requUser);
					rst = stmtP.executeQuery();
					
					
					Map<String, SortedSet<TagWithCount>> matrix = new TreeMap<String, SortedSet<TagWithCount>>();
					/*
					 * output result
					 */
					while (rst.next()) {
						
						String tag1 = rst.getString("tag1");
						String tag2 = rst.getString("tag2");
						int count = rst.getInt("abcount");
						
						if (trackSet.contains(tag1)) {
							/*
							 * tag2 co-occured with track!
							 */
							if (!matrix.containsKey(tag2)) {
								/*
								 * first occurence of tag2
								 */
								matrix.put(tag2, new TreeSet<TagWithCount>(new TagWithCountComparator()));
							}
							/*
							 * remember count
							 */
							matrix.get(tag2).add(new TagWithCount(tag1, count, 0));
						}
					}
					conn.close ();
					
					/*
					 * find track with most occurences for each tag
					 */
					for (String tag: matrix.keySet()) {
						/*
						 * get maximum
						 */
						TagWithCount trackTag = matrix.get(tag).first();
						
						int fontSize = trackTag.count + 100; 
						
						String href  = " href=\"/user/" + requUser + "/" + tag + "?items=50\""; 
						String title = " title=\"" + trackTag.globalCount + " posts\"";
						String clazz = " class=\"tag_" + trackTag.tag + "\"";
						String style = " style=\"font-size:" + fontSize + "%;\"";
						
						System.out.println("<a " + href + title + clazz + style + ">" + tag + "</a>&nbsp;");
					}
					
				} catch (SQLException e) {
					log.fatal(e);
				}
			}
			
		} catch (SQLException e) {
			log.fatal(e);
		} catch (InstantiationException e) {
			log.fatal(e);
		} catch (IllegalAccessException e) {
			log.fatal(e);
		} catch (ClassNotFoundException e) {
			log.fatal(e);
		} finally {
			
		}
		
		
	}
	
	private static class TagWithCountComparator implements Comparator<TagWithCount> {

		public int compare(TagWithCount o1, TagWithCount o2) {
			return o2.count - o1.count;
		}
		
	}
	
	private static class TagWithCount {
		public String tag;
		public int count;
		public int globalCount;
		
		public TagWithCount(String tag, int count, int globalCount) {
			super();
			this.tag = tag;
			this.count = count;
			this.globalCount = globalCount;
		}
		
	}
	

}
