package resources;

/**
 * Stores a single element of a folkrank resultset with calculated weight
 *
 */
public class FolkrankItem {
	
	/**
	 * name of the resultset element
	 */
	private String name;
	
	/**
	 * its folkrank weight
	 */
	private float weight;
	
	public FolkrankItem(String name, float weight) {
		this.name 	= name; 
		this.weight = weight;		
	}

	/**
	 * Getter and Setter	 
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}	
}
