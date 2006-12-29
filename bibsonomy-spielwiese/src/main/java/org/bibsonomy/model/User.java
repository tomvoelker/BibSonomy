package org.bibsonomy.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.bibsonomy.DefaultValues;

/**
 * This class is used to test how to read/write
 * 
 * @author Robert Jäschke
 */
public class User {

	private String name;
	private Date registrationDate;
	private String email;
	private String realname;
	private URL homepage;

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
}