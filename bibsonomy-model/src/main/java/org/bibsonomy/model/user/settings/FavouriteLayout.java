package org.bibsonomy.model.user.settings;

import org.bibsonomy.model.enums.FavouriteLayoutSource;


/**
 * 
 *
 * @author jp
 */
public class FavouriteLayout {
	FavouriteLayoutSource source;
	String style;
	String displayName;
	int blabla;
	
	public FavouriteLayout(FavouriteLayoutSource source, String style){
		this.source = source;
		this.style = style.toUpperCase();
		//TODO working with Filemanagers
		if(source == FavouriteLayoutSource.SIMPLE){
			if(style.compareToIgnoreCase("BibTeX") == 0){
				this.displayName = "BibTeX";
			} else if(style.compareToIgnoreCase("EndNote") == 0){
				this.displayName = "EndNote";
			}
		} else {
			this.displayName = style.toLowerCase();
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
	/**
	 * @return DB-friendly String representation
	 */
	public String toString(){
		return this.getSource() + "/" + this.getStyle();
	}
}
