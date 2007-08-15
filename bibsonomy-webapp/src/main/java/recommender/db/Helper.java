/*
 * Created on 13.01.2006
 */
package recommender.db;


public class Helper {
	public static String buildCSVList(Iterable<? extends Object> c) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object o : c) {
			if (first == false) {
				sb.append(',');
			} else {
				first = false;
			}
			sb.append(o.toString());
		}
		return sb.toString();
	}
	
	public static String buildQuotedCSVList(Iterable<? extends Object> c) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object o : c) {
			if (first == false) {
				sb.append("','");
			} else {
				sb.append('\'');
				first = false;
			}
			//sb.append(o.toString().replaceAll("([^\\\\]|^)([''])","$1\\\\$2"));
			sb.append(escape(o.toString()));
		}
		if (first == false) {
			sb.append('\'');
		}
		return sb.toString();
	}
	
	public static String escape(String s) {
		return s.replaceAll("\\\\'","'").replaceAll("'","\\\\'");
	}
}
