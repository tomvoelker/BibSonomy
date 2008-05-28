package org.bibsonomy.importer.filter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Adds BibTeX fields.
 * 
 * @author rja
 * @version $Id$
 */
public class BibTeXFieldAdderFilter implements PostFilterChainElement {

	/**
	 * Fields and their values which should be added.
	 */
	private final HashMap<String, String> fields = new HashMap<String, String>();

	private static final String className = BibTeXFieldAdderFilter.class.getName();

	/** Looks for properties of common bibtex fields and adds them. 
	 * 
	 * To define a field and its value, add the property 
	 * {@link org.bibsonomy.importer.filter.BibTeXFieldAdderFilter}{@code .FIELD = VALUE}
	 * where FIELD is the name of an attribute of the class {@link BibTex}. Fields which do not
	 * have a corresponding setter method in {@link BibTex} are ignored.
	 * 
	 * 
	 * @param prop
	 * @throws IOException
	 */
	public BibTeXFieldAdderFilter(final Properties prop) throws IOException {
		/*
		 * load fields and their values
		 */
		final Set<Object> keys = prop.keySet();
		for (final Object key: keys) {
			final String string = (String) key;

			if (string.startsWith(className)) {
				/*
				 * found a field to be added
				 */
				fields.put(string.substring(className.length() + 1), prop.getProperty(string));
			}
		}
	}
	public void filterPost(final Post<BibTex> post) {
		/*
		 * Get infos about the BibTeX class.
		 */
		try {
			final BeanInfo info = Introspector.getBeanInfo(BibTex.class);

			final MethodDescriptor[] methods = info.getMethodDescriptors();

			final BibTex resource = post.getResource();
			/*
			 * iterate over all provided fields 
			 */
			for (final String key: fields.keySet()) {
				final String value = fields.get(key);
				callMethod(methods, "set" + key.substring(0, 1).toUpperCase() + key.substring(1), value, resource);

			} 
		} catch (final IntrospectionException e) {
			System.err.println(e);
		}

	}

	/** Calls the method <code>methodName</code> on <code>resource</code> supplying the arguments <code>methodArgument</code>.
	 * @param methods
	 * @param methodName
	 * @param methodArgument
	 * @param resource
	 */
	private void callMethod (final MethodDescriptor[] methods, final String methodName, final String methodArgument, final BibTex resource) {
		try {
			for (final MethodDescriptor methodDescriptor: methods) {
				if (methodDescriptor.getName().equals(methodName)) {
					final Method method = methodDescriptor.getMethod();
					method.invoke(resource, methodArgument);
				}
			}
		} catch (final Exception e) {
			System.err.println(e);
		}
	}


}
