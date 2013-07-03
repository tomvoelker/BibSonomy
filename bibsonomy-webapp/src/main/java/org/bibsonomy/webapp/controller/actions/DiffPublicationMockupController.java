package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.DiffPublicationCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.sword.SwordService;

/**
 * @author PlatinAge
 * @version $Id$
 */
public class DiffPublicationMockupController extends AbstractEditPublicationController<DiffPublicationCommand> {
	private static final Log LOGGER = LogFactory.getLog(DiffPublicationMockupController.class);

	private SwordService swordService = null;

	@Override
	protected View getPostView() {
		return Views.DIFFPUBLICATIONPAGE; // TODO: this could be configured using Spring!
	}
	
	@Override
	protected DiffPublicationCommand instantiateEditPostCommand() {
		final DiffPublicationCommand command = new DiffPublicationCommand();
		Post<BibTex> tmpTestPost = new Post<BibTex>();
		tmpTestPost.setResource(instantiateBibTex());
		command.setPostDiff(tmpTestPost);
		tmpTestPost.setDescription("Diff Publication Controller description");
		//command.getPostDiff().setResource(instantiateBibTex());
		return command;
	}
	
	
	protected BibTex instantiateBibTex() {
		BibTex bibtex = new BibTex();
		//TODO: testzweck (required/general infos)
		bibtex.setTitle("My Preprint test words");
		List<PersonName> person = new ArrayList<PersonName>();
		person.add( new PersonName("Captain", "Editor"));
		bibtex.setAuthor(person);
		bibtex.setEditor(person);
		bibtex.setYear("year");
		
		bibtex.setBooktitle("booktitle");
		bibtex.setJournal("journal");
		bibtex.setVolume("volume One");
		bibtex.setNumber("number");
		bibtex.setPages("pages");
		bibtex.setMonth("month");
		bibtex.setDay("day");
		bibtex.setPublisher("publisher");
		bibtex.setAddress("address");
		bibtex.setEdition("edition");
		bibtex.setChapter("chapter");
		bibtex.setUrl("url");
		bibtex.setKey("key");
		bibtex.setType("type");
		bibtex.setHowpublished("howpublished");
		bibtex.setInstitution("institution");
		bibtex.setOrganization("organization");
		bibtex.setSchool("school");
		bibtex.setSeries("series");
		bibtex.setCrossref("crossref");
		bibtex.setMisc("misc");
		bibtex.setAbstract("bibtexAbstract");
		
		//not visible
		bibtex.setPrivnote("privnote");
		bibtex.setNote("note");
		bibtex.setAnnote("annote");
		return bibtex;
	}
	
	@Override
	protected View finalRedirect(String userName, Post<BibTex> post, String referer) {
		/*
		 * If a SWORD service is configured and the user claims to be the creator of the 
		 * publication, we forward him to the SWORD service to allow the user to upload the
		 * publication.
		 */
		
		if (present(swordService) && SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME)) {
			String ref = UrlUtils.safeURIEncode(referer);
			String publicationUrl = urlGenerator.getPublicationUrl(post.getResource().getIntraHash(), userName);
			return new ExtendedRedirectView(publicationUrl + "?referer=" + ref);
		}
		return super.finalRedirect(userName, post, referer);
	}
	
	/**
	 * @return the swordService
	 */
	public SwordService getSwordService() {
		return this.swordService;
	}

	/**
	 * @param swordService the swordService to set
	 */
	public void setSwordService(SwordService swordService) {
		this.swordService = swordService;
	}
	
	
}
