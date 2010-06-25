package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.controller.actions.EditPublicationController;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author rja
 * @version $Id$
 */
public class PublicationValidatorTest {

	private static final PostValidator<BibTex> postValidator = new PostValidator<BibTex>();
	private static final EditPublicationController controller = new EditPublicationController();

	private static final String bibtexEntry1 = "@inproceedings{stumme05finite,\n" + 
	"        title = {A Finite State Model for On-Line Analytical Processing in Triadic Contexts.},\n" +
	"        author = {Gerd Stumme},\n" +
	"        booktitle = {Proceedings of the 3rd International Conference on Formal Concept Analysis},\n" +
	"        editor = {Bernhard Ganter and Robert Godin},\n" +
	"        pages = {315-328},\n" +
	"        publisher = {Springer},\n" +
	"        series = {Lecture Notes in Computer Science},\n" +
	"        volume = 3403,\n" +
	"        year = 2005,\n" +
	"        biburl = {http://www.bibsonomy.org/bibtex/2840d97c6873133e49d39b1207f762430/jaeschke},\n" +
	"	     keywords = {context fca iccs_example olap triadic trias_example},\n" +
	"        ee = {http://springerlink.metapress.com/openurl.asp?genre=article{\\&}issn=0302-9743{\\&}volume=3403{\\&}spage=315}, isbn = {3-540-24525-1}}\n";


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
		assertTrue(errors.hasErrors());
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
		 * errors only on tags (we check before binding ... and can't 
		 * bind them here) 
		 */
		errors = validate(command);
		assertEquals(0, errors.getGlobalErrorCount());
		assertEquals(1, errors.getErrorCount());
		assertEquals(1, errors.getFieldErrorCount("tags"));
		/*
		 * broken misc field: errors!
		 */
		bib.setMisc("foo = {bar");
		errors = validate(command);
		assertEquals(0, errors.getGlobalErrorCount());
		assertEquals(2, errors.getErrorCount());
		assertEquals(1, errors.getFieldErrorCount("post.resource.misc"));
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

		@SuppressWarnings("unchecked")
		final MapBindingResult errors = new MapBindingResult(new HashMap(), "command");

		final PostListValidator valid = new PostListValidator();

		valid.validate(posts, errors);
		
		/*
		 * FIXME: add Asserts!
		 */
	}

	
	@Test
	public void testMiscFieldValidation() throws Exception {
		final PostBibTeXParser parser = new PostBibTeXParser();
		parser.setDelimiter(" ");
		parser.setWhitespace("_");
		parser.setTryParseAll(true);

		/*
		 * FIXME: why aren't commas, etc. removed?
		 */
		/*
		 * Parse the BibTeX snippet	
		 */
		final List<Post<BibTex>> posts = parser.parseBibTeXPosts(bibtexEntry1);
		/*
		 * validate the post
		 */
		final BibTex resource = posts.get(0).getResource();
		@SuppressWarnings("unchecked")
		final MapBindingResult errors = new MapBindingResult(new HashMap(), "bibtex");
		
		ValidationUtils.invokeValidator(new PublicationValidator(), resource, errors);
		
		/*
		 * The misc field "ee" contains some {} which should not cause 
		 * validation errors.
		 */
		assertFalse(errors.hasErrors());		
	}


	public static class PostListValidator implements Validator {
		private final PublicationValidator postValidator = new PublicationValidator();

		@SuppressWarnings("unchecked")
		@Override
		public boolean supports(Class clazz) {
			return true;
		}

		
		@Override
		public void validate(Object target, Errors errors) {
			if (target instanceof List<?>) {
				@SuppressWarnings("unchecked")
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
		@SuppressWarnings("unchecked")
		final MapBindingResult errors = new MapBindingResult(new HashMap(), "user");
		postValidator.validate(command, errors);
		return errors;
	}
}
