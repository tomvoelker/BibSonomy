package org.bibsonomy.webapp.util.spring.factorybeans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jensi
  */
public class JsonToMapFactoryBean implements FactoryBean<Map<String, String>>{

	private final String jsonObject;

	/**
	 * @param jsonObject
	 */
	public JsonToMapFactoryBean(String jsonObject) {
		this.jsonObject = jsonObject;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getObject() throws Exception {
		if (jsonObject == null) {
			return new HashMap<String, String>();
		}
		return new ObjectMapper().readValue(jsonObject, HashMap.class);
	}

	@Override
	public Class<?> getObjectType() {
		return Map.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
