package org.bibsonomy.database.common.enums;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * CRIS entity type
 *
 * @author dzo
 */
public enum CRISEntityType {

	PUBLICATION(1),

	PERSON(2),

	PROJECT(3);

	private final int id;

	CRISEntityType(final int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	private static final Map<Class<? extends Linkable>, CRISEntityType> CLASS_CRIS_ENTITY_TYPE_MAP = new HashMap<>();

	static {
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Project.class, PROJECT);
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Post.class, PUBLICATION);
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Person.class, PERSON);
	}

	/**
	 * returns the cris entry type based on the class of the linkable
	 * @param clazz
	 * @return
	 */
	public static CRISEntityType getCRISEntityType(final Class<? extends Linkable> clazz) {
		if (CLASS_CRIS_ENTITY_TYPE_MAP.containsKey(clazz)) {
			return CLASS_CRIS_ENTITY_TYPE_MAP.get(clazz);
		}

		// second try
		// FIXME: only for the lacy loading classes in the person mapping :(
		for (Map.Entry<Class<? extends Linkable>, CRISEntityType> entry : CLASS_CRIS_ENTITY_TYPE_MAP.entrySet()) {
			if (entry.getKey().isAssignableFrom(clazz)) {
				return entry.getValue();
			}
		}

		throw new IllegalArgumentException("cris type not supported");
	}
}
