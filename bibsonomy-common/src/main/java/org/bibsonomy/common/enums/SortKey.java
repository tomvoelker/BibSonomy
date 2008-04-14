package org.bibsonomy.common.enums;

/**
 * Defines sorting criteria (aka sort keys) for displayed lists
 * 
 * the difference between this enum and the one defined in 
 *   org.bibsonomy.model.enums.Order
 * is that the latter defines sorting criteria which are applied when
 * retrieving items from the database (i.e. arguments to ORDER BY..). This
 * class here defines sorting options which are applied only for the currently 
 * displayed entries, e.g. the first 10 ones.
 * 
 * The sort order (asc / desc) is defined in org.bibsonomy.common.enums.SortOrder 
 * 
 * @author Dominik Benz
 * @see org.bibsonomy.model.enums.Order
 * @see org.bibsonomy.common.enums.SortOrder
 * @version $Id$
 */
public enum SortKey {
	/** no re-sorting, keep order as it comes from DB */
	NONE,
	/** sort by year */
	YEAR,
	/** sort by author */
	AUTHOR,
	/** sort by editor */
	EDITOR,
	/** by entrytype */
	ENTRYTYPE,
	/** by title */
	TITLE,
	/** by booktitle */
	BOOKTITLE,
	/** by journal */
	JOURNAL,
	/** by school */
	SCHOOL;
}