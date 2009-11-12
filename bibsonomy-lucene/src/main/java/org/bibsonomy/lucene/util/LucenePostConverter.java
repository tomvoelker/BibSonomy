package org.bibsonomy.lucene.util;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.param.typehandler.LuceneTypeHandler;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.tex.TexEncode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * class for converting bibsonomy post model objects to lucene
 * documents
 * 
 * @author fei
 */
public class LucenePostConverter {
	private static final Log log = LogFactory.getLog(LucenePostConverter.class);

	private static final String LUCENE_CONTEXT_XML = "LuceneIndexConfig.xml";

	private static final String CFG_LUCENENAME = "luceneName";
	private static final String CFG_TYPEHANDLER = "typeHandler";
	private static final String CFG_ITEMPROPERTY   = "itemProperty";
	private static final String CFG_LIST_DELIMITER = " ";
	private static final String CFG_FLDINDEX = "luceneIndex";
	private static final String CFG_FLDSTORE = "luceneStore";
	
	private static final String FLD_MERGEDFIELD = "mergedfields";

	/** spring bean factory for initializing instances */
	private static BeanFactory beanFactory;
	
	/**
	 * static initialization
	 */
	static {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		        new String[] {LUCENE_CONTEXT_XML});

		// an ApplicationContext is also a BeanFactory (via inheritance)
		beanFactory = context;
	}
	
	/**
	 * read property values from given object as defined in given propertyMap
	 * 
	 * @param post
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Document readPost(Post<? extends Resource> post) {
		Document retVal = new Document();
		// FIXME: default values should be configured via spring
		Index fldIndex = Field.Index.NOT_ANALYZED;
		Store fldStore = Field.Store.YES;

		TexEncode tex = new TexEncode();
		
		// FIXME: configure merged field via spring
		String mergedField = "";

		//--------------------------------------------------------------------
		// read bibtex properties from spring configuration file
		//--------------------------------------------------------------------
		// FIXME: remove this test by refactoring
		Map<String,Map<String,Object>> resourcePropertyMap;
		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) ){
			resourcePropertyMap = (Map<String, Map<String,Object>>) beanFactory.getBean("bookmarkPropertyMap");
		} else if( BibTex.class.isAssignableFrom(post.getResource().getClass()) ){
			resourcePropertyMap = (Map<String, Map<String,Object>>) beanFactory.getBean("bibTexPropertyMap");
		} else {
			log.error("Unknown resource type");
			return retVal;
		}

		
		// cycle though all properties and store the corresponding
		// values in the content hash map
		for( String propertyName : resourcePropertyMap.keySet() ) {
			// log.debug("Reading property "+propertyName);


			// extract property value from object
			Object property = null;
			String propertyValue = "";
			try {
				// get property from post object
				property = PropertyUtils.getProperty(post, propertyName);
				// only handle non-null values
				if( property!=null ) {
					// get property value
					if( property instanceof Iterable<?> ) {
						// if property is a collection - concatenate all items in a single value
						for( Object item : (Iterable<?>)property ) {
							if(!"".equals(propertyValue))
								propertyValue += CFG_LIST_DELIMITER;
							propertyValue += extractPropertyValue(resourcePropertyMap, propertyName, item);
						}
					} else {
						propertyValue = extractPropertyValue(resourcePropertyMap, propertyName, property);
					}
					// get lucene index properties
					if( resourcePropertyMap.get(propertyName).get(CFG_FLDINDEX)!=null) {
						fldIndex = (Index) resourcePropertyMap.get(propertyName).get(CFG_FLDINDEX);
					}
					if( resourcePropertyMap.get(propertyName).get(CFG_FLDSTORE)!=null) {
						fldStore = (Store) resourcePropertyMap.get(propertyName).get(CFG_FLDSTORE);
					}
				}
			} catch (Exception e) {
				log.error("Error reading property '"+propertyName+"' from post object.", e);
			}
			
			String luceneName = (String)resourcePropertyMap.get(propertyName).get(CFG_LUCENENAME);
			// FIXME: configure default value field wise via spring
			String defaultValue = "";
			if( (propertyValue!=null) && (luceneName!=null) && (!"".equals(propertyValue.trim())) ) {
				// log.debug("Extracted '"+propertyValue+"' from property '"+propertyName+"' to '"+luceneName+"'");
				retVal.add( new Field(luceneName, propertyValue, fldStore, fldIndex));
				// FIXME: configure merged field via spring
				mergedField += CFG_LIST_DELIMITER + propertyValue;
			} else {
				// add empty field
				retVal.add( new Field(luceneName, defaultValue, fldStore, fldIndex));
			}
		}
		
		// store merged field
		// FIXME: configure merged field via spring
		retVal.add(new Field(FLD_MERGEDFIELD, tex.encode(mergedField), Field.Store.YES, Field.Index.ANALYZED));
		
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
	private static String extractPropertyValue( 
			Map<String, Map<String, Object>> bibTexPropertyMap, 
			String propertyName, Object item) 
	throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String itemProperty = (String)bibTexPropertyMap.get(propertyName).get(CFG_ITEMPROPERTY);
		LuceneTypeHandler typeHandler  = (LuceneTypeHandler)bibTexPropertyMap.get(propertyName).get(CFG_TYPEHANDLER);
		String itemValue = null;
		// get the string value for the given object
		if( typeHandler!=null ) {
			// if a type handler is set, use the type handler for rendering the string
			itemValue = typeHandler.getValue(item);
		} else {
			// if no type handler is set, look for an item property
			if( itemProperty!=null ) {
				itemValue = (String)PropertyUtils.getNestedProperty(item, itemProperty);
			} else {
				// if no type handler or item property is set use Object.toString()
				itemValue = item.toString();
			}
		}
		return itemValue;
	}
	
}
