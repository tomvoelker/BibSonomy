/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.pingback;

import com.malethan.pingback.Link;

/**
 * @author rja
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
