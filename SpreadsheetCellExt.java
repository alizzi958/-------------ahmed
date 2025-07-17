package com.wassup741.excel.comparator.common;

import org.apache.poi.ss.usermodel.CellStyle;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

public class SpreadsheetCellExt extends SpreadsheetCellBase {

	private CellStyle cellStyle;

	public SpreadsheetCellExt(int row, int column, int rowSpan, int columnSpan,
			SpreadsheetCellType<?> type) {
		super(row, column, rowSpan, columnSpan, type);
	}

	public SpreadsheetCellExt(int row, int column, int rowSpan, int columnSpan,
			SpreadsheetCellType<?> type, Object value) {
		super(row, column, rowSpan, columnSpan, type);
		setItem(value);
	}

	public CellStyle getCellStyle() {
		return cellStyle;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}
}
