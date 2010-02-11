/**
 * 
 */
package org.bibsonomy.layout.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryImpl;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.JabRefPreferences;

import org.antlr.runtime.RecognitionException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * <strong>ModelConverterUtils</strong> - Can be used to encode and decode
 * JabRef's BibtexEntry to Post
 * 
 * @author Waldemar Biller <wbi@cs.uni-kassel.de>
 * 
 */
public class ModelConverterUtils {

	private static final Set<String> EXCLUDE_FIELDS = new HashSet<String>(
			Arrays.asList(new String[] { "abstract", // added separately
					"bibtexAbstract", // added separately
					"bibtexkey", "entrytype", // added at beginning of entry
					"misc", // contains several fields; handled separately
					"month", // handled separately
					"openURL", "simHash0", // not added
					"simHash1", // not added
					"simHash2", // not added
					"simHash3", // not added
					"description", "keywords", "comment", "id" }));

	private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

	/**
	 * Decode a List of Posts
	 * 
	 * @param posts
	 * @return
	 */
	public static List<BibtexEntry> decode(
			final List<Post<? extends Resource>> posts) {

		final List<BibtexEntry> entries = new ArrayList<BibtexEntry>();
		for (final Post<? extends Resource> post : posts)
			entries.add(ModelConverterUtils.decode(post));

		return entries;
	}

	/**
	 * Decode a Post to a BibtexEntry
	 * 
	 * @param post
	 * @return
	 */
	public static BibtexEntry decode(final Post<? extends Resource> post) {

		try {

			final BibtexEntry entry = new BibtexEntryImpl();

			final BibTex bibtex = (BibTex) post.getResource();
			final BeanInfo info = Introspector.getBeanInfo(bibtex.getClass());

			final PropertyDescriptor[] descriptors = info
					.getPropertyDescriptors();

			// iterate over all properties

			for (final PropertyDescriptor pd : descriptors) {

				final Method getter = pd.getReadMethod();

				// loop over all String attributes
				final Object o = getter.invoke(bibtex, (Object[]) null);

				if (String.class.equals(pd.getPropertyType())
						&& (o != null)
						&& !ModelConverterUtils.EXCLUDE_FIELDS.contains(pd
								.getName())) {
					final String value = ((String) o);
					if (ValidationUtils.present(value))
						entry.setField(pd.getName().toLowerCase(), value);
				}
			}

			if (ValidationUtils.present(bibtex.getEntrytype()))
				entry.setType(BibtexEntryType.getType(bibtex.getEntrytype()));

			if (ValidationUtils.present(bibtex.getMisc())
					|| ValidationUtils.present(bibtex.getMiscFields())) {

				// parse the misc fields and loop over them
				BibTexUtils.parseMiscField(bibtex);
				
				if (bibtex.getMiscFields() != null)
					for (final String key : bibtex.getMiscFields().keySet()) {
						if ("id".equals(key)) {
							// id is used by jabref
							entry.setField("misc_id", bibtex.getMiscField(key));
							continue;
						}

						if (key.startsWith("__")) // ignore fields starting with
													// __ - jabref uses them for
													// control
							continue;

						entry.setField(key, bibtex.getMiscField(key));
					}

			}

			if (ValidationUtils.present(bibtex.getMonth()))
				entry.setField("month", bibtex.getMonth());

			final String bibAbstract = bibtex.getAbstract();
			if (ValidationUtils.present(bibAbstract))
				entry.setField("abstract", bibAbstract);

			// set tags
			final Set<Tag> tags = post.getTags();
			final StringBuffer tagsBuffer = new StringBuffer();
			for (final Tag tag : tags)
				tagsBuffer.append(tag.getName()
						+ JabRefPreferences.getInstance().get(
								"groupKeywordSeparator"));
			tagsBuffer.delete(tagsBuffer.lastIndexOf(JabRefPreferences
					.getInstance().get("groupKeywordSeparator")), tagsBuffer
					.length());

			if (ValidationUtils.present(tagsBuffer.toString()))
				entry.setField("keywords", tagsBuffer.toString());

			// set groups - will be used in jabref when exporting to bibsonomy
			if (ValidationUtils.present(post.getGroups())) {
				final Set<Group> groups = post.getGroups();
				final StringBuffer groupsBuffer = new StringBuffer();
				for (final Group group : groups)
					groupsBuffer.append(group.getName() + " ");

				groupsBuffer.delete(groupsBuffer.lastIndexOf(" "), groupsBuffer
						.length());

				if (ValidationUtils.present(groupsBuffer.toString()))
					entry.setField("groups", groupsBuffer.toString());
			}

			// set comment + description
			if (ValidationUtils.present(post.getDescription())) {
				entry.setField("description", post.getDescription());
				entry.setField("comment", post.getDescription());
			}

			if (ValidationUtils.present(post.getDate())) {
				final SimpleDateFormat sdf = new SimpleDateFormat(
						ModelConverterUtils.DATE_FORMAT);
				entry.setField("timestamp", sdf.format(post.getDate()));
			}

			if (ValidationUtils.present(post.getUser()))
				entry.setField("bibsonomyUsername", post.getUser().getName());

			return entry;

		} catch (final IntrospectionException e) {
			System.err.println(e.getMessage());
		} catch (final IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (final IllegalAccessException e) {
			System.err.println(e.getMessage());
		} catch (final InvocationTargetException e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	/**
	 * Encode a BibtexEntry to a Post
	 * 
	 * @param entry
	 * @return
	 */
	public static Post<? extends Resource> encode(final BibtexEntry entry) {

		try {
			final Post<BibTex> post = new Post<BibTex>();
			final BibTex bibtex = new BibTex();
			final List<String> knownFields = new ArrayList<String>();

			final BeanInfo info = Introspector.getBeanInfo(bibtex.getClass());
			final PropertyDescriptor[] descriptors = info
					.getPropertyDescriptors();

			bibtex.setMisc("");

			// set all known properties of the BibTex
			for (final PropertyDescriptor pd : descriptors)
				if (ValidationUtils.present(entry.getField((pd.getName())))
						&& !ModelConverterUtils.EXCLUDE_FIELDS.contains(pd
								.getName())) {
					final Method m = pd.getWriteMethod();
					m.invoke(bibtex, entry.getField(pd.getName()));

					knownFields.add(pd.getName());
				}

			// Add not known Properties to misc
			for (final String field : entry.getAllFields())
				if (!knownFields.contains(field)
						&& !ModelConverterUtils.EXCLUDE_FIELDS.contains(field))
					bibtex.addMiscField(field, entry.getField(field));

			BibTexUtils.serializeMiscFields(bibtex);

			// set the key
			bibtex.setBibtexKey(entry.getCiteKey());
			bibtex.setEntrytype(entry.getType().getName().toLowerCase());

			// set the date of the post
			if (ValidationUtils.present(entry.getField("timestamp"))) {
				final SimpleDateFormat sdf = new SimpleDateFormat(
						ModelConverterUtils.DATE_FORMAT);
				post.setDate(sdf.parse(entry.getField("timestamp")));
			}

			if (ValidationUtils.present(entry.getField("abstract")))
				bibtex.setAbstract(entry.getField("abstract"));

			Set<Tag> tags = new HashSet<Tag>();
			tags.clear();
			if (ValidationUtils.present(entry.getField("keywords")))
				tags = TagUtils.parse(entry.getField("keywords").replaceAll(
						JabRefPreferences.getInstance().get(
								"groupKeyWordSeparator", ", "), " "));
			post.setTags(tags);

			// Set the groups
			if (ValidationUtils.present(entry.getField("groups"))) {

				final String[] groupsArray = entry.getField("groups")
						.split(" ");
				final Set<Group> groups = new HashSet<Group>();

				for (final String group : groupsArray)
					groups.add(new Group(group));

				post.setGroups(groups);
			}

			if ((entry.getField("description") != null)
					&& !"".equals(entry.getField("description")))
				post.setDescription(entry.getField("description"));

			if ((entry.getField("comment") != null)
					&& !"".equals(entry.getField("comment")))
				post.setDescription(entry.getField("comment"));

			if ((entry.getField("month") != null)
					&& !"".equals(entry.getField("month")))
				bibtex.setMonth(entry.getField("month"));

			post.setResource(bibtex);

			return post;

		} catch (final IntrospectionException e) {
			System.err.println(e.getMessage());
		} catch (final IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (final IllegalAccessException e) {
			System.err.println(e.getMessage());
		} catch (final InvocationTargetException e) {
			System.err.println(e.getMessage());
		} catch (final ParseException e) {
			System.err.println(e.getMessage());
		} catch (final RecognitionException e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	/**
	 * Encode a List of BibtexEntries
	 * 
	 * @param entries
	 * @return
	 */
	public static List<Post<? extends Resource>> encode(
			final List<BibtexEntry> entries) {

		final List<Post<? extends Resource>> posts = new ArrayList<Post<? extends Resource>>();
		for (final BibtexEntry entry : entries)
			posts.add(ModelConverterUtils.encode(entry));

		return posts;
	}
}
