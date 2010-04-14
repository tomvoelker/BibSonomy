package org.bibsonomy.model.comparators;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Comparator;

import org.bibsonomy.model.Tag;

/**
 * sorts tags according to their global count values
 * 
 * @author fei
 *
 */
public class TagCountComparator implements Comparator<Tag> {

	/**
	 * compares two given tags based on the corresponding global counts
	 */
	public int compare(Tag o1, Tag o2) {
		if( !present(o1) ) {
			if( present(o2) )
				return 1;
			else 
				return 0;
		}
		else if( !present(o2) ) {
			return -1;
		} else {
			return o2.getGlobalcount() - o1.getGlobalcount();
		}
	}

}
