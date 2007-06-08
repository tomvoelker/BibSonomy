package org.bibsonomy.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.util.DefaultValues;

/**
 * This class defines a user.
 */
public class User {

	/**
	 * The (nick-)name of this user.
	 */
	private String name;

	/**
	 * The (real-)name of this user.
	 */
	private String realname;

	/**
	 * This user's email address.
	 */
	private String email;

	/**
	 * This user's password
	 */
	private String password;

	/**
	 * The {@link Date} when this user registered to bibsonomy.
	 */
	private Date registrationDate;

	/**
	 * Ths {@link URL} to this user's homepage.
	 */
	private URL homepage;

	/**
	 * The user belongs to these groups.
	 */
	private List<Group> groups;

	/**
	 * Those are the posts of this user.
	 */
	private List<Post<? extends Resource>> posts;

	/**
	 * The Api Key for this user
	 */
	private String apiKey;

	/**
	 * Indicates if this user is a spammer.
	 */
	private boolean spammer;

	public boolean isSpammer() {
		return this.spammer;
	}

	public void setSpammer(boolean spammer) {
		this.spammer = spammer;
	}

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
		if (this.groups == null) {
			this.groups = new LinkedList<Group>();
		}
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	public String getApiKey() {
		return this.apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}