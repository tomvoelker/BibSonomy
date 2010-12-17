package de.unikassel.puma.openaccess.sword;

/**
 * bean for configuring lucene via JNDI
 * 
 * @author
 * @version
 */

public class SwordConfig {
	/** temp directory to build zip-file for sword-deposit */
	private String dirTemp;
	
	/** name or url of sword server of repository */
	private String httpServer;
	
	/** port number of sword server */
	private Integer httpPort = 80;
	
	/** user agent to send to sword server */
	private String httpUserAgent = "PUMA";
	
	/** url to sword service document, e.g.: "/sword/servicedocument" */
	private String httpServicedocumentUrl;
	
	/** url to deposit sword document, e.g. "http://servername:8080/sword/deposit/urn:nbn:de:hebis:12-3456" */
	private String httpDepositUrl;
	
	/** sword authentication username */
	private String authUsername;
	
	/** sword authentication password */
	private String authPassword;

	/**
	 * @return the dirTemp
	 */
	public String getDirTemp() {
		return dirTemp;
	}

	/**
	 * @param dirTemp the dirTemp to set
	 */
	public void setDirTemp(String dirTemp) {
		this.dirTemp = dirTemp;
	}

	/**
	 * @return the httpServer
	 */
	public String getHttpServer() {
		return httpServer;
	}

	/**
	 * @param httpServer the httpServer to set
	 */
	public void setHttpServer(String httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * @return the httpPort
	 */
	public Integer getHttpPort() {
		return httpPort;
	}

	/**
	 * @param httpPort the httpPort to set
	 */
	public void setHttpPort(Integer httpPort) {
		this.httpPort = httpPort;
	}

	/**
	 * @return the httpUserAgent
	 */
	public String getHttpUserAgent() {
		return httpUserAgent;
	}

	/**
	 * @param httpUserAgent the httpUserAgent to set
	 */
	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}

	/**
	 * @return the httpServicedocumentUrl
	 */
	public String getHttpServicedocumentUrl() {
		return httpServicedocumentUrl;
	}

	/**
	 * @param httpServicedocumentUrl the httpServicedocumentUrl to set
	 */
	public void setHttpServicedocumentUrl(String httpServicedocumentUrl) {
		this.httpServicedocumentUrl = httpServicedocumentUrl;
	}

	/**
	 * @return the httpDepositUrl
	 */
	public String getHttpDepositUrl() {
		return httpDepositUrl;
	}

	/**
	 * @param httpDepositUrl the httpDepositUrl to set
	 */
	public void setHttpDepositUrl(String httpDepositUrl) {
		this.httpDepositUrl = httpDepositUrl;
	}

	/**
	 * @return the authUsername
	 */
	public String getAuthUsername() {
		return authUsername;
	}

	/**
	 * @param authUsername the authUsername to set
	 */
	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}

	/**
	 * @return the authPassword
	 */
	public String getAuthPassword() {
		return authPassword;
	}

	/**
	 * @param authPassword the authPassword to set
	 */
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

}
