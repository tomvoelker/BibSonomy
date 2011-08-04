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
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.param.typehandler.LuceneTypeHandler;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.util.tex.TexDecode;

/**
 * class for converting bibsonomy post model objects to lucene documents
 * 
 * @author fei
 * @version $Id$
 * 
 * @param <R> the resource to convert
 */
public class LuceneResourceConverter<R extends Resource> {
	private static final Log log = LogFactory.getLog(LuceneResourceConverter.class);

	/** property map for configuring and mapping post model properties to lucene fields and vice versa */
	private Map<String, Map<String, Object>> postPropertyMap;
	
	private ResourceFactory resourceFactory;
	
	private Class<R> resourceClass;
	
	/**
	 * read property values from given lucene document and creates post model
	 * 
	 * @param doc
	 * @return the post representation of the lucene document
	 */
	public Post<R> writePost(final Document doc) {
		// initialize 
		final Post<R> post = this.createEmptyPost();

		// cycle though all properties and set the properties
		for (final String propertyName : postPropertyMap.keySet()) {
			// get lucene index properties
			final String fieldName = this.getFieldName(propertyName);
			final String propertyStr = doc.get(fieldName); 
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
		
		// all done.
		return post;
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
	 * @return the lucene document representation of the post
	 */
	public Document readPost(final Post<R> post) {
		final Document luceneDocument = new Document();
		
		// all fields are concatenated for full text search
		final StringBuilder fulltextField = new StringBuilder();
		// all private fields are concatenated for full text search
		final StringBuilder privateField = new StringBuilder();
		
		/*
		 * cycle though all properties and store the corresponding
		 * values in the content hash map
		 */
		for (final String propertyName : postPropertyMap.keySet()) {
			/*
			 *  extract property value from object
			 */
			final String propertyValue = this.extractPropertyValue(post, propertyName);
			
			/*
			 * get index, store and name of lucene field
			 */
			final Index fieldIndex = this.getFieldIndexForProperty(propertyName);
			final Store fieldStore = this.getFieldStoreForProperty(propertyName);
			final String fieldName = this.getFieldName(propertyName);
			
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
		
		// store merged field
		luceneDocument.add(new Field(LuceneFieldNames.MERGED_FIELDS, decodeTeX(fulltextField.toString()), Field.Store.NO, Field.Index.ANALYZED));

		// store private field
		luceneDocument.add(new Field(LuceneFieldNames.PRIVATE_FIELDS, decodeTeX(privateField.toString()), Field.Store.YES, Field.Index.ANALYZED));
		
		// all done.
		return luceneDocument;
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
	
	private Post<R> createEmptyPost() {
		final R resource = this.resourceFactory.<R>createResource(this.resourceClass);
		final User user = new User();
		final Post<R> post = new LucenePost<R>();
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
}
