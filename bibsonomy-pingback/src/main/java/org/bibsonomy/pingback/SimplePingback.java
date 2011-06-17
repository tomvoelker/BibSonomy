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
 * TODO: implement Trackback
 * 
 * @author rja
 * @version $Id$
 */
public class SimplePingback implements Pingback {

	private static final Log log = LogFactory.getLog(Pingback.class);

	private URLGenerator urlGenerator;
	private PingbackClient pingbackClient;
	private PingbackClient trackbackClient;
	private LinkLoader linkLoader;


	/**
	 * TODO: wir ziehen zigmal die Webseite (Recommender, Keywords, Scraper, 
	 * etc. - so langsam sollten wir das mal cachen o.Ä.)
	 *  
	 * @param post
	 * @param linkAddress
	 * @return The return status from the corresponding pingback/trackback clients 
	 * or <code>null</code> if no pingback/trackback link was discovered.
	 */
	public String sendPingback(final Post<? extends Resource> post) {
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
					log.debug("found pingback link");
					if (link.isPingbackEnabled()) {
						return "pingback: " + sendPingback(post, link);
					} else if (link instanceof TrackbackLink) {
						/*
						 * check for trackback
						 */
						log.debug("found trackback link");
						final TrackbackLink trackbackLink = (TrackbackLink) link;
						if (trackbackLink.isTrackbackEnabled()) {
							return "trackback: " + sendTrackback(post, trackbackLink);
						}
					}
				}
			}
		}
		return null; 
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

	private String sendPingback(final Post<? extends Resource> post, final Link link) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("sending pingback for " + link.getUrl() + " to " + link.getPingbackUrl());
			}
			post.getResource().recalculateHashes(); // FIXME: shouldn't the UrlGenerator take care of this?
			final String permaLink = urlGenerator.getPostUrl(post);
			return pingbackClient.sendPingback(permaLink, link);
		} catch (final PingbackException e) {
			log.debug("Pingback to '" + link.getUrl() + "' failed", e);
			if (PingbackClient.PINGBACK_ALREADY_REGISTERED == e.getFaultCode()) {
				log.debug("Pingback to '" + link.getUrl() + "' already registered");
			}
			return "error (" + e.getMessage() + ")";
		}
	}

	private String sendTrackback(final Post<? extends Resource> post, final TrackbackLink link) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("sending trackback for " + link.getUrl() + " to " + link.getPingbackUrl());
			}
			post.getResource().recalculateHashes(); // FIXME: shouldn't the UrlGenerator take care of this?
			final String permaLink = urlGenerator.getPostUrl(post);
			return trackbackClient.sendPingback(permaLink, link);
		} catch (final PingbackException e) {
			log.debug("Trackback to '" + link.getUrl() + "' failed", e);
			return "error (" + e.getMessage() + ")";
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

	@Required
	public void setTrackbackClient(PingbackClient trackbackClient) {
		this.trackbackClient = trackbackClient;
	}

}
