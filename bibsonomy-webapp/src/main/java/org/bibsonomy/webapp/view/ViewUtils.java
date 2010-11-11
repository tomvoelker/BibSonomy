package org.bibsonomy.webapp.view;

import java.util.Map;

import org.springframework.validation.BindingResult;


/**
 * @author dzo
 * @version $Id$
 */
public abstract class ViewUtils {

	/**
	 * Gets the BindingResult (containing errors) from the model.
	 * @param model
	 * @return the binding result
	 */
	public static BindingResult getBindingResult(final Map<String, Object> model){
		for (final Object key : model.keySet() ){
			if (((String)key).startsWith(BindingResult.MODEL_KEY_PREFIX)) {
				return (BindingResult) model.get(key);
			}
		}
		
		return null;
	}

}
