package org.bibsonomy.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.util.SimHash;

/**
 * XXX: most of the code is duplicated code from {@link GoldStandardPublication}
 * but java doesn't support multiple inheritance
 * 
 * @author dzo
 * @version $Id$
 */
public class GoldStandardBookmark extends Bookmark implements GoldStandard<Bookmark> {
	private static final long serialVersionUID = -1280809960981056354L;
	
	
	private Set<Bookmark> references;
	private Set<Bookmark> referencedBy;
	
	private void lacyLoadReferences() {
		if (this.references == null) {
			this.references = new HashSet<Bookmark>();
		}
	}
	
	private void lacyLoadReferencedBy() {
		if (this.referencedBy == null) {
			this.referencedBy = new HashSet<Bookmark>();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#getReferences()
	 */
	@Override
	public Set<Bookmark> getReferences() {
		this.lacyLoadReferences();
		return Collections.unmodifiableSet(this.references);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addToReferences(org.bibsonomy.model.GoldStandardPublication)
	 */
	@Override
	public boolean addToReferences(final Bookmark resources) {
		this.lacyLoadReferences();
		return this.references.add(resources);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addAllToReferences(java.util.Set)
	 */
	@Override
	public boolean addAllToReferences(final Set<? extends Bookmark> resources) {
		this.lacyLoadReferences();
		if (resources != null) {
			return this.references.addAll(resources);
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeFromReferences(org.bibsonomy.model.GoldStandard)
	 */
	@Override
	public boolean removeFromReferences(final Bookmark resource) {
		return this.references == null ? false : this.references.remove(resource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeAllFromReferences(java.util.Set)
	 */
	@Override
	public boolean removeAllFromReferences(final Set<? extends Bookmark> resources) {
		return this.references == null ? false : this.references.removeAll(resources);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addAllToReferencedBy(java.util.Set)
	 */
	@Override
	public boolean addAllToReferencedBy(final Set<? extends Bookmark> resources) {
		this.lacyLoadReferencedBy();
		if (resources != null) {
			return this.referencedBy.addAll(resources);
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addToReferencedBy(org.bibsonomy.model.Resource)
	 */
	@Override
	public boolean addToReferencedBy(final Bookmark resource) {
		this.lacyLoadReferencedBy();
		return this.referencedBy.add(resource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#getReferencedBy()
	 */
	@Override
	public Set<Bookmark> getReferencedBy() {
		this.lacyLoadReferencedBy();
		return Collections.unmodifiableSet(this.referencedBy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeAllFromReferencedBy(java.util.Set)
	 */
	@Override
	public boolean removeAllFromReferencedBy(final Set<? extends Bookmark> resources) {
		return this.referencedBy == null ? false : this.referencedBy.removeAll(resources);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeFromReferencedBy(org.bibsonomy.model.Resource)
	 */
	@Override
	public boolean removeFromReferencedBy(final Bookmark resource) {
		return this.referencedBy == null ? false : this.referencedBy.remove(resource);
	}
	
	@Override
	public void recalculateHashes() {
		final String simHash = SimHash.getSimHash(this, HashID.INTER_HASH);
		this.setIntraHash(simHash);
		this.setInterHash(simHash);
	}

}
