package org.bibsonomy.ibatis.params.generic;

import java.util.Date;

public class ByDate {

	private final Date date;

	public ByDate(final Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}	
}