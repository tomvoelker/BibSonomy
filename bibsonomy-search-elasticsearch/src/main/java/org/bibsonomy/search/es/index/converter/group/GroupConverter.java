package org.bibsonomy.search.es.index.converter.group;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.search.util.Converter;

/**
 * converter for {@link Group}s
 *
 * @author dzo
 */
public class GroupConverter implements Converter<Group, Map<String, Object>, Object> {

	private static final Log LOG = LogFactory.getLog(GroupConverter.class);

	@Override
	public Map<String, Object> convert(final Group group) {
		final HashMap<String, Object> document = new HashMap<>();
		final URL homepage = group.getHomepage();
		if (present(homepage)) {
			document.put(GroupFields.HOMEPAGE, homepage.toString());
		}

		document.put(GroupFields.NAME, group.getName());
		document.put(GroupFields.REALNAME, group.getRealname());
		document.put(GroupFields.EXTERNAL_ID, group.getExternalId());
		document.put(GroupFields.ORGANIZATION, group.isOrganization());

		final Group parentGroup = group.getParent();
		if (present(parentGroup)) {
			document.put(GroupFields.PARENT_NAME, parentGroup.getName());
		}

		return document;
	}

	@Override
	public Group convert(final Map<String, Object> source, final Object options) {
		final Group group = new Group();

		group.setRealname((String) source.get(GroupFields.REALNAME));
		group.setName((String) source.get(GroupFields.NAME));
		group.setExternalId((String) source.get(GroupFields.EXTERNAL_ID));
		group.setOrganization((Boolean) source.get(GroupFields.ORGANIZATION));

		final Object homepage = source.get(GroupFields.HOMEPAGE);
		if (present(homepage)) {
			try {
				group.setHomepage(new URL((String) homepage));
			} catch (MalformedURLException e) {
				LOG.error("error converting group", e);
			}
		}

		if (source.containsKey(GroupFields.PARENT_NAME)) {
			final Group parentGroup = new Group();
			parentGroup.setName((String) source.get(GroupFields.PARENT_NAME));
			group.setParent(parentGroup);
		}

		return group;
	}
}
