/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.model.user.remote;

import org.bibsonomy.util.ValidationUtils;

/**
 * @author jensi
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
		ValidationUtils.assertNotNull(remoteUserId);
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
