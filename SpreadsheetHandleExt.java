package com.wassup741.excel.comparator.common;

import org.controlsfx.control.spreadsheet.SpreadsheetView;

import impl.org.controlsfx.spreadsheet.GridViewSkin;
import impl.org.controlsfx.spreadsheet.SpreadsheetGridView;
import impl.org.controlsfx.spreadsheet.SpreadsheetHandle;

public abstract class SpreadsheetHandleExt extends SpreadsheetHandle {

	@Override
	public abstract SpreadsheetView getView();

	@Override
	public abstract SpreadsheetGridView getGridView();

	@Override
	public abstract GridViewSkin getCellsViewSkin();

}
