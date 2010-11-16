package org.bibsonomy.webapp.util.spring.security;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author dzo
 * @version $Id$
 */
public class UserAdapter implements UserDetails {
	private static final long serialVersionUID = -3926600488722547211L;
	
	private final User user;

	/**
	 * @param user the user to adapt
	 */
	public UserAdapter(final User user) {
		this.user = new UnmodifiableUser(user);
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		final Collection<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		if (Role.ADMIN.equals(this.user.getRole())) {
			authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
		}
		
		return Collections.unmodifiableCollection(authorities);
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO: is logic.getUserDetails also returning deleted users?!
		return !Role.DELETED.equals(this.user.getRole()) ;
	}
	
	private static class UnmodifiableUser extends User {
		
		private static final long serialVersionUID = 1174129644241951251L;

		public UnmodifiableUser(final User user) {
			super();
			this.setActivationCode(user.getActivationCode());
			this.setAlgorithm(user.getAlgorithm());
			this.setApiKey(user.getApiKey());
			this.setBasket(user.getBasket());
			this.setBirthday(user.getBirthday());
			this.setConfidence(user.getConfidence());
			this.addFriends(user.getFriends());
			this.setRole(user.getRole());
			this.setEmail(user.getEmail());
			this.setGender(user.getGender());
			this.setGroups(user.getGroups());
			this.setHobbies(user.getHobbies());
			this.setHomepage(user.getHomepage());
			this.setInbox(user.getInbox());
			this.setInstitution(user.getInstitution());
			this.setInterests(user.getInterests());
			this.setIPAddress(user.getIPAddress());
			this.setLastLdapUpdate(user.getLastLdapUpdate());
			this.setLdapId(user.getLdapId());
			this.setMode(user.getMode());
			this.setOpenID(user.getOpenID());
			this.setOpenURL(user.getOpenURL());
			this.setPassword(user.getPassword());
			this.setPlace(user.getPlace());
			this.setPosts(user.getPosts());
			this.setPrediction(user.getPrediction());
			this.setProfession(user.getProfession());
			this.setRealname(user.getRealname());
			this.setReminderPassword(user.getReminderPassword());
			this.setReminderPasswordRequestDate(user.getReminderPasswordRequestDate());
			this.setSettings(user.getSettings());
			this.setSpammer(user.isSpammer());
			this.setToClassify(user.getToClassify());
			this.setUpdatedAt(user.getUpdatedAt());
			this.setUpdatedBy(user.getUpdatedBy());
			
			super.setName(user.getName());
		}
		
		@Override
		public void setName(String name) {
			throw new UnsupportedOperationException();
		}
	}
}
