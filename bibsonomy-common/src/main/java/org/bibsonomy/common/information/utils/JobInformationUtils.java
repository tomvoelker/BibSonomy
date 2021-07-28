package org.bibsonomy.common.information.utils;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.stream.Collectors;

import org.bibsonomy.common.information.JobInformation;

/**
 * util methods for jobInformation
 * @author dzo
 */
public final class JobInformationUtils {

	/**
	 * @param list
	 * @param jobInformationClass
	 * @return <code>true</code> iff at least one jobinformation is of the specified class
	 */
	public static boolean containsInformationType(List<JobInformation> list, Class<? extends JobInformation> jobInformationClass) {
		if (!present(list)) {
			return false;
		}
		return list.stream().anyMatch(jobInformationClass::isInstance);
	}

	/**
	 * @param list
	 * @param jobInformationClass
	 * @param <T>
	 * @return filters the list of information and returns only list items that are instances of the provided class
	 */
	public static <T extends JobInformation> List<T> extractInformationOfType(List<JobInformation> list, Class<? extends JobInformation> jobInformationClass) {
		return (List<T>) list.stream().filter(jobInformationClass::isInstance).collect(Collectors.toList());
	}
}
