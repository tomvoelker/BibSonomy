package org.bibsonomy.webapp.util.importer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.json.JsonObject;
import com.restfb.types.User;

/**
 * imports friends from facebook
 * 
 * TODO: also import friend lists (https://graph.facebook.com/me/friendLists?access_token=)
 * 
 * @author fei
 * @version $Id$
 */
public class FacebookFriendsImporter extends AbstractFriendsImporter<User> {
	private final static Log log = LogFactory.getLog(FacebookFriendsImporter.class);

	/**
	 * maps facebook user objects to BibSonomy user objects
	 *  
	 * @author fei
	 */
	public class FacebookUserAdapter implements UserAdapter<User> {

		@Override
		public org.bibsonomy.model.User getUser(final User user) {
			org.bibsonomy.model.User importUser = new org.bibsonomy.model.User();
			importUser.setRealname(user.getName());
			importUser.setEmail(user.getEmail());
			importUser.setBirthday(user.getBirthdayAsDate());
			importUser.setGender(user.getGender());
			importUser.setApiKey(user.getId());
			if (present(user.getInterestedIn())) {
				String hobbies;
				StringBuilder out = new StringBuilder();
				for (Object o : user.getInterestedIn()) {
					out.append(o.toString());
					out.append("\n");
				}
				hobbies = out.toString();
				importUser.setHobbies(hobbies);
			}
			try {
				importUser.setHomepage(new URL(user.getWebsite()));
			} catch (MalformedURLException ex) {
				// nop
			}
			importUser.setPlace(user.getHometownName());
			return importUser;
		}
		
	}
	
	@Override
	public Collection<User> getFriends(org.bibsonomy.model.User loginUser) {
		// we abuse the api key property for the access token
		return this.getFacebookFriends(loginUser.getApiKey());
	}

	@Override
	public UserAdapter<User> getUserAdapter() {
		return new FacebookUserAdapter();
	}
	
	/**
	 * actually retrieve list of friends
	 * 
	 * @param oauthToken
	 * @return
	 */
	private Collection<User> getFacebookFriends(String oauthToken) {
		// initialize facebook client
		FacebookClient facebookClient = new DefaultFacebookClient(oauthToken);
		
		// fetch friends
		Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
		log.debug("Importing " + myFriends.getData().size() + " friends from facebook");
		
		// ...iterate over paginated result sets
		Collection<User> fbFriends = new ArrayList<User>();
		for (Collection<com.restfb.types.User> myFriendsCollection : myFriends) {
			fbFriends.addAll(myFriendsCollection);
		}
		
		// now fetch all available data from friends
		List<String> ids = new ArrayList<String>();
		for (com.restfb.types.User friend : fbFriends) {
			ids.add(friend.getId());
		}
		
		// ...query apy
		JsonObject results = facebookClient.fetchObjects(ids, JsonObject.class);

		// ...fill in result objects
		fbFriends.clear();
		JsonMapper jsonMapper = new DefaultJsonMapper();
		for (String id : ids) {
			User friend = jsonMapper.toJavaObject(results.getString(id), User.class);
			fbFriends.add(friend);
		}
		
		// all done
		return fbFriends;
	}

}
