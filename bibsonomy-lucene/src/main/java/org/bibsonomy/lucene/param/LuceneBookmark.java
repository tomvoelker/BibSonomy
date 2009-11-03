package org.bibsonomy.lucene.param;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a bookmark, which is derived from {@link Resource}.
 * 
 * @version $Id$
 */
public class LuceneBookmark {

	private String content_id;
	private String group;
	private String date;
	private String user_name;
	private String desc;
	private String ext;
	private String url;
	private String tas;
	private String intrahash;
	
	
	public String getContent_id() {
		return content_id;
	}
	public void setContent_id(String contentId) {
		content_id = contentId;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String userName) {
		user_name = userName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTas() {
		return tas;
	}
	public void setTas(String tas) {
		this.tas = tas;
	}
	public String getIntrahash() {
		return intrahash;
	}
	public void setIntrahash(String intrahash) {
		this.intrahash = intrahash;
	}
	
	
	public HashMap<String,String> toHashMap() {
		HashMap<String,String> hM = new HashMap<String,String>();
		
		// loop over all properties and fill arrayList
		Class<?> c = this.getClass(); 
		for ( Field declaredFields : c.getDeclaredFields() ) { 
			String fieldName = declaredFields.getName();

		    String prop = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		    String mname = "get" + prop;
		    Method method = null;
			try {
				method = c.getMethod(mname);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    Object result = null;
			try {
				result = method.invoke(this);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String fieldValue = (String) result;
			hM.put(fieldName, fieldValue);
		} 
		
		//System.out.println(hM.toString());
		return hM;
	}
	
}