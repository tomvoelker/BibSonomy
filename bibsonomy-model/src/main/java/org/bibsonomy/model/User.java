package org.bibsonomy.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.bibsonomy.util.DefaultValues;
import org.bibsonomy.model.Group;

/**
 * This class defines a user.
 */
public class User {

	private String name;
	private String realname;
	private String email;
	private Date registrationDate;
	private URL homepage;
	/** The user belongs to these groups */
	private List<Group> groups;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public URL getHomepage() {
		return this.homepage;
	}

	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	public void setHomepageAsString(String homepage) {
		// FIXME put this into a Helper-method
		try {
			this.homepage = new URL(homepage);
		} catch (final MalformedURLException ex) {
			this.homepage = DefaultValues.getInstance().getBibsonomyURL();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}