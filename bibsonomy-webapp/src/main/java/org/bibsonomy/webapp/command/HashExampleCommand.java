package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * TODO: Beschreibung.
 * @author janus
 */
public class HashExampleCommand extends BaseCommand {
    
    private Post<BibTex> post;

    public Post<BibTex> getPost() {
        return post;
    }

    public void setPost(Post<BibTex> post) {
        this.post = post;
    }
    
}
