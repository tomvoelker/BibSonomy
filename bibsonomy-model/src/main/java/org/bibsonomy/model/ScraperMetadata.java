/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.model;

import java.io.Serializable;
import java.net.URL;

/**
 * represents meta data for scrapers 
 * 
 * @author rja
 */
public class ScraperMetadata implements Serializable {
	private static final long serialVersionUID = -314704072107016413L;
	
	private URL url;
	private String metaData;
	private String scraperClass;
	private int id;
	
	/**
	 * @return the url
	 */
	public URL getUrl() {
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
	
	/**
	 * @return the metaData
	 */
	public String getMetaData() {
		return this.metaData;
	}
	
	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	
	/**
	 * @return the scraperClass
	 */
	public String getScraperClass() {
		return this.scraperClass;
	}
	
	/**
	 * @param scraperClass the scraperClass to set
	 */
	public void setScraperClass(String scraperClass) {
		this.scraperClass = scraperClass;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
}