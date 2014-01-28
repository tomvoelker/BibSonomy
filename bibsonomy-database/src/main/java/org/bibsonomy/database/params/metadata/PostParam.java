package org.bibsonomy.database.params.metadata;

import org.bibsonomy.database.common.enums.MetaDataPluginKey;
import org.bibsonomy.database.params.GenericParam;

/**
 * @author clemensbaier
 */
public class PostParam extends GenericParam {

	private String interHash;
	private String intraHash;
	private String value;
	private MetaDataPluginKey key;
	
	public String getInterHash() {
		return this.interHash;
	}
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}
	public String getIntraHash() {
		return this.intraHash;
	}
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	public MetaDataPluginKey getKey() {
		return key;
	}
	public void setKey(MetaDataPluginKey key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
