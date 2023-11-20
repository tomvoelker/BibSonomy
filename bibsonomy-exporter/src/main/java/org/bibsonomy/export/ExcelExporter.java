/**
 * BibSonomy Exporter - Various exporters for bookmarks and publications.
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
package org.bibsonomy.export;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * exports entities into a spreadsheeet
 *
 * @author pda
 * @param <T>
 */
public class ExcelExporter<T> implements Exporter<T> {
	private final static String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private void fillHeaderRow(Row row, Collection<String> headers) {
		int idx = 0;
		for (String header : headers) {
			row.createCell(idx++).setCellValue(header);
		}
	}

	private void fillRow(Row row, T object, Collection<Function<T, String>> mappings) {
		int idx = 0;
		for (Function<T, String> entry : mappings) {
			row.createCell(idx++).setCellValue(entry.apply(object));
		}
	}

	@Override
	public void save(Collection<T> entities, OutputStream outputStream, List<ExportFieldMapping<T>> mappings) throws IOException {
		final XSSFWorkbook workbook = new XSSFWorkbook();
		final Sheet sheet = workbook.createSheet();
		fillHeaderRow(sheet.createRow(0), mappings.stream().map(ExportFieldMapping::getHeader).collect(Collectors.toList()));
		int row = 1;
		for (T entity : entities) {
			fillRow(sheet.createRow(row++), entity, mappings.stream().map(ExportFieldMapping::getConverter).collect(Collectors.toList()));
		}
		// auto size columns
		IntStream.range(0, mappings.size()).forEach(sheet::autoSizeColumn);
		workbook.write(outputStream);
		workbook.close();
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getFileExtension() {
		return ".xlxs";
	}
}
