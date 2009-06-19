package org.bibsonomy.util.filter.posts.modifier;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Required;


/**
 * Updates the given property to the new value.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class PropertyModifier<T> implements Modifier {

	private String propertyName;
	private T newPropertyValue;
	
	public PropertyModifier() {
		// TODO Auto-generated constructor stub
	}
	
	public PropertyModifier(String propertyName, T newPropertyValue) {
		super();
		this.propertyName = propertyName;
		this.newPropertyValue = newPropertyValue;
	}

	@Override
	public boolean updatePost(Post<? extends Resource> post) {
		final BeanWrapper bw = new BeanWrapperImpl(post);
		/*
		 * check, if new value differs from old value
		 */
		final Object oldPropertyValue = bw.getPropertyValue(propertyName);
		if (!newPropertyValue.equals(oldPropertyValue)) {
//			System.err.println(oldPropertyValue + " --> " + newPropertyValue);
			/*
			 * update!
			 */
			bw.setPropertyValue(propertyName, newPropertyValue);
			return true;
		}
		return false;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Required
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public T getNewPropertyValue() {
		return newPropertyValue;
	}

	@Required
	public void setNewPropertyValue(T newPropertyValue) {
		this.newPropertyValue = newPropertyValue;
	}
	
	@Override
	public String toString() {
		return propertyName + " := " + "'" + newPropertyValue + "'";
	}
	
	
}

