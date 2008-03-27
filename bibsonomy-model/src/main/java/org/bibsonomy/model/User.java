package org.bibsonomy.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.Role;

/**
 * This class defines a user. An unknown user has an empty (<code>null</code>) name.
 */
public class User {

	/**
	 * The (nick-)name of this user. Is <code>null</code> if the user is not logged in (unknown). 
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
	private Basket basket;

	/**
	 * Holds the friends of this user
	 */
	private final List<User> friends;
	
	/**
	 * Which role the user has in the system (e.g. admin, ...)
	 */
	private Role role;
	
	/**
	 * who updated state of the user
	 */
	private String updatedBy;
	
	/**
	 * date of update
	 */
	private Date updatedAt; 
	
	/**
	 * flag if the classifier should take this user
	 * into account for classification
	 */
	private Integer toClassify;
	
	/**
	 * The classification algortihm the user was classified with
	 */
	private String algorithm;
	
	/**
	 * The spammer prediction of the classifier
	 */
	private Integer prediction;
	
	/** The mode of the classiefier (day or night) */ 
	private String mode;
	
	/**
	 * constructor
	 */
	public User() {
		this(null);
	}

	/**
	 * constructor
	 * 
	 * @param name
	 */
	public User(final String name) {
		this.setName(name); 
		this.basket = new Basket();
		this.friends = new ArrayList<User>();
	}

	/**
	 * @return true if this user is a spammer false otherwise
	 */
	public boolean isSpammer() {
		if (this.spammer == null) {
			return false;
		}
		return 1 == this.spammer;
	}

	/**
	 * @return email
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
	 * @return homepage
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
	 * @return name
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
	 * @return realname
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
	 * @return registrationDate
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
	 * @return groups
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
	 * @return password
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
	 * @return posts
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
	 * @return apiKey
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
	 * @return settings
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
	 * @return IPAddress
	 */
	public String getIPAddress() {
		return this.IPAddress;
	}

	/**
	 * @param IPAddress
	 */
	public void setIPAddress(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	/**
	 * @return birthday
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
	 * @return gender
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
	 * @return profession
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
	 * @return interests
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
	 * @return hobbies
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
	 * @return place
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
	 * @return spammer
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

	/**
	 * @return openURL
	 */
	public String getOpenURL() {
		return this.openURL;
	}

	/**
	 * @param openURL
	 */
	public void setOpenURL(String openURL) {
		this.openURL = openURL;
	}

	/**
	 * @return basket
	 */
	public Basket getBasket() {
		return this.basket;
	}

	/**
	 * @param basket
	 */
	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	/**
	 * @return a List of friends
	 */
	public List<User> getFriends() {
		return this.friends;
	}

	/**
	 * Returns the first friend of this user
	 * 
	 * @return friend
	 */
	public User getFriend() {
		if (this.friends.size() < 1) return null;
		// XXX: iBatis should support this: "friends[0].name", which should
		// return the name of the first friend - but this doesn't seem to work
		return this.friends.get(0);
	}

	/**
	 * @param friend
	 */
	public void addFriend(final User friend) {
		this.friends.add(friend);
	}

	/**
	 * @return The role of the user.
	 */
	public Role getRole() {
		return this.role;
	}

	/**
	 * @param role
	 */
	public void setRole(Role role) {
		this.role = role;
	}
		
	/**
	 * @return Classification algorithm the user was classified with
	 */
	public String getAlgorithm() {
		return this.algorithm;
	}

	/**
	 * @param algorithm classification algorithm
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return prediction of classifier
	 */
	public Integer getPrediction() {
		return this.prediction;
	}

	/**
	 * @param prediction Prediction
	 */
	public void setPrediction(Integer prediction) {
		this.prediction = prediction;
	}

	/**
	 * @return if user is considered for classification
	 */
	public Integer getToClassify() {
		return this.toClassify;
	}

	/**
	 * @param toClassify if user should be classified
	 */
	public void setToClassify(Integer toClassify) {
		this.toClassify = toClassify;
	}

	/**
	 * @return person who updates user dataset
	 */
	public String getUpdatedBy() {
		return this.updatedBy;
	}

	/**
	 * @param updatedBy person who updates user dataset
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return Date of update
	 */
	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	/**
	 * @param updatetAt date of update
	 */
	public void setUpdatedAt(Date updatetAt) {
		this.updatedAt = updatetAt;
	}

	public String getMode() {
		return this.mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}			
	
	/**
	 * convenience method to add a group
	 * 
	 * @param group
	 */
	public void addGroup(Group group) {
		if (this.groups == null) {
			this.groups = new ArrayList<Group>();
		}
		this.groups.add(group);
	}
}