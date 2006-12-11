package org.bibsonomy.ibatis.params.generic;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.common.LimitOffset;

/**
 * define parameters for sql-statement: give me all bibtex entries of the main
 * page
 * 
 * @author mgr
 * 
 */
public abstract class HomePage extends LimitOffset {

	private ConstantID groupType;

	public HomePage() {
		this.groupType = ConstantID.GROUP_PUBLIC;
	}

	public abstract int getContentType();

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}
}