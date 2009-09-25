package org.bibsonomy.importer.event.hypertext09;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;



public class TagCloudBuilder {

	private static final MyLogger log = new MyLogger();
	//private static final Logger log = Logger.getLogger(TagCloudBuilder.class);
	
	public static void main(String[] args) {
		
		final String[] tracks = new String[] {
				"poster",
				"demo",
				"shortPaper",
				"fullPaper",
				"workshop",
				"invitedTalkAbstract"
			};
		final String requUser = "ht09";
		final String username = "bibsonomy";
		final String password = "12_bib_kde";
		final String dbUrl = "jdbc:mysql://odie/bibsonomy?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
		
		/*
		 * build hashset for tracks
		 */
		final HashSet<String> trackSet = new HashSet<String>(Arrays.asList(tracks));
		
		
		
			
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
							matrix.get(tag2).add(new TagWithCount(tag1, count));
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
					
					Set<Integer> fontSizes = new TreeSet<Integer>();
					Set<Integer> globalCounts = new TreeSet<Integer>();
					
					/*
					 * find track with most occurences for each tag
					 */
					for (String tag: matrix.keySet()) {
						/*
						 * get maximum
						 */
						TagWithCount trackTag = matrix.get(tag).first();
						
						
						
						final Integer globalCount = counts.get(tag);
						int fontSize = new Double(Math.log(globalCount)).intValue() * (globalCount - 1) + 100; 
						fontSizes.add(fontSize);
						globalCounts.add(globalCount);
						
						String href  = " href=\"/user/" + requUser + "/" + tag + "?items=100\""; 
						String title = " title=\"" + globalCount + " posts\"";
						String clazz = " class=\"tag_" + trackTag.tag + "\"";
						String style = " style=\"font-size:" + fontSize + "%;\"";
						
						System.out.println("<a" + href + title + clazz + style + ">" + tag + "</a>&nbsp;");
					}
					
					
					absences.removeAll(occurences);
					
					if (absences.size() > 0) {
						System.err.println("ERROR: missing " + absences);
					}
					
					System.out.println(fontSizes);
					System.out.println(globalCounts);
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
		//	System.err.println(e);
		}
		
		public void fatal(String e) {
		//	System.err.println(e);
		}
		
		public void fatal(Exception e) {
		//	System.err.println(e);
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

		public TagWithCount(String tag, int count) {
			super();
			this.tag = tag;
			this.count = count;
		}
		
	}
	

}
