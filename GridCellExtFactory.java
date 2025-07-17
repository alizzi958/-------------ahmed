package com.wassup741.excel.comparator.common;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public final class GridCellExtFactory
		implements
		Callback<TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>, TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell>> {
	private SpreadsheetHandleExt handle;

	public GridCellExtFactory(SpreadsheetHandleExt handle) {
		this.handle = handle;
	}

	@Override
	public TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> call(
			TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> param) {
		return new CellViewExt(handle);
	}
}