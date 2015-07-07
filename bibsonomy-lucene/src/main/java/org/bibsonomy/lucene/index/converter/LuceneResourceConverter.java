/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.lucene.index.converter;

import static org.bibsonomy.lucene.util.LuceneBase.CFG_FLDINDEX;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_FLDSTORE;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_FULLTEXT_FLAG;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_LIST_DELIMITER;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_LUCENENAME;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_PRIVATE_FLAG;
import static org.bibsonomy.lucene.util.LuceneBase.CFG_TYPEHANDLER;
import static org.bibsonomy.util.ValidationUtils.present;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.bibsonomy.es.ESConstants;
import org.bibsonomy.es.IndexType;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.param.typehandler.LuceneTypeHandler;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.GetProvider;
import org.bibsonomy.util.MapGetProvider;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.tex.TexDecode;

/**
 * class for converting bibsonomy post model objects to lucene documents
 * 
 * @author fei
 * 
 * @param <R> the resource to convert
 */
public class LuceneResourceConverter<R extends Resource> {
	private static final Log log = LogFactory.getLog(LuceneResourceConverter.class);

	/** property map for configuring and mapping post model properties to lucene fields and vice versa */
	private Map<String, Map<String, Object>> postPropertyMap;
	
	private ResourceFactory resourceFactory;
	
	private Class<R> resourceClass;
	
	private static final String PERSON_DELIMITER = " & ";
	private static final String NAME_PART_DELIMITER = " ; ";
	
	
	/**
	 * read property values from given lucene document and creates post model
	 * 
	 * @param doc
	 * @return the post representation of the lucene document
	 */
	public Post<R> writePost(final Document doc) {
		return writePost(new LuceneDocumentGetProvider(doc));
	}
	
	/**
	 * Reads property values from given {@link Map} and creates post model
	 * 
	 * @param map
	 * @return the post representation of the {@link Map}
	 */
	public Post<R> writePost(final Map<String, Object> map) {
		return writePost(new MapGetProvider<>(map));
	}

	private Object getPropertyValue(final String propertyName, final String propertyStr) {
		@SuppressWarnings("unchecked")
		final LuceneTypeHandler<Object> typeHandler = (LuceneTypeHandler<Object>) postPropertyMap.get(propertyName).get(CFG_TYPEHANDLER);
		if (typeHandler != null) {
			return typeHandler.setValue(propertyStr);
		}
		
		return propertyStr;
	}

	/**
	 * read property values from given object as defined in given propertyMap
	 * 
	 * @param post
	 * @param searchType 
	 * @return the lucene document representation of the post
	 */
	@SuppressWarnings("null")
	public Object readPost(final Post<R> post, final IndexType searchType) {
		Document luceneDocument = null;
		Map<String, Object> jsonDocument = null;
		
		// all fields are concatenated for full text search
		final StringBuilder fulltextField = new StringBuilder();
		// all private fields are concatenated for full text search
		final StringBuilder privateField = new StringBuilder();
		
		if (searchType == IndexType.ELASTICSEARCH) {
			jsonDocument = new HashMap<String, Object>();
		} else {
			luceneDocument = new Document();
		}
		/*
		 * cycle though all properties and store the corresponding
		 * values in the content hash map
		 */
		for (final String propertyName : postPropertyMap.keySet()) {
			/*
			 *  extract property value from object
			 */
			final String propertyValue = this.extractPropertyValue(post, propertyName);
			
			if (LuceneFieldNames.LAST_LOG_DATE.equals(propertyName)) {
				try {
					Long.parseLong(propertyValue);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(e);
				}
			}
			/*
			 * get index, store and name of lucene field
			 */
			final Index fieldIndex = this.getFieldIndexForProperty(propertyName);
			final Store fieldStore = this.getFieldStoreForProperty(propertyName);
			final String fieldName = this.getFieldName(propertyName);
			if (searchType == IndexType.ELASTICSEARCH) {
				if (!isPrivateProperty(propertyName)) {
					jsonDocument.put(fieldName, propertyValue);
				}
			} else {
				// add field to the lucene document
				luceneDocument.add(new Field(fieldName, propertyValue, fieldStore, fieldIndex));
			
				// TODO: only add non default values to these fields
				if (present(propertyValue)) {
					// add term to full text search field, if configured accordingly 
					if (this.isFulltextProperty(propertyName)) {
						fulltextField.append(CFG_LIST_DELIMITER);
						fulltextField.append(propertyValue);
					}
					// add term to private full text search field, if configured accordingly 
					if (this.isPrivateProperty(propertyName)) {
						privateField.append(CFG_LIST_DELIMITER);
						privateField.append(propertyValue);
					}
				}
			}
		}
		
		if (searchType == IndexType.ELASTICSEARCH) {
			if (BibTex.class.isAssignableFrom(this.resourceClass)) {
				final List<ResourcePersonRelation> rels = post.getResourcePersonRelations();
				jsonDocument.put(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, getNormalizedEntryType((Post<? extends BibTex>) post));
				setPersonFields(jsonDocument, rels);
			}
			return jsonDocument;
		}
		// store merged field
		luceneDocument.add(new Field(LuceneFieldNames.MERGED_FIELDS, decodeTeX(fulltextField.toString()), Field.Store.NO, Field.Index.ANALYZED));

		// store private field
		luceneDocument.add(new Field(LuceneFieldNames.PRIVATE_FIELDS, decodeTeX(privateField.toString()), Field.Store.YES, Field.Index.ANALYZED));
		
		// all done.
		return luceneDocument;
	}

	public void setPersonFields(Map<String, Object> jsonDocument, final List<ResourcePersonRelation> rels) {
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, null));
		jsonDocument.put(ESConstants.PERSON_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, null));
	}

	private String serializeMainNames(final List<ResourcePersonRelation> rels, PersonResourceRelationType type) {
		if (rels == null) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		for (ResourcePersonRelation rel : rels) {
			if ((type != null) && (rel.getRelationType() != type)) {
				continue;
			}
			final Person person = rel.getPerson();
			if (person == null) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(PERSON_DELIMITER);
			}
			if (ValidationUtils.present(person.getAcademicDegree())) {
				sb.append(prepareNamePart(person.getAcademicDegree()));
			}
			sb.append(NAME_PART_DELIMITER);
			final PersonName name = person.getMainName();
			if (ValidationUtils.present(name.getFirstName())) {
				sb.append(prepareNamePart(name.getFirstName()));
			}
			sb.append(NAME_PART_DELIMITER);
			if (ValidationUtils.present(name.getLastName())) {
				sb.append(prepareNamePart(name.getLastName()));
			}
		}
		return sb.toString();
	}
	
	private List<ResourcePersonRelation> readPersonRelationsFromIndex(GetProvider<String, Object> result) {
		final List<ResourcePersonRelation> rels = new ArrayList<>();
		
		String ids = (String) result.get(ESConstants.PERSON_ENTITY_IDS_FIELD_NAME);
		String[] parts = split(ids, " ");
		
		final int personIndexCtr[] = new int[PersonResourceRelationType.AUTHOR.values().length];
		for (int i = 0; i+1 < parts.length; i += 2) {
			final String relatorCodeStr = parts[i].trim();
			final PersonResourceRelationType role = PersonResourceRelationType.getByRelatorCode(relatorCodeStr);
			final int personIndex = personIndexCtr[role.ordinal()]++;
			final String id = parts[i+1].trim();
			ResourcePersonRelation rel = new ResourcePersonRelation();
			rel.setRelationType(role);
			rel.setPersonIndex(personIndex);
			rel.setPerson(new Person());
			rel.getPerson().setPersonId(id);
			rels.add(rel);
		}
		
		final String namesField = (String) result.get(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME);
		final String[] names = split(namesField,PERSON_DELIMITER);
		if (names.length != rels.size()) {
			throw new IllegalStateException();
		}
		for (int i = 0; i < names.length; ++i) {
			String[] nameParts = split(names[i], NAME_PART_DELIMITER);
			if (nameParts.length != 3) {
				throw new IllegalStateException(); 
			}
			Person p = rels.get(i).getPerson();
			PersonName mainName = new PersonName();
			if (present(nameParts[1])) {
				mainName.setFirstName(nameParts[1].trim());
			}
			if (present(nameParts[2])) {
				mainName.setLastName(nameParts[2].trim());
			}
			p.setMainName(mainName);
			if (present(nameParts[0])) {
				p.setAcademicDegree(nameParts[0].trim());
			}
		}
		
		return rels;
	}

	/**
	 * @param fieldName
	 * @param delimiter
	 * @return
	 */
	private String[] split(String fieldName, String delimiter) {
		String[] rVal = fieldName.split(delimiter);
		if ((rVal.length == 1) && (StringUtils.isEmpty(rVal[0]))) {
			return new String[0];
		}
		return rVal;
	}

	/**
	 * @param value
	 * @return
	 */
	private static String prepareNamePart(String value) {
		return value.trim().replace(PERSON_DELIMITER, " ").replace(NAME_PART_DELIMITER, " ");
	}

	private String serializePersonIds(final List<ResourcePersonRelation> rels, PersonResourceRelationType type) {
		if (rels == null) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		for (ResourcePersonRelation rel : rels) {
			if ((type != null) && (rel.getRelationType() != type)) {
				continue;
			}
			final Person person = rel.getPerson();
			if (person == null) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(rel.getRelationType().getRelatorCode()).append(" ");
			sb.append(person.getPersonId());
		}
		return sb.toString();
	}

	private String getNormalizedEntryType(final Post<? extends BibTex> post) {
		final BibTex bibtex = post.getResource();
		String normalizedEntryType = null;
		
		final String entryType = bibtex.getEntrytype();
		if (BibTexUtils.PHD_THESIS.equals(entryType)) {
			normalizedEntryType = NormalizedEntryTypes.phdthesis.name();
		}
		if (BibTexUtils.MASTERS_THESIS.equals(entryType)) {
			normalizedEntryType = NormalizedEntryTypes.master_thesis.name();
		}
		if (BibTexUtils.THESIS.equals(entryType)) {
			normalizedEntryType = NormalizedEntryTypes.bachelor_thesis.name();
		}
		
		if (normalizedEntryType != null) {
			String type = bibtex.getType();
			if (type != null) {
				type = type.toLowerCase().trim();
				if ((type.contains("master") || type.equals("mathesis"))) {
					normalizedEntryType = NormalizedEntryTypes.master_thesis.name();
				} else if (type.contains("bachelor")) {
					normalizedEntryType = NormalizedEntryTypes.bachelor_thesis.name();
				} else if (type.contains("habil")) {
					normalizedEntryType = NormalizedEntryTypes.habilitation.name();
				} else if (type.equals("candthesis")) {
					normalizedEntryType = NormalizedEntryTypes.candidate_thesis.name();
				}
			}
		} else {
			normalizedEntryType = bibtex.getEntrytype();
		}
		return normalizedEntryType;
	}
	
	private boolean isFulltextProperty(final String propertyName) {
		final Boolean isFulltext = (Boolean) postPropertyMap.get(propertyName).get(CFG_FULLTEXT_FLAG);
		return isFulltext == null ? false : isFulltext;
	}
	
	private boolean isPrivateProperty(final String propertyName) {
		final Boolean isPrivate = (Boolean) postPropertyMap.get(propertyName).get(CFG_PRIVATE_FLAG);
		return isPrivate == null ? false : isPrivate;
	}
	
	private String getFieldName(final String propertyName) {
		return (String) postPropertyMap.get(propertyName).get(CFG_LUCENENAME);
	}
	
	private Index getFieldIndexForProperty(final String propertyName) {
		final Index index = (Index) postPropertyMap.get(propertyName).get(CFG_FLDINDEX);
		if (index != null) {
			return index;
		}
		
		// default value TODO: config via spring
		return Field.Index.NOT_ANALYZED;
	}
	
	private Store getFieldStoreForProperty(final String propertyName) {
		final Store store = (Store) postPropertyMap.get(propertyName).get(CFG_FLDSTORE);
		if (store != null) {
			return store;
		}
		// default value TODO: config via spring
		return Field.Store.YES;
	}

	/**
	 * extracts property value from given object
	 * @param post the post
	 * @param propertyName the property of the post
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private String extractPropertyValue(final Post<R> post, final String propertyName) {
		try {
			final Map<String, Object> propertyInfoMap = postPropertyMap.get(propertyName);
			@SuppressWarnings("unchecked")
			final LuceneTypeHandler<Object> typeHandler = (LuceneTypeHandler<Object>)propertyInfoMap.get(CFG_TYPEHANDLER);
		
			final Object property = PropertyUtils.getProperty(post, propertyName);
		
			if (property != null) {
				// get the string value for the given object
				if (typeHandler != null) {
					// if a type handler is set, use the type handler for rendering the string
					return typeHandler.getValue(property);
				}
				// if no type handler is set use Object.toString()
				return property.toString();
			}
		} catch (final Exception e) {
			log.error("Error reading property '" + propertyName + "' from post object.", e);
		}
		return ""; // FIXME: configure default value field wise via spring
	}
	
	/**
	 * decode BibTeX characters to corresponding utf8 characters
	 * 
	 * @param input
	 * @return
	 */
	private String decodeTeX(final String input) {
		try {
			return TexDecode.decode(input);
		} catch (final IllegalStateException e) {
			log.debug("Error decoding TeX-string '" + input + "'");
			return input;
		}
	}
	
	private LucenePost<R> createEmptyPost() {
		final R resource = this.resourceFactory.<R>createResource(this.resourceClass);
		final User user = new User();
		final LucenePost<R> post = new LucenePost<R>();
		post.setResource(resource);
		post.setUser(user);
		post.getResource().recalculateHashes();
		return post;
	}
	
	/**
	 * @param postPropertyMap the postPropertyMap to set
	 */
	public void setPostPropertyMap(final Map<String, Map<String, Object>> postPropertyMap) {
		this.postPropertyMap = postPropertyMap;
	}

	/**
	 * @param resourceFactory the resourceFactory to set
	 */
	public void setResourceFactory(final ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(final Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @param result The result from elasticsearch search query
	 * @return Posts converted from Map
	 */
	public Post<R> writePost(GetProvider<String, Object> result) {
		// initialize 
		final LucenePost<R> post = this.createEmptyPost();
				
		// cycle though all properties and set the properties
		for (final String propertyName : postPropertyMap.keySet()) {
			// get index properties
			final String fieldName = this.getFieldName(propertyName);
			final String propertyStr = (String) result.get(fieldName); 
			if (!present(propertyStr)) {
				continue;
			}
			final Object propertyValue = this.getPropertyValue(propertyName, propertyStr);
			try {
				PropertyUtils.setNestedProperty(post, propertyName, propertyValue);
			} catch (final Exception e) {
				log.error("Error setting property " + propertyName + " to " + propertyValue.toString(), e);
			}
		}
		if (result.get(ESConstants.SYSTEM_URL_FIELD_NAME) != null) {
			String systemUrl = result.get(ESConstants.SYSTEM_URL_FIELD_NAME).toString();
			post.setSystemUrl(systemUrl);
			post.setResourcePersonRelations(readPersonRelationsFromIndex(result));
			for (ResourcePersonRelation rel : post.getResourcePersonRelations()) {
				rel.setPost((Post<? extends BibTex>) post);
			}
		}
		return post;
	}

	/**
	 * @param doc
	 * @param rel
	 */
	@Deprecated
	public void updatePersonRelation(Map<String, Object> doc, ResourcePersonRelationLogStub rel) {
		List<ResourcePersonRelation> relsBefore = readPersonRelationsFromIndex(new MapGetProvider<>(doc));
		updateRelationList(relsBefore, rel);
		setPersonFields(doc, relsBefore);
	}

	@Deprecated
	private void updateRelationList(List<ResourcePersonRelation> relsBefore, ResourcePersonRelationLogStub rel) {
		int indexOfRelInRelsBefore = getIndexOfRel(relsBefore, rel);
		if (indexOfRelInRelsBefore != -1) {
			if (rel.isDeleted()) {
				relsBefore.remove(indexOfRelInRelsBefore);
			} else {
				updatePersonRelation(relsBefore.get(indexOfRelInRelsBefore), rel);
			}
		} else {
			ResourcePersonRelation relNew = new ResourcePersonRelation();
			updatePersonRelation(relNew, rel);
			// relNew.setPerson(rel.getPerson());
			relsBefore.add(relNew);
		}
	}

	/**
	 * @param rel
	 * @return
	 */
	private void updatePersonRelation(final ResourcePersonRelation relNew, final ResourcePersonRelationLogStub relStub) {
		relNew.setPerson(new Person());
		relNew.getPerson().setPersonId(relStub.getPersonId());
		relNew.setChangedAt(relStub.getChangedAt());
		relNew.setChangedBy(relStub.getChangedBy());
		relNew.setPersonRelChangeId(relStub.getPersonRelChangeId());
		relNew.setPersonIndex(relStub.getPersonIndex());
		relNew.setQualifying(relStub.getQualifying());
		relNew.setRelationType(relStub.getRelationType());
	}

	private int getIndexOfRel(List<ResourcePersonRelation> relsBefore, ResourcePersonRelationLogStub rel) {
		for (int i = 0; i < relsBefore.size(); ++i) {
			ResourcePersonRelation relBefore = relsBefore.get(i);
			if ((relBefore.getRelationType() == rel.getRelationType()) && relBefore.getPerson().getPersonId().equals(rel.getPersonId()) && (relBefore.getPersonIndex() == rel.getPersonIndex())) {
				return i;
			}
		}
		return -1;
	}
}
