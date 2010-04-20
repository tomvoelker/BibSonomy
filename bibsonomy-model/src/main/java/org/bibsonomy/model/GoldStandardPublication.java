package org.bibsonomy.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.util.SimHash;

/**
 * A publication with references to other publications
 * 
 * @author dzo
 * @version $Id$
 */
public class GoldStandardPublication extends BibTex implements GoldStandard<BibTex> {
	private static final long serialVersionUID = 128893745902925210L;
	
	private Set<BibTex> references;
	private Set<BibTex> referencedBy;
	
	private void lacyLoadReferences() {
		if (this.references == null) {
			this.references = new HashSet<BibTex>();
		}
	}
	
	private void lacyLoadReferencedBy() {
		if (this.referencedBy == null) {
			this.referencedBy = new HashSet<BibTex>();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#getReferences()
	 */
	@Override
	public Set<BibTex> getReferences() {
		this.lacyLoadReferences();
		return Collections.unmodifiableSet(this.references);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addToReferences(org.bibsonomy.model.GoldStandardPublication)
	 */
	@Override
	public boolean addToReferences(final BibTex publication) {
		this.lacyLoadReferences();
		return this.references.add(publication);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addAllToReferences(java.util.Set)
	 */
	@Override
	public boolean addAllToReferences(final Set<BibTex> publications) {
		this.lacyLoadReferences();
		if (publications != null) {
			return this.references.addAll(publications);
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeFromReferences(org.bibsonomy.model.GoldStandard)
	 */
	@Override
	public boolean removeFromReferences(final BibTex publication) {
		return this.references == null ? false : this.references.remove(publication);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeAllFromReferences(java.util.Set)
	 */
	@Override
	public boolean removeAllFromReferences(final Set<BibTex> publications) {
		return this.references == null ? false : this.references.removeAll(publications);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#addAllToReferencedBy(java.util.Set)
	 */
	@Override
	public boolean addAllToReferencedBy(Set<BibTex> resources) {
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
	public boolean addToReferencedBy(BibTex resource) {
		this.lacyLoadReferencedBy();
		return this.referencedBy.add(resource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#getReferencedBy()
	 */
	@Override
	public Set<BibTex> getReferencedBy() {
		this.lacyLoadReferencedBy();
		return Collections.unmodifiableSet(this.referencedBy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeAllFromReferencedBy(java.util.Set)
	 */
	@Override
	public boolean removeAllFromReferencedBy(Set<BibTex> resources) {
		return this.referencedBy == null ? false : this.referencedBy.removeAll(resources);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.GoldStandard#removeFromReferencedBy(org.bibsonomy.model.Resource)
	 */
	@Override
	public boolean removeFromReferencedBy(BibTex resource) {
		return this.referencedBy == null ? false : this.referencedBy.remove(resource);
	}
	
	@Override
	public void recalculateHashes() {
		final String simHash = SimHash.getSimHash(this, HashID.INTER_HASH);
		this.setIntraHash(simHash);
		this.setInterHash(simHash);
	}
}