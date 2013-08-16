package org.bibsonomy.rest.renderer.impl.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;

/**
 * @author dzo
 * @version $Id$
 */
public class DeserializerFactory extends BeanDeserializerFactory {
	private static final long serialVersionUID = -3016780764419888216L;
	
	/**
	 * @param config
	 */
	public DeserializerFactory(DeserializerFactoryConfig config) {
		super(config);
	}
	
	@Override
	public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
		final Class<?> enumClass = type.getRawClass();
		return new EnumDeserializer(constructEnumResolver(enumClass, ctxt.getConfig(), beanDesc.findJsonValueMethod()));
	}
	
	@Override
	public com.fasterxml.jackson.databind.deser.DeserializerFactory withConfig(DeserializerFactoryConfig config) {
		return new DeserializerFactory(config);
	}
}
