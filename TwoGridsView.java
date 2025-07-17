package com.wassup741.excel.comparator.compare;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import javafx.collections.ObservableList;

public class TwoGridsView {
	public GridViewResult left = new GridViewResult();
	public GridViewResult mainPartialColumns = new GridViewResult();
	public GridViewResult right = new GridViewResult();
	public GridViewResult dependentPartialColumns = new GridViewResult();

	public void createGrids() {
		left.all = createGrid(left.allRows);
		left.confilct = createGrid(left.confilctRows);
		left.blank = createGrid(left.blankRows);

		mainPartialColumns.all = createGrid(mainPartialColumns.allRows);
		mainPartialColumns.confilct = createGrid(mainPartialColumns.confilctRows);
		mainPartialColumns.blank = createGrid(mainPartialColumns.blankRows);

		right.all = createGrid(right.allRows);
		right.confilct = createGrid(right.confilctRows);
		right.blank = createGrid(right.blankRows);

		dependentPartialColumns.all = createGrid(dependentPartialColumns.allRows);
		dependentPartialColumns.confilct = createGrid(dependentPartialColumns.confilctRows);
		dependentPartialColumns.blank = createGrid(dependentPartialColumns.blankRows);

	}

	private Grid createGrid(ObservableList<ObservableList<SpreadsheetCell>> rows) {
		Grid grid = null;
		if (rows != null && rows.size() > 0) {
			grid = new GridBase(rows.size(), rows.get(0).size());
			grid.setRows(rows);
		}
		return grid;
	}
}
