package bibtex.expansions;

/**
 * 
 * @author rja
 */
public class CrossReferenceExpansionException extends ExpansionException {
	private static final long serialVersionUID = -797945153941147256L;
	
	private final String entryKey;
	private final String crossrefKey;

	CrossReferenceExpansionException(final String message, final String entryKey, final String crossrefKey) {
		super(message);
		this.entryKey = entryKey;
		this.crossrefKey = crossrefKey;
	}

	/**
	 * @return the entryKey
	 */
	public String getEntryKey() {
		return this.entryKey;
	}

	/**
	 * @return the crossrefKey
	 */
	public String getCrossrefKey() {
		return this.crossrefKey;
	}
}
