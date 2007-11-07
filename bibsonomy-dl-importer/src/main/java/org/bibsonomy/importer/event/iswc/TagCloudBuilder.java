package org.bibsonomy.importer.event.iswc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;



public class TagCloudBuilder {

	private static final MyLogger log = new MyLogger();
	
	public static void main(String[] args) {
		
		final String[] tracks = new String[] {
				"in_use_1",
				"in_use_2",
				"in_use_3",
				"in_use_4",
				"research_01",
				"research_02",
				"research_03",
				"research_04",
				"research_05",
				"research_06",
				"research_07",
				"research_08",
				"research_09",
				"research_10",
				"research_11",
				"research_12",
				"research_13",
				"research_14",
				"research_15",
				"workshop_esoe",
				"workshop_fews",
				"workshop_first",
				"workshop_om",
				"workshop_peas",
				"doctoral_consortium"
			};
		final String requUser = "iswc2007";
		final String username = "rja";
		final String password = "IchSchonWieder";
		final String dbUrl = "jdbc:mysql://gandalf:6033/bibsonomy?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
		
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
					
					HashSet<String> occurences = new HashSet<String>(); 
					HashSet<String> absences   = new HashSet<String>();
					
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
							occurences.add(tag2);
							
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
						} else {
							absences.add(tag2);
						}
					}
					
					/*
					 * get counts for each tag
					 */
					stmtP = conn.prepareStatement("SELECT tag_name, COUNT(*) AS count FROM tas WHERE user_name = ? AND content_type = 2 GROUP BY tag_name");
					stmtP.setString(1, requUser);
					rst = stmtP.executeQuery();
					
					HashMap<String, Integer> counts = new HashMap<String, Integer>();
					
					while (rst.next()) {
						counts.put(rst.getString("tag_name"), rst.getInt("count"));
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
						
						int fontSize = counts.get(tag) + 100; 
						
						String href  = " href=\"/user/" + requUser + "/" + tag + "?items=50\""; 
						String title = " title=\"" + trackTag.globalCount + " posts\"";
						String clazz = " class=\"tag_" + trackTag.tag + "\"";
						String style = " style=\"font-size:" + fontSize + "%;\"";
						
						System.out.println("<a " + href + title + clazz + style + ">" + tag + "</a>&nbsp;");
					}
					
					
					absences.removeAll(occurences);
					
					if (absences.size() > 0) {
						System.err.println("ERROR: missing " + absences);
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
	
	private static class MyLogger {

		public void info(String e) {
			System.err.println(e);
		}

		
		public void fatal(String e) {
			System.err.println(e);
		}
		
		public void fatal(Exception e) {
			System.err.println(e);
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
