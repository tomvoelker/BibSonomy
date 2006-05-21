package org.bibsonomy.rest;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ViewModel
{
	/**
	 * a url to the next part of the resource list
	 */
	private String urlToNextResources;

	/**
	 * start value for the list of resources
	 */
	private int startValue;
	
	/**
	 * end value for the list of resources
	 */
	private int endValue;
	
	/**
	 * @return Returns the urlToNextResources.
	 */
	public String getUrlToNextResources()
	{
		return urlToNextResources;
	}

	/**
	 * @param urlToNextResources The urlToNextResources to set.
	 */
	public void setUrlToNextResources( String urlToNextResources )
	{
		this.urlToNextResources = urlToNextResources;
	}

	/**
	 * @return Returns the endValue.
	 */
	public int getEndValue()
	{
		return endValue;
	}

	/**
	 * @param endValue The endValue to set.
	 */
	public void setEndValue( int endValue )
	{
		this.endValue = endValue;
	}

	/**
	 * @return Returns the startValue.
	 */
	public int getStartValue()
	{
		return startValue;
	}

	/**
	 * @param startValue The startValue to set.
	 */
	public void setStartValue( int startValue )
	{
		this.startValue = startValue;
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-21 20:31:51  mbork
 * continued implementing context
 *
 */