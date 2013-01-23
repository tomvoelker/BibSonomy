package org.bibsonomy.model.user.remote;

import org.bibsonomy.util.ValidationUtils;

/**
 * @author jensi
 * @version $Id$
 */
public class SimpleRemoteUserId implements RemoteUserId {
	private static final long serialVersionUID = -5200167887496639288L;
	
	private String remoteUserId;

	/**
	 * default constructor
	 */
	public SimpleRemoteUserId() {
	}
	
	/**
	 * handy constructor
	 * @param remoteUserId
	 */
	public SimpleRemoteUserId(String remoteUserId) {
		ValidationUtils.assertTrue(remoteUserId != null);
		this.remoteUserId = remoteUserId;
	}
	
	/**
	 * @return the remoteUserId
	 */
	public String getRemoteUserId() {
		return this.remoteUserId;
	}

	/**
	 * @param remoteUserId the remoteUserId to set
	 */
	public void setRemoteUserId(String remoteUserId) {
		this.remoteUserId = remoteUserId;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((obj == this) || ((obj != null) && (obj.getClass() == this.getClass()) && (this.remoteUserId.equals(((SimpleRemoteUserId)obj).remoteUserId))));
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode() + remoteUserId.hashCode();
	}
	
	@Override
	public String getSimpleId() {
		return remoteUserId;
	}

	@Override
	public RemoteUserNameSpace getNameSpace() {
		return new NameSpace(this);
	}
	
	protected static class NameSpace implements RemoteUserNameSpace {
		private final SimpleRemoteUserId ruid;
		
		/**
		 * @param ruid
		 */
		public NameSpace(SimpleRemoteUserId ruid) {
			this.ruid = ruid;
		}
		
		@Override
		public boolean equals(Object obj) {
			if ((obj instanceof NameSpace) == false) {
				return false;
			}
			return ruid.getClass().equals( ((NameSpace) obj).ruid.getClass());
		}
		
		@Override
		public int hashCode() {
			return ruid.getClass().hashCode();
		}
	}
}
