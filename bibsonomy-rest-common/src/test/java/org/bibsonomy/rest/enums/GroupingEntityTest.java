package org.bibsonomy.rest.enums;

import junit.framework.TestCase;

import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.UnsupportedGroupingException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GroupingEntityTest extends TestCase
{
	public void testGetGroupingEntity()
	{
		assertEquals( GroupingEntity.ALL, GroupingEntity.getGroupingEntity( "all" ) );
		assertEquals( GroupingEntity.GROUP, GroupingEntity.getGroupingEntity( "group" ) );
		assertEquals( GroupingEntity.USER, GroupingEntity.getGroupingEntity( "user" ) );
		assertEquals( GroupingEntity.VIEWABLE, GroupingEntity.getGroupingEntity( "viewable" ) );

		assertEquals( GroupingEntity.ALL, GroupingEntity.getGroupingEntity( " All" ) );
		assertEquals( GroupingEntity.GROUP, GroupingEntity.getGroupingEntity( "GROUP" ) );
		assertEquals( GroupingEntity.USER, GroupingEntity.getGroupingEntity( "uSeR " ) );
		assertEquals( GroupingEntity.VIEWABLE, GroupingEntity.getGroupingEntity( "ViewAble" ) );
		
		try
		{
			GroupingEntity.getGroupingEntity( "foo bar" );
			fail( "Should throw exception" );
		}
		catch( final UnsupportedGroupingException ex )
		{
		}

		try
		{
			GroupingEntity.getGroupingEntity( "" );
			fail( "Should throw exception" );
		}
		catch( final UnsupportedGroupingException ex )
		{
		}

		try
		{
			GroupingEntity.getGroupingEntity( null );
			fail( "Should throw exception" );
		}
		catch( final InternServerException ex )
		{
		}
	}

	/*
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	public void testToString()
	{
		assertEquals( "GROUP", GroupingEntity.GROUP.toString() );
		assertEquals( "USER", GroupingEntity.USER.toString() );
		assertEquals( "VIEWABLE", GroupingEntity.VIEWABLE.toString() );
		assertEquals( "ALL", GroupingEntity.ALL.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 */