/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package de.unikassel.puma.openaccess.sword;

/**
 * bean for configuring sword
 * 
 * @author sven
 */
@Deprecated // TODO: move into swordSerice
public class SwordConfig {
	/** temp directory to build zip-file for sword-deposit */
	private String dirTemp;
	
	/** name or url of sword server of repository */
	private String httpServer;
	
	/** port number of sword server */
	private int httpPort = 80;
	
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
		return this.dirTemp;
	}

	/**
	 * @param dirTemp the dirTemp to set
	 */
	public void setDirTemp(final String dirTemp) {
		this.dirTemp = dirTemp;
	}

	/**
	 * @return the httpServer
	 */
	public String getHttpServer() {
		return this.httpServer;
	}

	/**
	 * @param httpServer the httpServer to set
	 */
	public void setHttpServer(final String httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * @return the httpPort
	 */
	public int getHttpPort() {
		return this.httpPort;
	}

	/**
	 * @param httpPort the httpPort to set
	 */
	public void setHttpPort(final int httpPort) {
		this.httpPort = httpPort;
	}

	/**
	 * @return the httpUserAgent
	 */
	public String getHttpUserAgent() {
		return this.httpUserAgent;
	}

	/**
	 * @param httpUserAgent the httpUserAgent to set
	 */
	public void setHttpUserAgent(final String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}

	/**
	 * @return the httpServicedocumentUrl
	 */
	public String getHttpServicedocumentUrl() {
		return this.httpServicedocumentUrl;
	}

	/**
	 * @param httpServicedocumentUrl the httpServicedocumentUrl to set
	 */
	public void setHttpServicedocumentUrl(final String httpServicedocumentUrl) {
		this.httpServicedocumentUrl = httpServicedocumentUrl;
	}

	/**
	 * @return the httpDepositUrl
	 */
	public String getHttpDepositUrl() {
		return this.httpDepositUrl;
	}

	/**
	 * @param httpDepositUrl the httpDepositUrl to set
	 */
	public void setHttpDepositUrl(final String httpDepositUrl) {
		this.httpDepositUrl = httpDepositUrl;
	}

	/**
	 * @return the authUsername
	 */
	public String getAuthUsername() {
		return this.authUsername;
	}

	/**
	 * @param authUsername the authUsername to set
	 */
	public void setAuthUsername(final String authUsername) {
		this.authUsername = authUsername;
	}

	/**
	 * @return the authPassword
	 */
	public String getAuthPassword() {
		return this.authPassword;
	}

	/**
	 * @param authPassword the authPassword to set
	 */
	public void setAuthPassword(final String authPassword) {
		this.authPassword = authPassword;
	}

}
