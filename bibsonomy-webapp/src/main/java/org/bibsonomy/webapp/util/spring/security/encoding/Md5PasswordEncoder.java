/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.webapp.util.spring.security.encoding;

import org.bibsonomy.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * MD5-based PasswordEncoder for Spring Security 5 compatibility.
 *
 * Stored passwords have the form MD5(password) + salt (where salt may be empty).
 * encode() returns MD5(rawPassword) for new passwords.
 * matches() checks whether the stored password starts with MD5(rawPassword),
 * which handles both the salted legacy case and unsalted case.
 *
 * @author dzo
 */
public class Md5PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return StringUtils.getMD5Hash(rawPassword.toString());
	}

	/**
	 * Returns true when storedPassword equals MD5(rawPassword) or starts with MD5(rawPassword)
	 * (the legacy salted format is MD5(rawPassword) + salt appended).
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null || rawPassword == null) {
			return false;
		}
		final String md5 = StringUtils.getMD5Hash(rawPassword.toString());
		return encodedPassword.startsWith(md5);
	}
}
