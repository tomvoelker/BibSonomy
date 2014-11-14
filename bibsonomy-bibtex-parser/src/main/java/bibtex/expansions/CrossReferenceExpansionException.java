package bibtex.expansions;

/**
 * @author rja
 * @version $Id:$
 */
public class CrossReferenceExpansionException extends ExpansionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -797945153941147256L;
	private final String entryKey;
	private final String crossrefKey;

	CrossReferenceExpansionException(final String message, final String entryKey, final String crossrefKey) {
		super(message);
		this.entryKey = entryKey;
		this.crossrefKey = crossrefKey;
	}

	public String getEntryKey() {
		return entryKey;
	}

	public String getCrossrefKey() {
		return crossrefKey;
	}
	
}
