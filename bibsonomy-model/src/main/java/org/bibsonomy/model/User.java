package org.bibsonomy.model;

import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines a user.
 * 
 * TODO document
 * @author dbe
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
	private Integer spammer;
		
	/**
	 * the settings of this user
	 */
	private UserSettings settings = new UserSettings();
	
	/**
	 * OpenURL url
	 */
	private String openURL;
	
	/**
	 * IP Address
	 */
	private String IPAddress;
	
	/**
	 * birthday
	 */
	private Date birthday;
	
	/**
	 * Gender
	 */
	private String gender;
	
	/**
	 * Profession
	 */
	private String profession;
	
	/**
	 * Interests
	 */
	private String interests;
	
	/**
	 * Hobbies
	 */
	private String hobbies;
	
	/**
	 * Location of this user
	 */
	private String place;
	
	/**
	 * Basket of this user where he can pick some entries
	 */
	private Basket basket = new Basket();
		
		
	/**
	 * @return
	 */
	public boolean isSpammer() {
		if (this.spammer == null) {
			return false;
		}
		return 1 == this.spammer;
	}
	
	/**
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return
	 */
	public URL getHomepage() {
		return this.homepage;
	}

	/**
	 * @param homepage
	 */
	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getRealname() {
		return realname;
	}

	/**
	 * @param realname
	 */
	public void setRealname(String realname) {
		this.realname = realname;
	}

	/**
	 * @return
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * @return
	 */
	public List<Group> getGroups() {
		if (this.groups == null) {
			this.groups = new LinkedList<Group>();
		}
		return this.groups;
	}

	/**
	 * @param groups
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return
	 */
	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	/**
	 * @param posts
	 */
	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	/**
	 * @return
	 */
	public String getApiKey() {
		return this.apiKey;
	}

	/**
	 * @param apiKey
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return
	 */
	public UserSettings getSettings() {
		return this.settings;
	}

	/**
	 * @param settings
	 */
	public void setSettings(UserSettings settings) {
		this.settings = settings;
	}

	/**
	 * @return
	 */
	public String getIPAddress() {
		return this.IPAddress;
	}

	/**
	 * @param address
	 */
	public void setIPAddress(String address) {
		this.IPAddress = address;
	}

	/**
	 * @return
	 */
	public Date getBirthday() {
		return this.birthday;
	}

	/**
	 * @param birthday
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return
	 */
	public String getGender() {
		return this.gender;
	}

	/**
	 * @param gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return
	 */
	public String getProfession() {
		return this.profession;
	}

	/**
	 * @param profession
	 */
	public void setProfession(String profession) {
		this.profession = profession;
	}

	/**
	 * @return
	 */
	public String getInterests() {
		return this.interests;
	}

	/**
	 * @param interests
	 */
	public void setInterests(String interests) {
		this.interests = interests;
	}

	/**
	 * @return
	 */
	public String getHobbies() {
		return this.hobbies;
	}

	/**
	 * @param hobbies
	 */
	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	/**
	 * @return
	 */
	public String getPlace() {
		return this.place;
	}

	/**
	 * @param place
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @return
	 */
	public Integer getSpammer() {
		return this.spammer;
	}

	/**
	 * @param spammer
	 */
	public void setSpammer(Integer spammer) {
		this.spammer = spammer;
	}

	public String getOpenURL() {
		return this.openURL;
	}

	public void setOpenURL(String openURL) {
		this.openURL = openURL;
	}

	public Basket getBasket() {
		return this.basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}
}