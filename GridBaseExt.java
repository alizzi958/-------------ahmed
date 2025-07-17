package com.wassup741.excel.comparator.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.controlsfx.control.spreadsheet.GridBase;

import javafx.util.Callback;

public class GridBaseExt extends GridBase {

	private Sheet sheet;
	private List<Double> columnWidths = new ArrayList<>();
	private Callback<Integer, Double> rowHeight;

	public GridBaseExt(int rowCount, int columnCount, Sheet sheet) {
		super(rowCount, columnCount);
		this.sheet = sheet;
		calcColWidths();
	}

	public GridBaseExt(int rowCount, int columnCount, Sheet sheet,
			int columnIndex) {
		super(rowCount, columnCount);
		this.sheet = sheet;
		columnWidths.add(getColumnWidth(sheet
				.getColumnWidthInPixels(columnIndex)));
	}

	public Sheet getSheet() {
		return sheet;
	}

	private void calcColWidths() {
		columnWidths.clear();
		IntStream.range(0, getColumnCount()).forEach(
			index -> {
				columnWidths.add(getColumnWidth(sheet
						.getColumnWidthInPixels(index)));
			});
	}

	public List<Double> getColumnWidths() {
		return columnWidths;
	}

	@Override
	public void setRowHeightCallback(Callback<Integer, Double> rowHeight) {
		super.setRowHeightCallback(rowHeight);
		this.rowHeight = rowHeight;
	}

	public Callback<Integer, Double> getRowHeightCallback() {
		return rowHeight;
	}

	private Double getColumnWidth(float width) {
		return width > 25 ? new Double(width) : new Double(25);
	}
}
