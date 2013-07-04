package org.bibsonomy.util.filter.posts.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;



/**
 * Updates each match in the given property to the new value.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class ReplacementPropertyModifier implements Modifier {

	private String propertyName;
	private Pattern matchingPattern;
	private String replacementValue;
	
	public ReplacementPropertyModifier() {
		// TODO Auto-generated constructor stub
	}
	
	public ReplacementPropertyModifier(final String propertyName, final Pattern matchingPattern, final String replacementValue) {
		super();
		this.propertyName = propertyName;
		this.matchingPattern = matchingPattern;
		this.replacementValue = replacementValue;
	}
	
	@Override
	public boolean updatePost(final Post<? extends Resource> post) {
		final BeanWrapper bw = new BeanWrapperImpl(post);
		/*
		 * check, if property is of type String
		 */
		final Object oldPropertyValue = bw.getPropertyValue(this.propertyName);
		if (oldPropertyValue instanceof String) {
			/*
			 * replaces the pattern, if value matches the pattern
			 */
			final String oldPropertyString = (String) oldPropertyValue;
			final Matcher matcher = this.matchingPattern.matcher(oldPropertyString);
			final String newPropertyValue = matcher.replaceAll(this.replacementValue);
			if (!newPropertyValue.equals(oldPropertyValue)) {
				// System.err.println(oldPropertyValue + " --> " + newPropertyValue);
				/*
				 * update!
				 */
				bw.setPropertyValue(this.propertyName, newPropertyValue);
				return true;
			}
		} else {
			throw new IllegalArgumentException("property " + propertyName + " is not of type String but only Strings are supported");
		}
		
		
		
		return false;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Required
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String toString() {
		return propertyName + " := s/" + matchingPattern + "/" + replacementValue + "/";
	}

	public Pattern getMatchingPattern() {
		return matchingPattern;
	}
	public void setMatchingPattern(Pattern matchingPattern) {
		this.matchingPattern = matchingPattern;
	}

	public String getReplacementValue() {
		return replacementValue;
	}
	public void setReplacementValue(String replacementValue) {
		this.replacementValue = replacementValue;
	}
	
}