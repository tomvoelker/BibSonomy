package org.bibsonomy.webapp.validation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.controller.actions.EditPublicationController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

/**
 * @author rja
 * @version $Id$
 */
public class PublicationValidatorTest {

	final PostValidator<BibTex> postValidator = new PostValidator<BibTex>();
	final EditPublicationController controller = new EditPublicationController();
	
	
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

	@Test
	public void testname() throws Exception {
		final List<Post<BibTex>> posts = new LinkedList<Post<BibTex>>();
		final Post<BibTex> post1 = new Post<BibTex>();
		final BibTex bibtex1 = new BibTex();
		bibtex1.setTitle("Foo");
		bibtex1.setYear("Foo");
		bibtex1.setEntrytype("Foo");
		bibtex1.setBibtexKey("Foo");
		bibtex1.setAuthor("Foo");
		post1.setResource(bibtex1);
		
		final Post<BibTex> post2 = new Post<BibTex>();
		final BibTex bibtex2 = new BibTex();
		bibtex2.setTitle("Bar");
		post2.setResource(bibtex2);
		
		final Post<BibTex> post3 = new Post<BibTex>();
		final BibTex bibtex3 = new BibTex();
		bibtex3.setTitle("Bar");
		post3.setResource(bibtex3);
		
		posts.add(post3);
		posts.add(post2);
		posts.add(post1);
		
		final MapBindingResult errors = new MapBindingResult(new HashMap(), "command");
		
		final PostListValidator valid = new PostListValidator();
		
		valid.validate(posts, errors);

		
		System.out.println("################## global errors");
		System.out.println(errors.getGlobalErrors());
		
	}

	
	public static class PostListValidator implements Validator {

		private final PublicationValidator postValidator = new PublicationValidator();
		
		@Override
		public boolean supports(Class clazz) {
			return true;
		}

		@Override
		public void validate(Object target, Errors errors) {
			if (target instanceof List) {
				final List<Post<BibTex>> posts = (List) target;

				final ListIterator<Post<BibTex>> listIterator = posts.listIterator();
				
				int postErrorCount = 0;
				
				while (listIterator.hasNext()) {
					final int index = listIterator.nextIndex();
					final Post<BibTex> post = listIterator.next();
					
					errors.pushNestedPath("list[" + index + "]");

					postValidator.validate(post.getResource(), errors);
					
					if (errors.hasFieldErrors("*")) postErrorCount++;
					
					errors.popNestedPath();
				}
				
				errors.reject("upload.posts.errors", 
						new Object[]{postErrorCount}, 
						"{0} posts could not be imported because of validation errors. See below.");
				
			}
			
		}
		
		
	}
	
	private EditPostCommand<BibTex> newCommand() {
		return controller.instantiateCommand();
	}
	private MapBindingResult validate(EditPostCommand<BibTex> command) {
		final MapBindingResult errors = new MapBindingResult(new HashMap(), "user");
		postValidator.validate(command, errors);
		return errors;
	}

}
