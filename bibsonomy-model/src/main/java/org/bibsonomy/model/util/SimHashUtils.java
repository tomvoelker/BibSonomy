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
