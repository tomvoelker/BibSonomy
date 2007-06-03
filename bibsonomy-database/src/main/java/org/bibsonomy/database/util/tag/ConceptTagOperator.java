/*
 * Created on 03.06.2007
 */
package org.bibsonomy.database.util.tag;

import java.util.ArrayList;

import org.bibsonomy.model.Tag;

public class ConceptTagOperator implements TagOperator {
	private boolean leftToRight = true;
	
	public boolean isLeftToRight() {
		return this.leftToRight;
	}

	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	public String getName() {
		if (leftToRight == true) {
			return "->";
		}
		return "<-";
	}

	public void operate(Tag left, Tag right) {
		if (leftToRight == false) {
			final Tag tmp = left;
			left = right;
			right = tmp;
		}
		if (left.getSuperTags() == null) {
			left.setSuperTags(new ArrayList<Tag>());
		}
		if (right.getSubTags() == null) {
			right.setSubTags(new ArrayList<Tag>());
		}
		left.getSuperTags().add(right);
		right.getSubTags().add(left);
	}

}
