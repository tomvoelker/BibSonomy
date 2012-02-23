package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.HashExampleCommand;
import org.bibsonomy.webapp.command.ContextCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 *
 * @author janus
 */
public class HashExampleController implements MinimalisticController<HashExampleCommand>, ErrorAware {
    
    private Errors errors;

    @Override
    public HashExampleCommand instantiateCommand() {
        
        final HashExampleCommand command = new HashExampleCommand();
        Post<BibTex> post = new Post<BibTex>();
        post.setResource(new BibTex());
        command.setPost(post);
        
        return command;
    }

    @Override
    public View workOn(HashExampleCommand command) {
        command.getPost().getResource().recalculateHashes();
        
        return Views.HASHEXAMPLE;
    }

    @Override
    public Errors getErrors() {
        return errors;
    }

    @Override
    public void setErrors(Errors errors) {
        this.errors = errors;
    }
    
}
