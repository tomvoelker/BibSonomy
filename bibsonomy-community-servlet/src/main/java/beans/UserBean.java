package beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.Role;

import filters.InitUserFilter;

/**
 * Still used in {@link ActionValidationFilter}, {@link InitUserFilter}, 
 * {@link SessionSettingsFilter}, and several Servlets!
 * @author rja
 *
 */
@Deprecated
public class UserBean implements Serializable {

	private static final long serialVersionUID = -7295358376819936465L;
	
	protected String name 	      = null;
	protected String homepage     = "";
	protected String email        = "";
	protected String realname     = "";
	protected String openurl      = ""; // BASE_URL for this users openURL service (http://www.exlibrisgroup.com/sfx_openurl_syntax.htm)
	protected int postsInBasket   = 0;
	protected Role role			  = Role.DEFAULT; 
	
	
	private int tagboxStyle     = 0;  // 0 = cloud, 1 = list
	private int tagboxSort      = 0;  // 0 = alph, 1 = freq
	private int tagboxMinfreq   = 0;  // minimal freq a tag must have to be shown
	private int tagboxTooltip   = 0;  // 0 = don't show, 1 = show (TODO: what does this mean?)
	private int itemcount       = 10; // how many posts to show in post lists?  
	private String defaultLanguage = "en"; // the default language
	private String apiKey = null; // the API key
	private int logLevel = 0;
	private boolean confirmDelete = true;

	private final Set<String> groups = new HashSet<String>(); // groups the user is in
	private List<String> friends = new LinkedList<String>(); // the friends of the user	

	private final int MAX_ITEMCOUNT = 1000; // maximal number of items shown (inclusive)
	
	@Override
	public String toString () {
		return name + "(" + realname + " <" + email + ">): " +
				"style="     + tagboxStyle   + ", " +
				"sort="      + tagboxSort    + ", " +
				"minfreq="   + tagboxMinfreq + ", " +
				"itemcount=" + itemcount     + ", " +
				"postsIB="   + postsInBasket;
	}
	

	
	/** Returns all groups the user is in, including public, private, friends!
	 * 
	 * This is a workaround (or maybe a good solution? ;-) to show all groups in
	 * the "viewable for" selection when posting something.
	 * 
	 * @return
	 */
	public List<String> getAllGroups () {
		List<String> result = new LinkedList<String>();
		result.add("public");
		result.add("private");
		result.add("friends");
		result.addAll(groups);
		return result;
	}
	public void addGroup (String group) {
		groups.add(group);
	}
	public Set<String> getGroups() {
		return groups;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getItemcount() {
		return itemcount;
	}
	public void setItemcount(int itemcount) {
		if (itemcount <= MAX_ITEMCOUNT && itemcount > 0) {
			this.itemcount = itemcount;	
		}
	}
	public int getTagboxMinfreq() {
		return tagboxMinfreq;
	}
	public void setTagboxMinfreq(int minfreq) {
		this.tagboxMinfreq = minfreq;
	}
	public int getTagboxSort() {
		return tagboxSort;
	}
	public void setTagboxSort(int sort) {
		this.tagboxSort = sort;
	}
	public int getTagboxStyle() {
		return tagboxStyle;
	}
	public void setTagboxStyle(int style) {
		this.tagboxStyle = style;
	}
	public int getTagboxTooltip() {
		return tagboxTooltip;
	}
	public void setTagboxTooltip(int tooltip) {
		this.tagboxTooltip = tooltip;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public String getOpenurl() {
		return openurl;
	}
	public void setOpenurl(String openurl) {
		this.openurl = openurl;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public int getPostsInBasket() {
		return postsInBasket;
	}
	public void setPostsInBasket(int postsInBasket) {
		this.postsInBasket = postsInBasket;
	}

	public List<String> getFriends() {
		return friends;
	}
	public void setFriends(List<String> friends) {
		this.friends = friends;
	}
	public void addFriend(String name) {		
		friends.add(name);
	}
	public String getDefaultLanguage() {
		return this.defaultLanguage;
	}
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	public Role getRole() {
		return this.role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getApiKey() {
		return this.apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}



	public int getLogLevel() {
		return this.logLevel;
	}



	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}



	public String getConfirmDelete() {
		return this.confirmDelete ? "true" : "false";
	}



	public void setConfirmDelete(String confirmDelete) {
		this.confirmDelete = "true".equals(confirmDelete);
	}
}