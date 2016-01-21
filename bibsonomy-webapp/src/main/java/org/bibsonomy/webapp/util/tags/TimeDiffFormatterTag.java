/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 * Please note: We need the empty value for "time.postfix". Thus: always enable
 * "Keep properties with empty values" in your ResourceBundleEditor settings!
 * 
 * @author rja
 */
public class TimeDiffFormatterTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 8006189027834637063L;
	
	/**
	 * Common message code prefix used for all messages.
	 */
	private static final String MESSAGE_PREFIX = "time.";
	
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
	 * If one of the dates is not given, "" is returned.
	 * 
	 * @param startDate
	 * @param endDate 
	 * @param locale
	 * @param messageSource 
	 * @return The formatted time difference. 
	 */
	protected static String formatTimeDiff(final Date startDate, final Date endDate, final Locale locale, final MessageSource messageSource) {
		if (!present(startDate) || !present(endDate)) return "";
		/*
		 * based on http://stackoverflow.com/questions/635935/how-can-i-calculate-a-time-span-in-java-and-format-the-output
		 */
		final long endDateTime = endDate.getTime();

		/*
		 * time between now and the given date
		 */
		long diffInSeconds = (endDateTime - startDate.getTime()) / 1000;
		final long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
		long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
		long years = (diffInSeconds = (diffInSeconds / 12));
		final boolean justNow = years == 0 && months == 0 && days == 0 && hrs == 0 && min == 0;
		
		if (justNow) {
			return getMessage("justnow", null, locale, messageSource);
		}
		/*
		 * holds the resulting message key
		 */
		final StringBuilder sb = new StringBuilder(getMessage("prefix", null, locale, messageSource));
		if (years > 0) {
			if (months >= 12 / 2) {
				years++;
			}
			if (years == 1) {
				sb.append(getMessage("a_year", null, locale, messageSource)); // "a year"
			} else {
				sb.append(getMessage("x_years", Long.valueOf(years), locale, messageSource)); // years + " years"
			}
		} else if (months > 0) {
			if (days >= 30 / 2) {
				months++;
			}
			if (months == 1) {
				sb.append(getMessage("a_month", null, locale, messageSource)); // "a month"
			} else {
				sb.append(getMessage("x_months", Long.valueOf(months), locale, messageSource)); // months + " months"
			}
		} else if (days > 0) {
			if (hrs >= 24 / 2) {
				days++;
			}
			if (days == 1) {
				sb.append(getMessage("a_day", null, locale, messageSource)); // "a day"
			} else {
				sb.append(getMessage("x_days", Long.valueOf(days), locale, messageSource)); // days + " days"
			}
		} else if (hrs > 0) {
			if (min >= 60 / 2) {
				hrs++;
			}
			if (hrs == 1) {
				sb.append(getMessage("an_hour", null, locale, messageSource)); // "an hour"
			} else {
				sb.append(getMessage("x_hours", Long.valueOf(hrs), locale, messageSource)); // hrs + " hours"
			}
		} else if (min > 0) {
			if (sec >= 60 / 2) {
				min++;
			}
			if (min == 1) {
				sb.append(getMessage("a_minute", null, locale, messageSource)); // "a minute"
			} else {
				sb.append(getMessage("x_minutes", Long.valueOf(min), locale, messageSource)); // min + " minutes"
			}
		}
		
		sb.append(getMessage("postfix", null, locale, messageSource)); // " ago"
		return sb.toString();
	}
	
	/**
	 * @param code
	 * @param number
	 * @param locale
	 * @param messageSource
	 * @return
	 */
	private static String getMessage(final String code, final Long number, final Locale locale, final MessageSource messageSource) {
		return messageSource.getMessage(MESSAGE_PREFIX + code, new Object[]{number}, locale);
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
