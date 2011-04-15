package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

import de.unikassel.puma.openaccess.sword.SwordService;

/**
 * 
 * For strange Java Generics reasons I could not implement the 
 * {@link #instantiateEditPostCommand()} method in exactly the
 * same way in the {@link AbstractEditPublicationController}. Thus I had
 * to make that controller abstract and implement the method 
 * here.
 * 
 * The underlying problem is a bit deeper: I had to parametrize
 * {@link AbstractEditPublicationController} to subclass it in
 * {@link PostPublicationController}.
 * 
 * @author rja
 * @version $Id$
 */
public class EditPublicationController extends AbstractEditPublicationController<EditPublicationCommand> {

	private SwordService swordService = null;

	@Override
	protected EditPublicationCommand instantiateEditPostCommand() {
		return new EditPublicationCommand();
	}
	
	@Override
	protected View finalRedirect(String userName, Post<BibTex> post, String referer) {
		/*
		 * If a SWORD service is configured and the user claims to be the creator of the 
		 * publication, we forward him to the SWORD service to allow the user to upload the
		 * publication.
		 */
		
		if (present(swordService) || SystemTagsUtil.containsSystemTag(post.getTags(), "myown")) {
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
