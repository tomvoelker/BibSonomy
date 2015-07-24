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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("[");
		sb.append(getPersonRelChangeId());
		sb.append("-");
		if (person != null) {
			sb.append(person.getPersonId());
		} else {
			sb.append("null");
		}
		sb.append("-");
		sb.append(this.getRelationType());
		sb.append("-");
		if ((post != null) && (post.getResource() != null)) {
			sb.append(post.getResource().getInterHash());
		} else {
			sb.append("null");
		}
		sb.append("]");
		return sb.toString();
	}
}
