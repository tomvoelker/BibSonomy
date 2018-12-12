package org.bibsonomy.export;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
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
	public void save(Collection<T> entities, OutputStream outputStream, Map<String, Function<T, String>> mappings) throws IOException {
		final XSSFWorkbook workbook = new XSSFWorkbook();
		final Sheet sheet = workbook.createSheet();
		fillHeaderRow(sheet.createRow(0), mappings.keySet());
		int row = 1;
		for (T entity : entities) {
			fillRow(sheet.createRow(row++), entity, mappings.values());
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
