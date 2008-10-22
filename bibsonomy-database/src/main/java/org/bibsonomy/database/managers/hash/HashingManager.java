package org.bibsonomy.database.managers.hash;

import java.util.Map;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public abstract class HashingManager {

	private static final String FALSE = "0";
	private static final String TRUE = "1";

	/**
	 * generates a hash string for a HashElement
	 * 
	 * @param <T>
	 * @param element
	 *            to hash
	 * @return hash string
	 */
	protected <T extends Resource> String elementToHashString(HashElement<Post<T>, ? extends GenericParam> element) {
		if (getMap().containsKey(elementToHash(element).toString())) {
			/*
			 * 
			 */
		}
		return elementToHash(element).toString();
	}

	/**
	 * generates a hash stringbuilder for a HashElement
	 * 
	 * @param <T>
	 * @param element
	 *            to hash
	 * @return hash stringbuilder
	 */
	protected <T extends Resource> StringBuilder elementToHash(HashElement<Post<T>, ? extends GenericParam> element) {

		StringBuilder sb = new StringBuilder();
		addBoolean(sb, element.isDescription());
		addBoolean(sb, element.isTagIndex());
		addBoolean(sb, element.isHash());
		addBoolean(sb, element.isSearch());
		addBoolean(sb, element.isRequestedUserName());
		addBoolean(sb, element.isNumSimpleConceptsOverNull());
		addBoolean(sb, element.isNumTransitiveConceptsOverNull());
		sb.append(element.getGroupingEntity());

		/*
		 * not neccessary to distinguish between the elements
		 */
		// addBoolean(sb, element.isDate());
		// addBoolean(sb, element.isRequestedGroupName());
		// addBoolean(sb, element.isNumSimpleTagsOverNull());
		sb.append(additionalElementToHash(element));

		return sb;
	}

	/**
	 * generates a hash string for an incoming param
	 * 
	 * @param param
	 *            incoming param
	 * @return hash string
	 */
	protected String paramToHashString(GenericParam param) {
		return paramToHash(param).toString();
	}

	/**
	 * generates a hash stringbuilder for an incoming param
	 * 
	 * @param param
	 * @return hash stringbuilder
	 */
	protected StringBuilder paramToHash(GenericParam param) {
		StringBuilder sb = new StringBuilder();

		addBoolean(sb, ValidationUtils.present(param.getDescription()));
		addBoolean(sb, ValidationUtils.present(param.getTagIndex()));
		addBoolean(sb, ValidationUtils.present(param.getHash()));
		addBoolean(sb, ValidationUtils.present(param.getSearch()));
		addBoolean(sb, ValidationUtils.present(param.getRequestedUserName()));
		sb.append(present(param.getNumSimpleConcepts()));
		sb.append(present(param.getNumTransitiveConcepts()));
		sb.append(param.getGrouping());

		/*
		 * not neccessary to distinguish between the elements
		 */

		// addBoolean(sb, ValidationUtils.present(param.getDate()));
		// addBoolean(sb,
		// ValidationUtils.present(param.getRequestedGroupName()));
		// sb.append(present(param.getNumSimpleTags()));
		sb.append(additionalParamToHash(param));

		return sb;
	}

	/**
	 * appends boolean values as 0 or 1 to the current hash string
	 */
	protected void addBoolean(StringBuilder sb, boolean b) {
		if (b) {
			sb.append(TRUE);
		} else {
			sb.append(FALSE);
		}
	}

	/**
	 * appends 1 to the current hash string, if an integer is higher than 0, and
	 * 0 otherwise
	 */
	protected String present(Integer number) {
		if (number > 0) {
			return TRUE;
		}
		return FALSE;
	}

	/**
	 * returns the hash element suitable for the incoming param
	 * 
	 * @param param
	 *            incoming param
	 * @return hash element
	 */
	public HashElement<? extends Post<? extends Resource>, ? extends GenericParam> getMapping(GenericParam param) {
		return getMap().get(paramToHashString(param));
		// HashElement elem = getMap().get(paramToHashString(param));
		// if (elem != null && elem.isOrderValid(param.getOrder())) {
		// return elem;
		// }
		// return null;
	}

	/**
	 * subclasses have to provide a map to store hash elements with their
	 * according hash
	 * 
	 * @return map
	 */
	protected abstract Map<String, HashElement<? extends Post<? extends Resource>, ? extends GenericParam>> getMap();

	/**
	 * implement these methods, if a subclass handles specialized hash elements
	 * with additional attributes
	 */
	protected abstract StringBuilder additionalElementToHash(HashElement element);

	protected abstract StringBuilder additionalParamToHash(GenericParam param);
}
