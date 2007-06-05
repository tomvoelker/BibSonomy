package org.bibsonomy.database.util.tag;

import java.util.ArrayList;

import org.bibsonomy.model.Tag;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ConceptTagOperator implements TagOperator {

	private boolean leftToRight = true;

	public boolean isLeftToRight() {
		return this.leftToRight;
	}

	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	public String getName() {
		if (this.leftToRight == true) {
			return "->";
		}
		return "<-";
	}

	public void operate(Tag left, Tag right) {
		if (this.leftToRight == false) {
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