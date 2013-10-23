package org.bibsonomy.pingback;

import com.malethan.pingback.Link;

/**
 * @author rja
 * @version $Id$
 */
public class TrackbackLink extends Link {

	/**
	 * default constructor 
	 * @param title
	 * @param url
	 * @param pingbackUrl
	 * @param success
	 */
	public TrackbackLink(String title, String url, String pingbackUrl, boolean success) {
		super(title, url, pingbackUrl, success);
	}
	
	@Override
	public boolean isPingbackEnabled() {
		return false;
	}
	
	/**
	 * 
	 * @return <code>true</code> if trackpack is enabled
	 */
	public boolean isTrackbackEnabled() {
		return super.isPingbackEnabled();
	}

}
