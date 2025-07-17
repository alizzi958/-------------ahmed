package com.wassup741.excel.comparator.compare;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GridViewResult {
	public Grid all, confilct, blank;
	public ObservableList<ObservableList<SpreadsheetCell>> allRows = FXCollections
			.observableArrayList();
	public ObservableList<ObservableList<SpreadsheetCell>> confilctRows = FXCollections
			.observableArrayList();
	public ObservableList<ObservableList<SpreadsheetCell>> blankRows = FXCollections
			.observableArrayList();
}
