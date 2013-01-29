package org.bibsonomy.model.user.remote;

import org.bibsonomy.util.ObjectUtils;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlRemoteUserId implements RemoteUserId {
	private static final long serialVersionUID = -4075334406626749850L;
	private String identityProviderId;
	private String userId;
	private RemoteUserNameSpace ns;
	
	/**
	 * default constructor
	 */
	public SamlRemoteUserId() {
	}
	
	/**
	 * handy constructor
	 * @param indentityProviderId
	 * @param userId
	 */
	public SamlRemoteUserId(String indentityProviderId, String userId) {
		this.identityProviderId = indentityProviderId;
		this.userId = userId;
	}
	
	/**
	 * @return the identityProvider
	 */
	public String getIdentityProviderId() {
		return this.identityProviderId;
	}
	/**
	 * @param identityProvider the identityProvider to set
	 */
	public void setIdentityProviderId(String identityProvider) {
		this.ns = null;
		this.identityProviderId = identityProvider;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return this.userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		SamlRemoteUserId other = (SamlRemoteUserId) obj;
		return (ObjectUtils.equal(this.identityProviderId, other.identityProviderId) && ObjectUtils.equal(this.userId, other.userId) );
	}
	
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.getClass(), identityProviderId, userId);
	}

	@Override
	public String getSimpleId() {
		return userId;
	}
	
	@Override
	public RemoteUserNameSpace getNameSpace() {
		if (ns == null) {
			ns = new NameSpace(this);
		}
		return ns;
	}
	
	protected static class NameSpace implements RemoteUserNameSpace {
		private final SamlRemoteUserId ruid;
		
		/**
		 * @param ruid
		 */
		public NameSpace(SamlRemoteUserId ruid) {
			this.ruid = ruid;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NameSpace)) {
				return false;
			}
			NameSpace other = (NameSpace) obj;
			if (! ruid.getClass().equals(other.ruid.getClass())) {
				return false;
			}
			return ObjectUtils.equal(ruid.identityProviderId, other.ruid.identityProviderId);
		}
		
		@Override
		public int hashCode() {
			return ObjectUtils.hashCode(ruid.getClass(), ruid.identityProviderId);
		}
	}
}
