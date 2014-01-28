package org.bibsonomy.rest.strategy;

import java.util.StringTokenizer;

import org.bibsonomy.rest.enums.HttpMethod;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public interface ContextHandler {
	
	/**
	 * TODO: improve documentation
	 * 
	 * @param context
	 * @param urlTokens
	 * @param httpMethod
	 * @return TODO
	 */
	public Strategy createStrategy(Context context, StringTokenizer urlTokens, HttpMethod httpMethod);
}