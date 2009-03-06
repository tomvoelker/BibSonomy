package org.bibsonomy.recommender.tags.simple;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tag recommender which parses html file at given url for keywords from the meta-inf section.
 * 
 * @author fei
 * @version $Id$
 */
public class MetaInfoTagRecommender implements TagRecommenderConnector {
	private static final Logger log = Logger.getLogger(MetaInfoTagRecommender.class);
	
	public void addRecommendedTags(SortedSet<RecommendedTag> recommendedTags,
			Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Recommender using html <meta> informations.";
	}

	/**
	 * Parse html file at given url an return list of keywords as given in file's meta-inf section.
	 * TODO: Assumes that keywords are comma separated.
	 */
	public SortedSet<RecommendedTag> getRecommendedTags(
			Post<? extends Resource> post) {
		SortedSet<RecommendedTag> result = 
			new TreeSet<RecommendedTag>(new RecommendedTagComparator());

		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) ) {
			String url = ((Bookmark)post.getResource()).getUrl();
			log.debug("Scraping " + url + " for keywords.");
			
			String[] keywords = getKeywordsForUrl(url).split(",");
			if( keywords.length>0 ) {
				for( int i=0; i<keywords.length; i++ ){
					if(keywords[i].length()>0)
						// FIXME: compute sensible confidence/score values.
						// FIXME: Normalizing of tags should be done in some central helper class
						result.add(new RecommendedTag(keywords[i].toLowerCase().trim().replaceAll("\\s", "_"),0.5,0));
				}
			}
		}
		return result;
	}
	
	/**
	 * Parses html file at given url and returns keywords from its meta informations.
	 * @param url file's url
	 * @return keywords as given in html file if present, empty string otherwise.
	 */
	private String getKeywordsForUrl(String url) {
		String keywordsStr = "";
		try {
			final Document document = XmlUtils.getDOM(new URL(url));
			
			final NodeList metaList = document.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				final Element metaElement = (Element) metaList.item(i);
				
				Attr nameAttr = metaElement.getAttributeNode("name");
				if( (nameAttr!=null) && (nameAttr.getNodeValue().equalsIgnoreCase("keywords")) ) {
					keywordsStr += metaElement.getAttribute("content");
					log.debug("KEYWORDS for URL "+url.toString()+":"+keywordsStr);
				}
			}
		} catch (IOException ex) {
			// ignore exceptions silently
		}
		return keywordsStr;
	}
	

	public boolean connect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean disconnect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean initialize(Properties props) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
