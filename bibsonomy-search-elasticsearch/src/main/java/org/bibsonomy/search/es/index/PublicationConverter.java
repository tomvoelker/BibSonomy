/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.index;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.ESConstants.Fields.Publication;
import org.bibsonomy.util.ValidationUtils;

/**
 * converts a {@link BibTex} to the ElasticSearch representation and vice versa
 *
 * @author dzo
 * @author jensi
 */
public class PublicationConverter extends ResourceConverter<BibTex> {
	private static final Log log = LogFactory.getLog(PublicationConverter.class);
	
	private static final String PERSON_DELIMITER = " & ";
	private static final String NAME_PART_DELIMITER = " ; ";
	
	static interface PersonNameSetter {
		public void setPersonNames(final BibTex publication, final List<PersonName> personNames);
	}
	
	private static final PersonNameSetter AUTHOR_NAME_SETTER = new PersonNameSetter() {
		@Override
		public void setPersonNames(BibTex publication, List<PersonName> personNames) {
			publication.setAuthor(personNames);
		}
	};
	
	private static final PersonNameSetter EDITOR_NAME_SETTER = new PersonNameSetter() {
		@Override
		public void setPersonNames(BibTex publication, List<PersonName> personNames) {
			publication.setEditor(personNames);
		}
	};
	
	/**
	 * @param systemURI
	 */
	public PublicationConverter(URI systemURI) {
		super(systemURI);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#createNewResource()
	 */
	@Override
	protected BibTex createNewResource() {
		return new BibTex();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertResourceInternal(org.bibsonomy.model.Resource, java.util.Map)
	 */
	@Override
	protected void convertResourceInternal(BibTex publication, Map<String, Object> source) {
		publication.setAddress((String) source.get(Fields.Publication.ADDRESS));
		publication.setAnnote((String) source.get(Fields.Publication.ANNOTE));
		publication.setKey((String) source.get(Fields.Publication.KEY));
		publication.setAbstract((String) source.get(Fields.Publication.ABSTRACT));
		publication.setBibtexKey((String) source.get(Fields.Publication.BIBTEXKEY));
		publication.setBooktitle((String) source.get(Fields.Publication.BOOKTITLE));
		publication.setChapter((String) source.get(Fields.Publication.CHAPTER));
		publication.setCrossref((String) source.get(Fields.Publication.CROSSREF));
		publication.setDay((String) source.get(Fields.Publication.DAY));
		publication.setEdition((String) source.get(Fields.Publication.EDITION));
		
		setPersonNames(Fields.Publication.EDITORS, Fields.Publication.EDITOR, EDITOR_NAME_SETTER, publication, source);
		setPersonNames(Fields.Publication.AUTHORS, Fields.Publication.AUTHOR, AUTHOR_NAME_SETTER, publication, source);
		
		publication.setEntrytype((String) source.get(Publication.ENTRY_TYPE));
		publication.setHowpublished((String) source.get(Publication.HOWPUBLISHED));
		publication.setInstitution((String) source.get(Publication.INSTITUTION));
		publication.setJournal((String) source.get(Publication.JOURNAL));
		publication.setMisc((String) source.get(Publication.MISC));
		publication.setMonth((String) source.get(Publication.MONTH));
		publication.setNote((String) source.get(Publication.NOTE));
		publication.setNumber((String) source.get(Publication.NUMBER));
		publication.setOrganization((String) source.get(Publication.ORGANIZATION));
		publication.setPages((String) source.get(Publication.PAGES));
		publication.setPrivnote((String) source.get(Publication.PRIVNOTE));
		publication.setPublisher((String) source.get(Publication.PUBLISHER));
		publication.setSchool((String) source.get(Publication.SCHOOL));
		publication.setSeries((String) source.get(Publication.SERIES));
		publication.setType((String) source.get(Publication.TYPE));
		publication.setUrl((String) source.get(Publication.URL));
		publication.setVolume((String) source.get(Publication.VOLUME));
		publication.setYear((String) source.get(Publication.YEAR));
	}

	/**
	 * @param publication
	 * @param source
	 */
	private static void setPersonNames(final String fieldName, @Deprecated final String fallbackFieldName, final PersonNameSetter personNameSetter, BibTex publication, Map<String, Object> source) {
		final Object rawPersonNamesFieldValue = source.get(fieldName);
		if (rawPersonNamesFieldValue == null) {
			// TODO: remove fallback raw field with 3.6
			final Object fallbackRawFieldValue = source.get(fallbackFieldName);
			if (fallbackRawFieldValue instanceof List) {
				@SuppressWarnings("unchecked")
				final List<String> personNamesList = (List<String>) rawPersonNamesFieldValue;
				final String personNamesString = org.bibsonomy.util.StringUtils.implodeStringCollection(personNamesList, PersonNameUtils.PERSON_NAME_DELIMITER);
				personNameSetter.setPersonNames(publication, PersonNameUtils.discoverPersonNamesIgnoreExceptions(personNamesString));
			} else if (fallbackRawFieldValue != null) {
				log.warn(fieldName + " field was '" + fallbackRawFieldValue + "' of type '" + fallbackRawFieldValue.getClass().getName() + "'");
			}
		} else if (rawPersonNamesFieldValue instanceof List) {
			@SuppressWarnings("unchecked")
			final List<Map<String, String>> personNamesList = (List<Map<String, String>>) rawPersonNamesFieldValue;
			final StringBuilder personNameStringBuilder = new StringBuilder();
			
			final Iterator<Map<String, String>> personNameIterator = personNamesList.iterator();
			while (personNameIterator.hasNext()) {
				final Map<String, String> embeddedObject = personNameIterator.next();
				final String personname = embeddedObject.get(Fields.Publication.PERSON_NAME);
				personNameStringBuilder.append(personname);
				if (personNameIterator.hasNext()) {
					personNameStringBuilder.append(PersonNameUtils.PERSON_NAME_DELIMITER);
				}
			}
			personNameSetter.setPersonNames(publication, PersonNameUtils.discoverPersonNamesIgnoreExceptions(personNameStringBuilder.toString()));
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertPostInternal(java.util.Map, org.bibsonomy.model.Post)
	 */
	@Override
	protected void convertPostInternal(Map<String, Object> source, Post<BibTex> post) {
		post.setResourcePersonRelations(readPersonRelationsFromIndex(source));
		for (final ResourcePersonRelation rel : post.getResourcePersonRelations()) {
			rel.setPost(post);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertResource(java.util.Map, org.bibsonomy.model.Resource)
	 */
	@Override
	protected void convertResource(Map<String, Object> jsonDocument, BibTex resource) {
		jsonDocument.put(Fields.Publication.ADDRESS, resource.getAddress());
		jsonDocument.put(Fields.Publication.ANNOTE, resource.getAnnote());
		jsonDocument.put(Fields.Publication.KEY, resource.getKey());
		jsonDocument.put(Fields.Publication.ABSTRACT, resource.getAbstract());
		jsonDocument.put(Fields.Publication.BIBTEXKEY, resource.getBibtexKey());
		jsonDocument.put(Fields.Publication.BOOKTITLE, resource.getBooktitle());
		jsonDocument.put(Fields.Publication.CHAPTER, resource.getChapter());
		jsonDocument.put(Fields.Publication.CROSSREF, resource.getCrossref());
		jsonDocument.put(Fields.Publication.DAY, resource.getDay());
		jsonDocument.put(Fields.Publication.EDITION, resource.getEdition());
		
		final List<PersonName> editors = resource.getEditor();
		if (present(editors)) {
			jsonDocument.put(Fields.Publication.EDITORS, convertPersonNames(editors));
		}
		
		final List<PersonName> authors = resource.getAuthor();
		if (present(authors)) {
			jsonDocument.put(Fields.Publication.AUTHORS, convertPersonNames(authors));
		}
		
		jsonDocument.put(Publication.ENTRY_TYPE, resource.getEntrytype());
		jsonDocument.put(Fields.Publication.HOWPUBLISHED, resource.getHowpublished());
		
		jsonDocument.put(Fields.Publication.INSTITUTION, resource.getInstitution());
		jsonDocument.put(Fields.Publication.JOURNAL, resource.getJournal());
		
		/*
		 * insert misc field
		 * parse it and insert all misc fields as separate fields in elasticsearch
		 */
		jsonDocument.put(Fields.Publication.MISC, resource.getMisc());
		if (!resource.isMiscFieldParsed()) {
			try {
				resource.parseMiscField();
			} catch (final InvalidModelException e) {
				log.warn("parsing misc field failed", e);
			}
		}
		
		final Map<String, String> parsedMiscField = resource.getMiscFields();
		if (present(parsedMiscField)) {
			for (final Entry<String, String> miscFieldEntry : parsedMiscField.entrySet()) {
				String key = normKey(miscFieldEntry.getKey());
				
				// check if the key was already added before;
				if (jsonDocument.containsKey(key) || Fields.SPECIAL_FIELDS.contains(key)) {
					key = "misc_" + key;
				}
				
				jsonDocument.put(key, miscFieldEntry.getValue());
			}
		}
		jsonDocument.put(Fields.Publication.MONTH, resource.getMonth());
		jsonDocument.put(Fields.Publication.NOTE, resource.getNote());
		jsonDocument.put(Fields.Publication.NUMBER, resource.getNumber());
		jsonDocument.put(Fields.Publication.ORGANIZATION, resource.getOrganization());
		jsonDocument.put(Fields.Publication.PAGES, resource.getPages());
		
		jsonDocument.put(Fields.Publication.PRIVNOTE, resource.getPrivnote());
		jsonDocument.put(Fields.Publication.PUBLISHER, resource.getPublisher());
		jsonDocument.put(Fields.Publication.SCHOOL, resource.getSchool());
		jsonDocument.put(Fields.Publication.SERIES, resource.getSeries());
		
		jsonDocument.put(Fields.Publication.TYPE, resource.getType());
		jsonDocument.put(Fields.Publication.URL, resource.getUrl());
		jsonDocument.put(Fields.Publication.VOLUME, resource.getVolume());
		
		jsonDocument.put(Publication.YEAR, resource.getYear());
	}
	
	/**
	 * @param key
	 * @return
	 */
	private static String normKey(String key) {
		// norm the key
		key = key.toLowerCase();
		return key.replaceAll("[^a-z0-9]", "");
	}

	/**
	 * @param author
	 * @return
	 */
	private static List<Map<String, String>> convertPersonNames(List<PersonName> persons) {
		final List<Map<String, String>> serializedPersonNames = new LinkedList<>();
		
		for (final PersonName person : persons) {
			serializedPersonNames.add(Collections.singletonMap(Publication.PERSON_NAME, PersonNameUtils.serializePersonName(person)));
		}
		
		return serializedPersonNames;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertPostInternal(java.util.Map, org.bibsonomy.model.Post)
	 */
	@Override
	protected void convertPostInternal(final Post<BibTex> post, final Map<String, Object> jsonDocument) {
		jsonDocument.put(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME, getNormalizedEntryType(post));
		
		final List<ResourcePersonRelation> rels = post.getResourcePersonRelations();
		this.updateDocumentWithPersonRelation(jsonDocument, rels);
	}

	/**
	 * @param jsonDocument
	 * @param rels
	 */
	public void updateDocumentWithPersonRelation(final Map<String, Object> jsonDocument, final List<ResourcePersonRelation> rels) {
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, PersonResourceRelationType.AUTHOR));
		jsonDocument.put(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME, serializeMainNames(rels, null));
		jsonDocument.put(Fields.PERSON_ENTITY_IDS_FIELD_NAME, serializePersonIds(rels, null));
	}
	
	private static String getNormalizedEntryType(final Post<? extends BibTex> post) {
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
	
	private static String serializeMainNames(final List<ResourcePersonRelation> rels, PersonResourceRelationType type) {
		if (rels == null) {
			return null;
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
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}
	
	private static String serializePersonIds(final List<ResourcePersonRelation> rels, PersonResourceRelationType type) {
		if (rels == null) {
			return null;
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
			sb.append(getRelatorCodeOrEmptyString(rel)).append(" ");
			sb.append(person.getPersonId());
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}
	
	private static List<ResourcePersonRelation> readPersonRelationsFromIndex(Map<String, Object> result) {
		final List<ResourcePersonRelation> rels = new ArrayList<>();
		
		final String ids = (String) result.get(Fields.PERSON_ENTITY_IDS_FIELD_NAME);
		if (StringUtils.isEmpty(ids)) {
			return rels;
		}
		String[] parts = split(ids, " ");
		
		final int personIndexCtr[] = new int[PersonResourceRelationType.values().length];
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
		final String[] names = split(namesField, PERSON_DELIMITER);
		if (names.length != rels.size()) {
			throw new IllegalStateException();
		}
		
		for (int i = 0; i < names.length; ++i) {
			String[] nameParts = split(names[i], NAME_PART_DELIMITER);
			if (nameParts.length < 3) {
				throw new IllegalStateException(); 
			}
			Person p = rels.get(i).getPerson();
			PersonName mainName = buildNameFromParts(nameParts, 1);
			p.setMainName(mainName);
			if (present(nameParts[0])) {
				p.setAcademicDegree(nameParts[0].trim());
			}
			if (nameParts.length % 2 != 1) {
				log.error("wrong number of name parts found for person " + p.getPersonId() + ": " + nameParts);
			} else {
				for (int namePartI = 3; namePartI < nameParts.length; namePartI += 2) {
					p.addName(buildNameFromParts(nameParts, namePartI));
				}
			}
		}
		return rels;
	}
	
	private static PersonName buildNameFromParts(String[] nameParts, int firstPartIndex) {
		PersonName name = new PersonName();
		if (present(nameParts[1])) {
			name.setFirstName(nameParts[firstPartIndex].trim());
		}
		if (present(nameParts[2])) {
			name.setLastName(nameParts[firstPartIndex+1].trim());
		}
		return name;
	}
	
	/**
	 * @param fieldName
	 * @param delimiter
	 * @return
	 */
	private static String[] split(String fieldName, String delimiter) {
		if (fieldName == null) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		String[] rVal = fieldName.split(delimiter);
		if ((rVal.length == 1) && (StringUtils.isEmpty(rVal[0]))) {
			return new String[0];
		}
		return rVal;
	}
	
	private static String getRelatorCodeOrEmptyString(ResourcePersonRelation rel) {
		PersonResourceRelationType type = rel.getRelationType();
		if (type == null) {
			log.error("relation without relatorcode: " + rel);
			return "";
		}
		return type.getRelatorCode();
	}
	
	/**
	 * @param value
	 * @return
	 */
	private static String prepareNamePart(String value) {
		return value.trim().replace(PERSON_DELIMITER, " ").replace(NAME_PART_DELIMITER, " ");
	}

}
