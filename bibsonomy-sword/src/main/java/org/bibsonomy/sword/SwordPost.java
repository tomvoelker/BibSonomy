package org.bibsonomy.sword;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Creates {@link Post}s from an {@link Sword}.
 * 
 * @author:  sst
 * @version: $Id$
 * $Author$
 * 
 */
public class SwordPost {
	
	public Post<BibTex> bibTexPost = null;
	public Document fileBlob = null;


	public SwordPost () {
	}

	public SwordPost (Post<BibTex> bibTexPost, Document document) {
		setSwordPost(bibTexPost, document);
	}
	
	public void setSwordPost (Post<BibTex> bibTexPost, Document document) {
		setBibTexPost(bibTexPost);
		setDocument(document);
	}
	
	public Post<BibTex> getBibTexPost() {
		return bibTexPost;
	}
	public void setBibTexPost(Post<BibTex> bibTexPost) {
		this.bibTexPost = bibTexPost;
	}
	public Boolean hasBibTexPost() {
		return (null != this.bibTexPost)?true:false;
	}
	public Document getDocument() {
		return fileBlob;
	}
	public void setDocument(Document document) {
		this.fileBlob = document;
	}
	public Boolean hasDocument() {
		return (null != this.fileBlob)?true:false;
	}
	
	
}
