package resources;

/**
 * adds a "count" field to tag relations, which can be used
 * for the /relations page
 *
 */
public class ExtendedTagRelation extends TagRelation {

	private int count = 0;
	
	public ExtendedTagRelation (String lower, String upper, int count) {
		super (lower, upper);
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}