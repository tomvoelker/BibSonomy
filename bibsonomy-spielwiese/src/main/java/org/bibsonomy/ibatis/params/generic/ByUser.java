package org.bibsonomy.ibatis.params.generic;

import java.util.Date;

public class ByUser {
	private final String user;
	private Date registrationDate;

	public ByUser(final String user) {
		this.user = user;
	}

	public ByUser(final String user, final Date date) {
		this(user);
		this.registrationDate = date;
	}

	public String getUser() {
		return user;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}
}