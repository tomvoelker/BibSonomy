/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.RemoteUserNameSpace;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.util.UrlUtils;

/**
 * This class defines a user. An unknown user has an empty (<code>null</code>) name.
 */
@Getter
@Setter
public class User implements Serializable {
	/*
	 * XXX: When adding new fields make sure to integrate them into the updateUser method
	 * {@link UserUtils#updateUser}
	 */

	/**
	 * For persistency (Serializable)
	 */
	private static final long serialVersionUID = -4494680395320981307L;

	/**
	 * The (nick-)name of this user. Is <code>null</code> if the user is not logged in (unknown). 
	 */
	private String name;
	
	/**
	 * This user's password
	 */
	private String password;
	
	/**
	 * this user's password salt
	 */
	private String passwordSalt;
	
	/**
	 * The Api Key for this user
	 */
	private String apiKey;
	/**
	 * Which role the user has in the system (e.g. admin, ...)
	 */
	private Role role;
	
	/**
	 * a set of usernames, this user marked as spammers. 
	 */
	private Set<User> reportedSpammers;

	
	/* ****************************** profile ****************************** */ 
	
	/**
	 * The (real-)name of this user.
	 */
	private String realname;
	/**
	 * This user's email address.
	 */
	private String email;
	/**
	 * Ths {@link URL} to this user's homepage.
	 */
	private URL homepage;
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
	 * Institution (company, etc.)
	 */
	private String institution;
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
	 * OpenURL url
	 * TODO: should be of type url
	 */
	private String openURL;
	
	/**
	 * If an external avatar sercive (e.g. Gravatar) shall be used instead of
	 * an locally uploaded profile picture
	 */
	private boolean useExternalPicture;
	
	/**
	 * User's locally uploaded profile picture file
	 */
	private UploadedFile profilePicture;
	
	/**
	 * holds the users group role.
	 */
	@Deprecated
	private GroupRole groupRole;

	/* ****************************** system properties ****************************** */
	/**
	 * The user belongs to these groups.
	 */
	private List<Group> groups;

	/** a list of groups that the user has requested and are not already activated by an admin */
	private List<Group> pendingGroups;
	
	/**
	 * Holds the friends of this user
	 */
	private List<User> friends;
	/**
	 * Those are the posts of this user.
	 */
	private List<Post<? extends Resource>> posts;
	/**
	 * List of tags which were assigned to this user via a tagged relationship
	 */
	private List<Tag> tags;
	/**
	 * the settings of this user
	 */
	private UserSettings settings;
	/**
	 * Clipboard of this user where he can pick some entries
	 */
	private Clipboard clipboard;
	/**
	 * Inbox of this user where he gets Posts sent by other users
	 */
	private Inbox inbox;


	/* ****************************** classification ****************************** */ 

	/**
	 * Indicates if this user is a spammer.
	 */
	private Boolean spammer;
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
	/**
	 * The confidence of the classifier
	 */
	private Double confidence;
	/** 
	 * The mode of the classifier (day or night)
	 */ 
	private String mode;
	
	/**
	 * The logged interaction at registration form
	 */
	private String registrationLog;
	
	/* ****************************** account management ****************************** */
	
	/**
	 * The Activation Code
	 */
	private String activationCode;
	/**
	 * The {@link Date} when this user registered to bibsonomy.
	 */
	private Date registrationDate;
	/**
	 * IP Address
	 */
	private String IPAddress;
	/**
	 * OpenID url for authentication
	 */
	private String openID;
	/**
	 * LDAP userId for authentication
	 */
	private String ldapId;
	
	/** userids of remote authentication systems such as saml, ldap, and openid */
	private final Map<RemoteUserNameSpace, RemoteUserId> remoteUserIds = new HashMap<>(2);
	/**
	 * The temporary password the user can request when asking for a password reminder.
	 */
	private String reminderPassword;
	/**
	 * The time at which the user requested a password reminder.
	 */
	private Date reminderPasswordRequestDate;

	/** the person that the user has claimed to be */
	private Person claimedPerson;

	/**
	 * Constructor
	 */
	public User() {
		this.role = Role.NOBODY; // TODO: check, if this has any bad implications!
	}

	/**
	 * Constructor
	 * 
	 * @param name the name of the user
	 */
	public User(final String name) {
		this();
		this.setName(name); 
	}

	/**
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name == null ? null : name.toLowerCase();
	}

	/**
	 * @return groups
	 */
	public List<Group> getGroups() {
		if (this.groups == null) {
			this.groups = new LinkedList<>();
		}
		return this.groups;
	}

	public List<Group> getPendingGroups() {
		if (this.pendingGroups == null)
			this.pendingGroups = new LinkedList<>();
		return pendingGroups;
	}

	/**
	 * Convenience method to add a group.
	 * 
	 * @param group
	 */
	public void addGroup(final Group group) {
		// call getGroups to initialize this.groups
		this.getGroups();
		this.groups.add(group);
	}

	/**
	 * @return settings
	 */
	public UserSettings getSettings() {
		if (this.settings == null) {
			this.settings = new UserSettings();
		}
		return this.settings;
	}

	/**
	 * The spammer property can have three states:
	 * <dl>
	 * <dt><code>null</code></dd>
	 * <dd>
	 *  The spam status hasn't been set in this object, i.e., we don't
	 *  know it and don't want to change it.  
	 *  <br/>
	 *  It can never be <code>null</code> for users coming from the 
	 *  DBLogic, since in the DB the property is either 
	 *  <code>true</code> or <code>false</code>.
	 * </dd>
	 * <dt><code>true</code></dt>
	 * <dd>This user is a spammer, for sure.</dd>
	 * <dt><code>false</code></dt>
	 * <dd>This user not a spammer or not yet.</dd>
	 * <dd></dd>
	 * </dl>
	 * @return spammer
	 */
	public Boolean getSpammer() {
		return this.spammer;
	}
	
	/**
	 * @return <code>true</code> if and only if spammer is <code>true</code>.
	 */
	public boolean isSpammer() {
		return this.spammer == null ? false : this.spammer.booleanValue();
	}


	/**
	 * @param openID
	 */
	public void setOpenID(final String openID) {
		this.openID = UrlUtils.normalizeURL(openID);
//		if (openID == null) {
//			remoteUserIds.remove(new OpenIdRemoteUserId("").getNameSpace());
//			return;
//		}
//		setRemoteUserId(new OpenIdRemoteUserId(this.openID));
	}

	/**
	 * @param ldapId
	 */
	public void setLdapId(final String ldapId) {
		this.ldapId = ldapId;
		// TODO: remove?
//		if (ldapId == null) {
//			remoteUserIds.remove(new LdapRemoteUserId("").getNameSpace());
//			return;
//		}
//		setRemoteUserId(new LdapRemoteUserId(ldapId));
	}

	/**
	 * @return clipboard
	 */
	public Clipboard getClipboard() {
		if (this.clipboard == null) {
			this.clipboard = new Clipboard();
		}
		return this.clipboard;
	}

	/**
	 * @return inbox
	 */
	public Inbox getInbox() {
		if (this.inbox == null) {
			this.inbox = new Inbox();
		}
		return this.inbox;
	}

	/**
	 * @return a List of friends
	 */
	public List<User> getFriends() {
		if (this.friends == null) {
			this.friends = new LinkedList<>();
		}
		return this.friends;
	}
	
	/**
	 * TODO: unused?
	 * FIXME: move to util class if it is used;
	 * @return a List with names of user's friends
	 */
	public List<String> getFriendsAsString() {
		if (this.friends == null) {
			this.friends = new LinkedList<>();
		}
		final List<String> friendsAsString = new LinkedList<>();
		for (final User friend : friends) {
			friendsAsString.add(friend.getName());
		}
		return friendsAsString;
	}

	/**
	 * @param friend
	 */
	public void addFriend(final User friend) {
		// call getFriends to initialize this.friends
		this.getFriends();
		this.friends.add(friend);
	}

	/**
	 * 
	 * @param friends
	 */
	public void addFriends(final List<User> friends) {
		// call getFriends to initialize this.friends
		this.getFriends();
		this.friends.addAll(friends);
	}

	/**
	 * @return posts
	 */
	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<>();
		}
		return this.posts;
	}

	/**
	 * @return the remoteUserIds - currently only SAML is supported via this property
	 */
	public Collection<RemoteUserId> getRemoteUserIds() {
		return this.remoteUserIds.values();
	}
	
	/**
	 * removes all remoteUserIds from this user
	 */
	public void clearRemoteUserIds() {
		this.remoteUserIds.clear();
	}
	
	/**
	 * @param remoteId remote Id to be added  - currently only SAML is supported via this property
	 * @return whether remoteId was already attached to this user
	 */
	public boolean setRemoteUserId(RemoteUserId remoteId) {
		return (remoteUserIds.put(remoteId.getNameSpace(), remoteId) != null);
	}

	/**
	 * Check if the user has the particular groupLevelPermission
	 * @param groupLevelPermission
	 * @return true if the user has the particular groupLevelPermission
	 */
	public boolean hasGroupLevelPermission(GroupLevelPermission groupLevelPermission) {
		return this.getGroupLevelPermissions().contains(groupLevelPermission);
	}
	
	/**
	 * @return all group level permissions this user has from any group he is a member of
	 */
	public Set<GroupLevelPermission> getGroupLevelPermissions() {
		Set<GroupLevelPermission> groupLevelPermissions = new HashSet<>();
		for (Group group: this.getGroups()) {
			groupLevelPermissions.addAll(group.getGroupLevelPermissions());
		}
		return groupLevelPermissions;
	}

	/**
	 * Two users are equal, if their name is equal. Users with <code>null</code>
	 * names are not equal.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj != null && obj instanceof User && this.name != null && this.name.equals(((User) obj).name);
	}

	@Override
	public int hashCode() {
		if (this.name != null) return this.name.hashCode();
		return super.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}