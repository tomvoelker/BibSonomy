/**
 * BibSonomy Recommendation - Tag and resource recommender.
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
package org.bibsonomy.recommender.tag.simple;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tag recommender which parses html file at given url for keywords from the meta-inf section.
 * 
 * @author fei
 */
public class MetaInfoTagRecommender extends AbstractTagRecommender {
	private static final Log log = LogFactory.getLog(MetaInfoTagRecommender.class);
	
	@Override
	protected void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> entity) {
		String url = null;
		if (entity.getResource() instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) entity.getResource();
			url = bookmark.getUrl();
		}
		
		if (entity.getResource() instanceof BibTex) {
			final BibTex publication = (BibTex) entity.getResource();
			url = publication.getUrl();
		}
		
		if (url != null) {
			if (!UrlUtils.isValid(url)) {
				log.debug("Invalid url: "+url);
				return;
			}
			log.debug("Scraping " + url + " for keywords.");
			
			final String[] keywords = getKeywordsForUrl(url).split(",");
			int ctr = 0;
			if (keywords.length > 0) {
				for (int i = 0; i < keywords.length; i++){
					final String keyword = keywords[i];
					if (keyword.length() > 0) {
						final String tag = getCleanedTag(keyword.toLowerCase().trim().replaceAll("\\s", "_"));
						if (tag != null) {
							ctr++;
							/*
							 * add one to not get 1.0 as score 
							 */
							recommendedTags.add(new RecommendedTag(tag, 1.0 / (ctr + 1.0), 0));
						}
					}
				}
			}
		}
	}

	@Override
	public String getInfo() {
		return "Recommender using html <meta> informations.";
	}

	/**
	 * Parses html file at given url and returns keywords from its meta informations.
	 * @param url file's url
	 * @return keywords as given in html file if present, empty string otherwise.
	 */
	private static String getKeywordsForUrl(String url) {
		final StringBuilder keywordsStr = new StringBuilder();
		try {
			final Document document = XmlUtils.getDOM(new URL(url));
			
			final NodeList metaList = document.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				final Element metaElement = (Element) metaList.item(i);
				
				final Attr nameAttr = metaElement.getAttributeNode("name");
				if ((nameAttr != null) && (nameAttr.getNodeValue().equalsIgnoreCase("keywords"))) {
					keywordsStr.append(metaElement.getAttribute("content"));
					// TODO: can we stop here? Are there multiple meta keywords?
				}
			}
			log.debug("KEYWORDS for URL " + url.toString() + ":" + keywordsStr);
		} catch (IOException ex) {
			// ignore exceptions silently
		}
		return keywordsStr.toString();
	}

	@Override
	protected void setFeedbackInternal(Post<? extends Resource> entity, RecommendedTag tag) {
		// ignored
	}
}
