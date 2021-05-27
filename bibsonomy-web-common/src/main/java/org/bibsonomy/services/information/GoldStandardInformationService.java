package org.bibsonomy.services.information;

import org.antlr.stringtemplate.StringTemplate;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

import static org.bibsonomy.util.ValidationUtils.present;


/**
 * A mail information service to inform about changes in goldstandards.
 *
 * @author kchoong
 */
public class GoldStandardInformationService extends MailInformationService{

    /**
     * Current property to determine, if the given user should be notified about goldstandard changes
     */
    private boolean crisEnabled;

    @Override
    protected void setAttributes(StringTemplate stringTemplate, User userToInform, Post<? extends Resource> post) {
        stringTemplate.setAttribute("user", userToInform.getName());
        stringTemplate.setAttribute("title", post.getResource().getTitle());
        stringTemplate.setAttribute("history", this.absoluteURLGenerator.getHistoryUrlForPost(post));
    }

    @Override
    protected boolean userWantsToBeInformed(User userToInform) {
        return present(this.getMailAddress(userToInform)) && this.crisEnabled;
    }


    /**
     * @param crisEnabled
     */
    public void setCrisEnabled(boolean crisEnabled) {
        this.crisEnabled = crisEnabled;
    }
}
