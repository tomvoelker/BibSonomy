package org.bibsonomy.lucene.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.bibsonomy.lucene.param.typehandler.LuceneTypeHandler;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.tex.TexDecode;

/**
 * class for converting bibsonomy post model objects to lucene
 * documents
 * 
 * @author fei
 */
public abstract class LuceneResourceConverter<R extends Resource> extends LuceneBase {
	private static final Log log = LogFactory.getLog(LuceneResourceConverter.class);

	/** property map for configuring and mapping post model properties to lucene fields and vice versa */
	private Map<String,Map<String,Object>> postPropertyMap;
	
	/**
	 * read property values from given lucene document and creates post model
	 * 
	 * @param post
	 * @return
	 */
	public Post<R> writePost(Document doc) {
		// initialize 
		Post<R> post = createEmptyPost();

		// cycle though all properties and store the corresponding
		// values in the content hash map
		for( String propertyName : postPropertyMap.keySet() ) {
			// get lucene index properties
			String fieldName   = (String)postPropertyMap.get(propertyName).get(CFG_LUCENENAME);
			String propertyStr = doc.get(fieldName); 
			
			if( !ValidationUtils.present(propertyStr) )
				continue;
			
			LuceneTypeHandler typeHandler = (LuceneTypeHandler)postPropertyMap.get(propertyName).get(CFG_TYPEHANDLER);

			Object propertyValue = null;
			if( typeHandler!=null ) {
				propertyValue = typeHandler.setValue(propertyStr);
			} else
				propertyValue = propertyStr;

			try {
				PropertyUtils.setNestedProperty(post, propertyName, propertyValue);
			} catch (Exception e) {
				log.error("Error setting property " + propertyName + " to " + propertyValue.toString(), e);
			}

		}
		
		// all done.
		return post;
	}
	
	/**
	 * read property values from given object as defined in given propertyMap
	 * 
	 * @param post
	 * @return
	 */
	public Document readPost(Post<R> post) {
		Document retVal = new Document();
		// FIXME: default values should be configured via spring
		Index fldDefaultIndex = Field.Index.NOT_ANALYZED;
		Store fldDefaultStore = Field.Store.YES;
		Index fldIndex = fldDefaultIndex;
		Store fldStore = fldDefaultStore;

		// all fields are concatenated for full text search
		String mergedField = "";
		
		// all private fields are concatenated for full text search
		String privateField= "";
		
		//--------------------------------------------------------------------
		// cycle though all properties and store the corresponding
		// values in the content hash map
		//--------------------------------------------------------------------
		for( String propertyName : postPropertyMap.keySet() ) {
			//----------------------------------------------------------------
			// retrieve property value from post object
			//----------------------------------------------------------------
			// extract property value from object
			Object property = null;
			String propertyValue = "";
			try {
				// get property from post object
				property = PropertyUtils.getProperty(post, propertyName);
				// only handle non-null values
				if( property!=null ) {
					LuceneTypeHandler typeHandler  = (LuceneTypeHandler)postPropertyMap.get(propertyName).get(CFG_TYPEHANDLER);
					
					// get property value
					propertyValue = extractPropertyValue(postPropertyMap, typeHandler, propertyName, property);
					
					// get lucene index configuration
					if( postPropertyMap.get(propertyName).get(CFG_FLDINDEX)!=null) {
						fldIndex = (Index) postPropertyMap.get(propertyName).get(CFG_FLDINDEX);
					} else {
						fldIndex = fldDefaultIndex;
					}
					if( postPropertyMap.get(propertyName).get(CFG_FLDSTORE)!=null) {
						fldStore = (Store) postPropertyMap.get(propertyName).get(CFG_FLDSTORE);
					} else {
						fldStore = fldDefaultStore;
					}
				}
			} catch (Exception e) {
				log.error("Error reading property '"+propertyName+"' from post object.", e);
			}
			//----------------------------------------------------------------
			// add property to the lucene document
			//----------------------------------------------------------------
			String luceneName = (String)postPropertyMap.get(propertyName).get(CFG_LUCENENAME);
			// FIXME: configure default value field wise via spring
			String defaultValue = "";
			if( (propertyValue!=null) && (luceneName!=null) && (!"".equals(propertyValue.trim())) ) {
				// add field to the lucene document
				retVal.add( new Field(luceneName, propertyValue, fldStore, fldIndex));
				// add term to full text search field, if configured accordingly 
				if( ValidationUtils.present(postPropertyMap.get(propertyName).get(CFG_FULLTEXT_FLAG)) &&
				    (Boolean)postPropertyMap.get(propertyName).get(CFG_FULLTEXT_FLAG) ) {
					mergedField += CFG_LIST_DELIMITER + propertyValue;
				}
				// add term to private full text search field, if configured accordingly 
				if( ValidationUtils.present(postPropertyMap.get(propertyName).get(CFG_PRIVATE_FLAG)) &&
				    (Boolean)postPropertyMap.get(propertyName).get(CFG_PRIVATE_FLAG) ) {
					privateField += CFG_LIST_DELIMITER + propertyValue;
				}
			} else {
				// add empty field
				retVal.add( new Field(luceneName, defaultValue, fldStore, fldIndex));
			}
		}
		
		// store merged field
		retVal.add(new Field(FLD_MERGEDFIELDS, TexDecode.decode(mergedField), Field.Store.NO, Field.Index.ANALYZED));

		// store private field
		retVal.add(new Field(FLD_PRIVATEFIELDS, TexDecode.decode(privateField), Field.Store.YES, Field.Index.ANALYZED));
		
		// all done.
		return retVal;
	}
	
	/**
	 * extracts property value from given object
	 * 
	 * @param bibTexPropertyMap
	 * @param propertyName
	 * @param item
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private String extractPropertyValue( 
			Map<String, Map<String, Object>> bibTexPropertyMap, LuceneTypeHandler typeHandler,
			String propertyName, Object item) 
	throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String itemValue = null;
		// get the string value for the given object
		if( typeHandler!=null ) {
			// if a type handler is set, use the type handler for rendering the string
			itemValue = typeHandler.getValue(item);
		} else {
			// if no type handler is set use Object.toString()
			itemValue = item.toString();
		}
		return itemValue;
	}
	//------------------------------------------------------------------------
	// abstract interface
	//------------------------------------------------------------------------
	protected abstract Post<R> createEmptyPost();
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setPostPropertyMap(Map<String,Map<String,Object>> postPropertyMap) {
		this.postPropertyMap = postPropertyMap;
	}

	public Map<String,Map<String,Object>> getPostPropertyMap() {
		return this.postPropertyMap;
	}
	
}
