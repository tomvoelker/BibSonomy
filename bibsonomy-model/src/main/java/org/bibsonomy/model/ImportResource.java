package org.bibsonomy.model;

import org.bibsonomy.model.util.data.Data;

/**
 * @author jensi
 * @version $Id$
 */
public final class ImportResource extends BibTex {

	private static final long serialVersionUID = -7090432859414957747L;
	
	private final Data data;
	
	private final boolean alreadyParsed;
	
	/**
	 * Creates a resource that still has to be parsed
	 * @param data
	 */
	public ImportResource(Data data) {
		this.data = data;
		this.alreadyParsed = false;
	}
	
	/**
	 * Creates a resource that has already been parsed
	 */
	public ImportResource() {
		this.data = null;
		this.alreadyParsed = true;
	}
	
	@Override
	public void recalculateHashes() {
	}

	/**
	 * @return the data
	 */
	public Data getData() {
		return this.data;
	}

	/**
	 * @return the alreadyParsed
	 */
	public boolean isAlreadyParsed() {
		return this.alreadyParsed;
	}

}
