package org.bibsonomy.webapp.validation;

import java.util.HashMap;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.controller.actions.PostPublicationController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.MapBindingResult;

/**
 * @author rja
 * @version $Id$
 */
public class PostPublicationValidatorTest {

	final PostPublicationValidator validator = new PostPublicationValidator();
	final PostPublicationController controller = new PostPublicationController();
	
	
	@Test
	public void testValidateResourceErrorsBibTex() {
		final EditPostCommand<BibTex> command = newCommand();
		MapBindingResult errors;
		final Post<BibTex> post = command.getPost();
		final BibTex bib = post.getResource();
		
		errors = validate(command);
		/*
		 * no author or editor: errors!
		 */
		Assert.assertTrue(errors.hasErrors());
		/*
		 * set fields such that no errors occur
		 */
		bib.setTitle("Title");
		bib.setAuthor("Firstname Lastname");
		bib.setEntrytype("proceedings");
		bib.setBibtexKey("key");
		bib.setYear("1999");
		post.addTag("tag");
		/*
		 * errors only on tags/title (we check before binding ... and can't 
		 * bind them here) 
		 */
		errors = validate(command);
		Assert.assertEquals(0, errors.getGlobalErrorCount());
		Assert.assertEquals(2, errors.getErrorCount());
		Assert.assertEquals(1, errors.getFieldErrorCount("post.resource.title"));
		Assert.assertEquals(1, errors.getFieldErrorCount("tags"));
		/*
		 * broken misc field: errors!
		 */
		bib.setMisc("foo = {bar");
		errors = validate(command);
		Assert.assertEquals(0, errors.getGlobalErrorCount());
		Assert.assertEquals(3, errors.getErrorCount());
		Assert.assertEquals(1, errors.getFieldErrorCount("post.resource.misc"));
	}


	private EditPostCommand<BibTex> newCommand() {
		return controller.instantiateCommand();
	}
	private MapBindingResult validate(EditPostCommand<BibTex> command) {
		final MapBindingResult errors = new MapBindingResult(new HashMap(), "user");
		validator.validate(command, errors);
		return errors;
	}

}
