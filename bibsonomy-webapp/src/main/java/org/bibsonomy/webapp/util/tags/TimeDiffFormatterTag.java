package org.bibsonomy.webapp.util.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.JspTagException;

import org.springframework.context.MessageSource;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * A JSP tag that prints the formatted time difference between two dates.
 * 
 * @author rja
 * @version $Id$
 */
public class TimeDiffFormatterTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 8006189027834637063L;
	
	private Date startDate;
	private Date endDate;

	
	@Override
	protected int doStartTagInternal() throws Exception {
		try {
			this.pageContext.getOut().print(formatTimeDiff(this.startDate, present(this.endDate) ? this.endDate : new Date(), getLocale(), getMessageSource()));
		} catch (final IOException ex) {
			throw new JspTagException("Error: IOException while writing to client" + ex.getMessage());
		}
		return SKIP_BODY;
	}

	
	/**
	 * Use the current RequestContext's application context as MessageSource.
	 */
	private MessageSource getMessageSource() {
		return getRequestContext().getMessageSource();
	}

	
	/**
	 * Use the current RequestContext's application context as MessageSource.
	 */
	private Locale getLocale() {
		return getRequestContext().getLocale();
	}
	
	/**
	 * Formats the difference between the given date and the current date using
	 * the locale. Only the largest part of the time difference is returned.
	 * 
	 * FIXME: I18N missing
	 * 
	 * @param startDate
	 * @param endDate 
	 * @param locale
	 * @param messageSource 
	 * @return The formatted time difference. 
	 */
	protected static String formatTimeDiff(final Date startDate, final Date endDate, final Locale locale, final MessageSource messageSource) {
		/*
		 * time between now and the given date
		 */
		
	    final StringBuffer sb = new StringBuffer();

	    /*
	     * source: http://stackoverflow.com/questions/635935/how-can-i-calculate-a-time-span-in-java-and-format-the-output
	     */
		final long endDateTime = endDate.getTime();
		long diffInSeconds = (endDateTime - startDate.getTime()) / 1000;
	    final long sec    = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
	    final long min    = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
	    final long hrs    = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
	    final long days   = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
	    final long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
	    final long years  = (diffInSeconds = (diffInSeconds / 12));

	    if (years > 0) {
	        if (years == 1) {
	            sb.append("a year");
	        } else {
	            sb.append(years + " years");
	        }
	        if (years <= 6 && months > 0) {
	            if (months == 1) {
	                sb.append(" and a month");
	            } else {
	                sb.append(" and " + months + " months");
	            }
	        }
	    } else if (months > 0) {
	        if (months == 1) {
	            sb.append("a month");
	        } else {
	            sb.append(months + " months");
	        }
	        if (months <= 6 && days > 0) {
	            if (days == 1) {
	                sb.append(" and a day");
	            } else {
	                sb.append(" and " + days + " days");
	            }
	        }
	    } else if (days > 0) {
	        if (days == 1) {
	            sb.append("a day");
	        } else {
	            sb.append(days + " days");
	        }
	        if (days <= 3 && hrs > 0) {		
	            if (hrs == 1) {
	                sb.append(" and an hour");
	            } else {
	                sb.append(" and " + hrs + " hours");
	            }
	        }
	    } else if (hrs > 0) {
	        if (hrs == 1) {
	            sb.append("an hour");
	        } else {
	            sb.append(hrs + " hours");
	        }
	        if (min > 1) {
	            sb.append(" and " + min + " minutes");
	        }
	    } else if (min > 0) {
	        if (min == 1) {
	            sb.append("a minute");
	        } else {
	            sb.append(min + " minutes");
	        }
	        if (sec > 1) {
	            sb.append(" and " + sec + " seconds");
	        }
	    } else {
	        if (sec <= 1) {
	            sb.append("about a second");
	        } else {
	            sb.append("about " + sec + " seconds");
	        }
	    }

	    sb.append(" ago");


		return sb.toString(); 
	}

	/**
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * If not given, the current date is used. 
	 * 
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
