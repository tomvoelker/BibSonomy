package org.bibsonomy.recommender.tags.database.params;

/**
 * Bean for selection result strategy settings
 * 
 * @author fei
 * @version $Id$
 */
public class SelectorSettingParam {
	private Long id;
	private String info;
	private byte[] meta;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}
	
	/**
	 * @return the info
	 */
	public String getInfo() {
		return this.info;
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(final String info) {
		this.info = info;
	}
	
	/**
	 * @return the meta
	 */
	public byte[] getMeta() {
		return this.meta;
	}
	
	/**
	 * @param meta the meta to set
	 */
	public void setMeta(final byte[] meta) {
		this.meta = meta;
	}	
}
