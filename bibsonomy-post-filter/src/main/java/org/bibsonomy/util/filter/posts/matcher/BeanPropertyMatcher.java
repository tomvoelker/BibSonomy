package org.bibsonomy.util.filter.posts.matcher;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.filter.posts.comparator.Comparator;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;


/**
 * Compares the value of the given property in the posts against the given value. 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BeanPropertyMatcher<T> implements Matcher {

	private String propertyName;
	private T propertyValue;
	private Comparator<T> comparator;

	public BeanPropertyMatcher() {
		// TODO Auto-generated constructor stub
	}
	
	public BeanPropertyMatcher(String propertyName, Comparator<T> comparator, T propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.comparator = comparator;
	}
	
	@Override
	public boolean matches(Post<? extends Resource> post) {
		final T property = getProperty(post);
		if (property == null) return false;
		return comparator.compare(property, propertyValue);
	}

	/**
	 * Extracts the property from the given post.
	 *  
	 * @param post
	 * @return
	 */
	private T getProperty(final Post<? extends Resource> post) {
		return (T) new BeanWrapperImpl(post).getPropertyValue(propertyName);
	}
	
	

	public String getPropertyName() {
		return propertyName;
	}

	@Required
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public T getPropertyValue() {
		return propertyValue;
	}

	@Required
	public void setPropertyValue(T propertyValue) {
		this.propertyValue = propertyValue;
	}

	public Comparator<T> getComparator() {
		return comparator;
	}

	@Required
	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	@Override
	public String toString() {
		return propertyName + " " + comparator + " '" + propertyValue + "'"; 
	}


}

