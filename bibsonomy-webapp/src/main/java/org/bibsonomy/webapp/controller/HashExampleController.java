package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.HashExampleCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * controller for
 * 		- /hashexample
 * 
 * TODO: adapt or delete http://www.bibsonomy.org/help/doc/inside.html 
 * 
 * @author janus
 * @version $Id$
 */
public class HashExampleController implements MinimalisticController<HashExampleCommand>, ErrorAware {
    
    private Errors errors;

    @Override
    public HashExampleCommand instantiateCommand() {
        final HashExampleCommand command = new HashExampleCommand();
        final Post<BibTex> post = new Post<BibTex>();
        post.setResource(new BibTex());
        command.setPost(post);
        return command;
    }

    @Override
    public View workOn(final HashExampleCommand command) {
        command.getPost().getResource().recalculateHashes();
        return Views.HASHEXAMPLE;
    }

    @Override
    public Errors getErrors() {
        return this.errors;
    }

    @Override
    public void setErrors(final Errors errors) {
        this.errors = errors;
    }
}
