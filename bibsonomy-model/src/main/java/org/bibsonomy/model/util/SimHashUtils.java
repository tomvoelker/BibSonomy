/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.HashID;

/**
 * @author dzo
 */
public final class SimHashUtils {
	private SimHashUtils() {
		// noop
	}

	public static class HashAndId {
		private final String hash;
		private final HashID hashID;

		public HashAndId(String hash, HashID hashID) {
			this.hash = hash;
			this.hashID = hashID;
		}

		/**
		 * @return the hash
		 */
		public String getHash() {
			return hash;
		}

		/**
		 * @return the hashID
		 */
		public HashID getHashID() {
			return hashID;
		}
	}

	/**
	 * removes the hash identifier
	 * @param hash the hash to remove the hash identifer if present
	 * @return the hash without the hash identifier
	 */
	public static String removeHashIdentifier(final String hash) {
		if (present(hash) && hash.length() == 33) {
			return hash.substring(1);
		}

		return hash;
	}

	/**
	 * @param hash the hash to extract the information from
	 * @return
	 */
	public static HashAndId extractHashAndHashId(final String hash) {
		return extractHashAndHashId(hash, HashID.INTER_HASH);
	}

	/**
	 * @param hash the hash to extract the information from
	 * @param defaultSimHash the default sim hash
	 * @return
	 */
	public static HashAndId extractHashAndHashId(final String hash, final HashID defaultSimHash) {
		if (present(hash) && hash.length() == 33) {
			final String extractedHash = hash.substring(1);
			try {
				final HashID hashId = HashID.getSimHash(Integer.parseInt(hash.substring(0, 1)));
				return new HashAndId(extractedHash, hashId);
			} catch (final NumberFormatException ex) {
				throw new RuntimeException(ex);
			}
		}

		return new HashAndId(hash, defaultSimHash);
	}
}
