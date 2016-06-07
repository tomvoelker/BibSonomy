package org.bibsonomy.model.user.settings;

import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.enums.SimpleExportLayout;


/**
 * @author jp
 */
public class FavouriteLayout {
	private FavouriteLayoutSource source;
	private String style;
	private String displayName;
	
	/**
	 * @param source TODO: change type to {@link FavouriteLayoutSource}
	 * @param style
	 * 
	 * creates a new favourite layout with source as source, STYLE as style and a display Name
	 */
	public FavouriteLayout(String source, String style) {
		this.source = FavouriteLayoutSource.valueOf(source);
		this.style = style.toUpperCase();
		//TODO working with Filemanagers
		this.displayName = style.toLowerCase();
		if (SimpleExportLayout.BIBTEX.getDisplayName().equalsIgnoreCase(style)) {
			displayName = SimpleExportLayout.BIBTEX.getDisplayName();
		} else if (SimpleExportLayout.ENDNOTE.getDisplayName().equalsIgnoreCase(style)) {
			displayName = SimpleExportLayout.ENDNOTE.getDisplayName();
		}
	}
	
	/**
	 * @return the source
	 */
	public FavouriteLayoutSource getSource() {
		return this.source;
	}
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * @return the style
	 */
	public String getStyle() {
		return this.style;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FavouriteLayout other = (FavouriteLayout) obj;
		if (source != other.source)
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		return true;
	}
}
