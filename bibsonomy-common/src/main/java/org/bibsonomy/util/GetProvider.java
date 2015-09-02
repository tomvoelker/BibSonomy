package org.bibsonomy.util;

/**
 * interface for accessing things by an argument
 * 
 * @param <F> argument type typically {@link String}
 * @param <T> return type
 * 
 * @author jensi
 */
public interface GetProvider<F,T> {
	
	/**
	 * @param arg some argument
	 * @return something by an argument
	 */
	public T get(F arg);
}
