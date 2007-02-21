package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedGroupingException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public enum GroupingEntity
{
	USER, GROUP, VIEWABLE, ALL,FRIEND;

	/**
	 * Returns the corresponding HttpMethod-enum for the given string.
	 */
	public static GroupingEntity getGroupingEntity( final String groupingEntity )
	{
		if( groupingEntity == null ) throw new InternServerException( "GroupingEntity is null" );
		final String entity = groupingEntity.toLowerCase().trim();
		if( "user".equals( entity ) )
		{
			return USER;
		}
		else if( "group".equals( entity ) )
		{
			return GROUP;
		}
		else if( "friend".equals( entity ) )
		{
			return FRIEND;
		}
		else if( "viewable".equals( entity ) )
		{
			return VIEWABLE;
		}
		else if( "all".equals( entity ) )
		{
			return ALL;
		}
		else
		{
			throw new UnsupportedGroupingException( groupingEntity );
		}
	}
}

/*
 * $Log$
 * Revision 1.1  2007-02-21 14:42:37  mbork
 * somehow forgot to commit ^^
 *
 * Revision 1.2  2007/02/20 09:54:19  mgrahl
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */