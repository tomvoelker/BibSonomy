package org.bibsonomy.search.es.client;

import org.elasticsearch.script.Script;

/**
 * update data
 *
 * @author dzo
 */
public class UpdateData extends AbstractData {

	private Script script;

	/**
	 * @return the script
	 */
	public Script getScript() {
		return script;
	}

	/**
	 * @param script the script to set
	 */
	public void setScript(Script script) {
		this.script = script;
	}
}
