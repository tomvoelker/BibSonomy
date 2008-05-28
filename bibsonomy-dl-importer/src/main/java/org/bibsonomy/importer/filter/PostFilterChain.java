package org.bibsonomy.importer.filter;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Loads filters specified in the configuration and calls them. 
 *  
 * @author rja
 * @version $Id$
 */
public class PostFilterChain implements PostFilterChainElement {


	private final List<PostFilterChainElement> filters = new LinkedList<PostFilterChainElement>();

	public PostFilterChain(final Properties prop) throws ClassNotFoundException {
		/*
		 * maximal number of filters to look for ... default: 10
		 */
		int maxFilters = 10;
		try {
			maxFilters = Integer.parseInt(PostFilterChain.class.getName() + ".maxFilters");
		} catch (final NumberFormatException e) {
			maxFilters = 10;
		}
		
		/*
		 * iterate over filters
		 */
		for (int i=1; i<=maxFilters; i++) {
			final String key = PostFilterChain.class.getName() + ".filter" + i;
			if (prop.containsKey(key)) {
				/*
				 * filter definition found
				 */
				final String className = prop.getProperty(key);
				final PostFilterChainElement filter = loadFilter(className, prop);
				filters.add(filter);
			}
		}
	}

	/** Loads a filter with the given name. If the filter has a constructor, which
	 * accepts Properties, that constructor will be called, otherwise the no-arg
	 * constructor.
	 * 
	 * @param className - name of the filter's class.
	 * @param prop - properties for configuration
	 * @return An instance of the filter. 
	 * @throws ClassNotFoundException
	 */
	private PostFilterChainElement loadFilter(final String className, final Properties prop) throws ClassNotFoundException {
		final Class<?> clazz = PostFilterChain.class.getClassLoader().loadClass(className);
		
		try {
			try {
				/*
				 * Check for constructor accepting Properties.
				 */
				final Constructor<?> cons = clazz.getConstructor(Properties.class);
				return (PostFilterChainElement) cons.newInstance(prop);
			} catch (final NoSuchMethodException e) {
				/*
				 * Check for no-arg constructor.
				 */
				final Constructor<?> cons = clazz.getConstructor();
				return (PostFilterChainElement) cons.newInstance();
			}
			
		} catch (final Exception e) {
			System.err.println(e);
			throw new ClassNotFoundException();
		}
	}

	/** 
	 * Calls filterPost on all configured filters.
	 * 
	 * @see org.bibsonomy.importer.filter.PostFilterChainElement#filterPost(org.bibsonomy.model.Post)
	 */
	public void filterPost(final Post<BibTex> post) {
		for (final PostFilterChainElement filter: filters) {
			filter.filterPost(post);
		}
	}

	/**
	 * Adds a filter to the filter chain.
	 * 
	 * @param filter
	 */
	public void addFilter(final PostFilterChainElement filter) {
		filters.add(filter);
	}

}
