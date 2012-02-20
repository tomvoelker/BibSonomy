package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.BibTeXHashExampleCommand;
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
public class BibTeXHashExampleController implements MinimalisticController<BibTeXHashExampleCommand>, ErrorAware {
    
    private Errors errors;

    @Override
    public BibTeXHashExampleCommand instantiateCommand() {
        
        final BibTeXHashExampleCommand command = new BibTeXHashExampleCommand();
        Post<BibTex> post = new Post<BibTex>();
        post.setResource(new BibTex());
        command.setPost(post);
        
        return command;
    }

    @Override
    public View workOn(BibTeXHashExampleCommand command) {
        command.getPost().getResource().recalculateHashes();
        
        return Views.BIBTEXHASHEXAMPLE;
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
