package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Lukas
 * @version $Id$
 */
public class OrganizationExtractor extends AbstractParticipantExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {

		if (src instanceof ExtendedMarcWithPicaRecord) {

			ExtendedMarcWithPicaRecord record = (ExtendedMarcWithPicaRecord) src;

			//check if the current record is a conference recored/proceedings
			if (checkForConference(record)) {

				// fields which possibly contain information
				String[][] marcFields = { { "110:a", "111:a", "710:a" },
						{ "110:c", "111:c", "710:c" } };
				// fallback pica fields
				String[][] picaFields = { { "029A:$a", "029F:$8", "029E:$8" },
						{ "029A:$c", "029F:$g", "029E:$g" } };

				String conference = "";
				String location = "";

				// try to find marc information
				for (int i = 0; i < marcFields.length
						&& (!ValidationUtils.present(conference) || !ValidationUtils
								.present(location)); i++) {
					if (!ValidationUtils.present(conference)) {
						conference = src.getFirstFieldValue(
								marcFields[0][i].split(":")[0],
								marcFields[0][i].split(":")[1].charAt(0));
					}

					if (!ValidationUtils.present(location)) {
						location = src.getFirstFieldValue(
								marcFields[1][i].split(":")[0],
								marcFields[1][i].split(":")[1].charAt(0));
					}
				}

				// get pica information if marc was not available
				for (int i = 0; i < picaFields.length
						&& (!ValidationUtils.present(conference) || !ValidationUtils
								.present(location)); i++) {
					if (!ValidationUtils.present(conference)) {
						conference = record.getFirstPicaFieldValue(
								picaFields[0][i].split(":")[0],
								picaFields[0][i].split(":")[1]);
					}

					if (!ValidationUtils.present(location)) {
						location = record.getFirstPicaFieldValue(
								picaFields[1][i].split(":")[0],
								picaFields[1][i].split(":")[1]);
					}

				}

				// set the results
				target.setOrganization((conference != null ? conference
						: "NoOrganization")
						+ (location != null ? "<" + location + ">" : ""));

			}
		}
	}

}
