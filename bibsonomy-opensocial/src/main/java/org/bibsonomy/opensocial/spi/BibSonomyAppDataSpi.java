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
 * TODO: remove BibSonomy from class name
 * class for managing application (i.e. gadget) specific personal data store
 *  
 * @author fei
 */
public class BibSonomyAppDataSpi implements AppDataService {

	@Override
	public Future<Void> deletePersonData(final UserId userId, final GroupId groupId, final String appId, final Set<String> fields, final SecurityToken token) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<DataCollection> getPersonData(final Set<UserId> userIds, final GroupId groupId, final String appId, final Set<String> fields, final SecurityToken token) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Void> updatePersonData(final UserId userId, final GroupId groupId, final String appId, final Set<String> fields, final Map<String, String> values, final SecurityToken token) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

}
