package org.bibsonomy.opensocial.spi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * class for managing application (i.e. gadget) specific personal data store
 *  
 * @author fei
 */
public class BibSonomyAppDataSpi implements AppDataService {

	public Future<Void> deletePersonData(UserId userId, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<DataCollection> getPersonData(Set<UserId> userIds, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<Void> updatePersonData(UserId userId, GroupId groupId, String appId, Set<String> fields, Map<String, String> values, SecurityToken token) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

}
