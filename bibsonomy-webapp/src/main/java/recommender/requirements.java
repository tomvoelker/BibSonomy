/*
 * Created on 17.11.2005
 *
 */
package recommender;


public class requirements {
/*	public List getMostRelated(final String tag, final String userName) {
		return (List) new DatabaseAction() {
			private static final String SQL_SELECT_TAS = "SELECT t1.tag_name FROM tas WHERE content_id = ?";
			private static final String SQL_SELECT_TAS = "SELECT tag_name FROM tas WHERE content_id = ?";

			// mehrfachverwendete Tags: SELECT t1.tag_id FROM tas t1 GROUP BY tag_name HAVING count(t1.content_id)>1;
			// mehrfach getagter content:  select t1.content_id from tas t1 GROUP BY t1.content_id HAVING count(t1.tag_name)>1;
			// was mehrfach gleich getagt wurde: select t1.tag_name,t1.content_id,count(*) from tas t1 GROUP BY t1.content_id,t1.tag_name HAVING count(*)>1;
			// gleichVerwendungsanzahl zweier Tags: select t1.tag_name,t2.tag_name,count(t2.tag_id) from tas t1 join tas t2 on t1.content_id=t2.content_id where t1.tag_id<t2.tag_id AND t1.tag_name!=t2.tag_name group by t1.tag_name,t2.tag_name HAVING count(t2.tag_id)>10;
			// An wie vielen contents steht ein tag: select tag_name,count(content_id) as content_freq from tas group by tag_name;
			
			// Anzahl taggings mit gleichem tagnamen zwischen usern: select r1.user_name,t2.user_name,count(*) as ctr from (select tag_name,user_name from tas where user_name='hotho' group by tag_name order by count(tag_id) desc limit 10) r1 join tas t2 on r1.tag_name=t2.tag_name group by r1.user_name,t2.user_name order by ctr desc;
			public Object execute(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				con.prepareStatement()
				return null;
			}
			
		}.execute();
	}
	public List getMostRelatedForAllUsers(String tag) {
		return null;
	}*/
	
	/* 
	 * c_a_gem(x,y) = Anzahl gemeinsamen Auftretens von tagname x und tagname y f�r denselben content (Tabelle tagtag)
	 * a_all(x) = Anzahl taggings mit namen x gesamt (tabelle tags)
	 * 
	 * u_a_gem(x,y) = Anzahl gemeinsamen Auftretens von tagname x und tagname y f�r denselben user
	 * 
	 * keine symmetrische "�hnlichkeit"-Relation, denn z.B. "sparkasse => wahrscheinlich bank" aber "bank => nicht so wahrscheinlich sparkasse"
	 * Wie gut kann tag mit Name x durch das tag mit Name y ersetzt werden = tagSim(x, y) = c_a_gem(x,y) / a_all(x) + faktor * (u_a_gem(x,y) / a_all(x))
	 * 
	 * 
	 */
//	private static interface Tag {
//		public String getName();
		/** Wie oft wurde ein spezieller Content damit getagt (von allen Benutzern) */
//		public int getOccurenceAmountFor(Content c);
		/** An wie vielen verschiedenen Contents das Tag h�ngt */ 
/*		public int getContentFrequency();
		
		public List<Tag> getMostSimilarTags();
	}
	private static interface Content {
		public Collection<Tag> getTags();
	}
	
	// Wie ähnlich ist content c1 dem content c2 
	public static double similarity(Content c1, Content c2) {
		double sim = 0.0;
		for (Tag t : c1.getTags()) {
			sim += (t.getOccurenceAmountFor(c1) * t.getOccurenceAmountFor(c2)) / t.getContentFrequency();
		}
		return sim;
	}*/
	/* 
	 * Wie gut passt content c zu user u = 
	 */
}
