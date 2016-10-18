package org.bibsonomy.model.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * Model validator used by our logic
 *
 * @author dzo
 */
public class ModelValidator {
	
	private Map<Class<? extends Resource>, ResourceValidator<? extends Resource>> resourceValidators = new HashMap<>();
	
	/**
	 * adds the default {@link #resourceValidators}
	 */
	public ModelValidator() {
		this.resourceValidators.put(Bookmark.class, new BookmarkValidator());
		this.resourceValidators.put(BibTex.class, new PublicationValidator());
	}
	
	/**
	 * 
	 * @param post
	 * @return the errors while validating a normal post
	 */
	public List<ErrorMessage> validatePost(final Post<? extends Resource> post) {
		final List<ErrorMessage> errors = new LinkedList<>();
		final boolean resourcePresent = present(post.getResource());
		if (!resourcePresent) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("Resource");
			errors.add(errorMessage);
		}
		
		if (!present(post.getGroups())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("Groups");
			errors.add(errorMessage);
		}
		
		final User user = post.getUser();
		if (!present(user) || !present(user.getName())) {
			final MissingFieldErrorMessage errorMessage = new MissingFieldErrorMessage("User");
			errors.add(errorMessage);
		}
		
		if (resourcePresent) {
			errors.addAll(this.validateResource(post.getResource()));
		}
		return errors ;
	}

	/**
	 * @param resource
	 * @return
	 */
	private List<ErrorMessage> validateResource(final Resource resource) {
		return validateResourceInternal(resource);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) // XXX: generics :(
	private <T extends Resource> ResourceValidator<T> getFittingValidator(final T resource) {
		final Class<?> resourceClass = resource.getClass();
		ResourceValidator<? extends Resource> validator = this.resourceValidators.get(resourceClass);
		if (validator == null) {
			for (final Map.Entry<Class<? extends Resource>, ResourceValidator<? extends Resource>> entry : this.resourceValidators.entrySet()) {
				if (entry.getKey().isAssignableFrom(resourceClass)) {
					validator = entry.getValue();
					break;
				}
			}
			if (validator == null) {
				throw new UnsupportedResourceTypeException();
			}
		}
		return (ResourceValidator) validator;
	}
	
	private <R extends Resource> List<ErrorMessage> validateResourceInternal(final R resource) {
		final ResourceValidator<R> validator = getFittingValidator(resource);
		return validator.validateResource(resource);
	}

	/**
	 * @param resourceValidators the resourceValidators to set
	 */
	public void setResourceValidators(Map<Class<? extends Resource>, ResourceValidator<? extends Resource>> resourceValidators) {
		this.resourceValidators = resourceValidators;
	}
}
