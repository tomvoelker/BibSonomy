package org.bibsonomy.opensocial.oauth.database.beans;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;


/**
 * Data structure representing and OAuth consumer key and secret
 */
public class OAuthConsumerInfo {

	/** Value for oauth_consumer_key */
	private String consumerKey;

	/** HMAC secret, or RSA private key, depending on keyType */
	private String consumerSecret;

	/** Type of key */
	private KeyType keyType;

	/** Name of public key to use with xoauth_public_key parameter.  May be null */
	private String keyName;

	/** Callback URL associated with this consumer key */
	private String callbackUrl;
	
	/** URL of the referencing gadget */
	private String gadgetUrl;
	
	private long moduleId;
	
	/** consumer's title */
	private String title;
	/** consumer's summary */
	private String summary;
	/** consumer's description */
	private String description;
	/** consumer's thumbnail */
	private String thumbnail;
	/** consumer's icon */
	private String icon;
	
	/** Name of the server */
	private String serviceName;
		public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setGadgetUrl(String gadgetUrl) {
		this.gadgetUrl = gadgetUrl;
	}

	public String getGadgetUrl() {
		return gadgetUrl;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}


}
