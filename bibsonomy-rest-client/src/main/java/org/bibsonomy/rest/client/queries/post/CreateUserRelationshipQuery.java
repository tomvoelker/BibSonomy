package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Creatte a relationship among users.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class CreateUserRelationshipQuery extends AbstractQuery<String> {

	/** parameter value for friend relationship */
	public final static String FRIEND_RELATIONSHIP = "friend";
	/** paramaeter value for follower relationship */
	public final static String FOLLOWER_RELATIONSHIP = "follower";

	/** source user */
	private String username;
	/** target user */
	private String targetUserName;
	/** type of relationship (friend/follower) */
	private String relationType;
	/** tag for tagged relationships */
	private String tag;

	/**
	 * Create new query.
	 * 
	 * @param username
	 *            - the (currently logged in) source user
	 * @param targetUserName
	 *            - the name of the user to establish a relationship with
	 * @param relationType
	 *            - the type of relationship (i.e. "friend" or "follower"
	 * @param tag
	 *            - a tag (for taggged relationships)
	 */
	public CreateUserRelationshipQuery(final String username, final String targetUserName, final String relationType, final String tag) {
		/*
		 * check input
		 */
		if (!present(username)) throw new IllegalArgumentException("No source user given!");
		if (!present(targetUserName)) throw new IllegalArgumentException("No target user given");
		if (!(FRIEND_RELATIONSHIP.equals(relationType) || FOLLOWER_RELATIONSHIP.equals(relationType))) {
			throw new IllegalArgumentException("Relation type must be either '" + FRIEND_RELATIONSHIP + "' or '" + FOLLOWER_RELATIONSHIP + "'");
		}
		/*
		 * set params
		 */
		this.username = username;
		this.targetUserName = targetUserName;
		this.relationType = relationType;
		this.tag = tag;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		/*
		 * create body of request
		 */
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeUser(sw, new User(this.targetUserName), null);
		/*
		 * friend/follower, tag
		 */
		final String friendOrFollower = ( FRIEND_RELATIONSHIP.equals(relationType) ? URL_FRIENDS : URL_FOLLOWERS );
		final String queryTag = ( present(tag) ? "/"+tag : "" );
		/*
		 * perform request
		 */
		this.downloadedDocument = performRequest(HttpMethod.POST, URL_USERS + "/" + this.username + "/" + friendOrFollower + queryTag + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
		return null;

	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getTargetUserName() {
		return this.targetUserName;
	}

	public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}

	public String getRelationType() {
		return this.relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}	

}
