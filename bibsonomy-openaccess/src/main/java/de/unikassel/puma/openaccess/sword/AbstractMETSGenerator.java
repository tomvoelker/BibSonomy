/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
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
package de.unikassel.puma.openaccess.sword;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.renderer.UrlRenderer;

/**
 * Abstract generator for PUMA data to METS-XML-Files for publication depositing
 * with a certain standard for the inner XML data (e.g. EPDCX, MODS)
 *
 * @author kchoong
 */
@Getter
@Setter
public abstract class AbstractMETSGenerator {
    protected final METSRenderer xmlRenderer;

    protected PumaData<BibTex> pumaData;
    protected List<String> fileNameList;
    protected User user;

    /**
     * default renderer
     * @param urlRenderer
     */
    public AbstractMETSGenerator(final UrlRenderer urlRenderer) {
        this.xmlRenderer = new METSRenderer(urlRenderer);
        this.xmlRenderer.init();
    }

    public String getFileName(final int fileNumber) {
        if (this.fileNameList.size() > fileNumber) {
            return this.fileNameList.get(fileNumber);
        }

        return null;
    }

    public abstract Mets generateMETS(PumaData<BibTex> pumaData);

    public abstract void writeMETS(OutputStream outputStream) throws IOException;
}
