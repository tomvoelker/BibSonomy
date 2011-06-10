package org.bibsonomy.pingback;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.services.Pingback;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.UrlUtils;
import org.springframework.beans.factory.annotation.Required;

import com.malethan.pingback.Link;
import com.malethan.pingback.LinkLoader;
import com.malethan.pingback.PingbackClient;
import com.malethan.pingback.PingbackException;

/**
 * Sends Pingbacks or Trackbacks to external web pages that got bookmarked.
 * 
 * FIXME: use HttpClient (multi-threading!)
 * FIXME: use multithreading
 * TODO: implement Trackback
 * 
 * @author rja
 * @version $Id$
 */
public class SimplePingback implements Pingback {

	private static final Log log = LogFactory.getLog(Pingback.class);

	private URLGenerator urlGenerator;
	private PingbackClient pingbackClient;
	private LinkLoader linkLoader;


	/**
	 * TODO: wir ziehen zigmal die Webseite (Recommender, Keywords, Scraper, 
	 * etc. - so langsam sollten wir das mal cachen o.Ä.)
	 *  
	 * @param post
	 * @param linkAddress
	 */
	public void sendPingback(final Post<? extends Resource> post) {
		/*
		 * send only pings for public posts
		 */
		if (GroupUtils.isPublicGroup(post.getGroups())) {
			/*
			 * extract URL
			 */
			final String linkAddress = getLinkAddress(post);
			if (present(linkAddress)) {
				final Link link = linkLoader.loadLink(linkAddress);
				if (link.isSuccess()) {
					if (link.isPingbackEnabled()) {
						sendPingback(post, link);
					}
				}
			}
		}
	}

	/**
	 * Extracts a URL from the post. Easy for bookmarks, a little more difficult
	 * for publications.
	 * 
	 * @param post
	 * @return
	 */
	private String getLinkAddress(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		if (resource instanceof Bookmark) {
			return ((Bookmark) resource).getUrl();
		} else if (resource instanceof BibTex) {
			final BibTex bibtex = (BibTex) resource;

			final String url = bibtex.getUrl();
			if (present(url)) return UrlUtils.cleanBibTeXUrl(url);
			bibtex.serializeMiscFields();
			
			final String ee = bibtex.getMiscField("ee");
			if (present(ee)) return UrlUtils.cleanBibTeXUrl(ee);
		}
		return null;
	}

	private void sendPingback(final Post<? extends Resource> post, final Link link) {
		try {
			post.getResource().recalculateHashes(); // FIXME: shouldn't the UrlGenerator take care of this?
			final String permaLink = urlGenerator.getPostUrl(post);
			pingbackClient.sendPingback(permaLink, link);
		} catch (final PingbackException e) {
			log.debug("Pingback to '" + link.getUrl() + "' failed", e);
			if (PingbackClient.PINGBACK_ALREADY_REGISTERED == e.getFaultCode()) {
				log.debug("Pingback to '" + link.getUrl() + "' already registered");
			}
		}
	}

	@Required
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	@Required
	public void setPingbackClient(PingbackClient pingbackClient) {
		this.pingbackClient = pingbackClient;
	}

	@Required
	public void setLinkLoader(LinkLoader linkLoader) {
		this.linkLoader = linkLoader;
	}

}
