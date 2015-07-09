package org.bibsonomy.model;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ResourcePersonRelation extends ResourcePersonRelationBase {
	private Person person;
	private Post<? extends BibTex> post;

	/**
	 * @return the personName
	 */
	public Person getPerson() {
		return this.person;
	}
	/**
	 * @param person the {@link Person} to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the post
	 */
	public Post<? extends BibTex> getPost() {
		return this.post;
	}
	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends BibTex> post) {
		this.post = post;
	}
}
