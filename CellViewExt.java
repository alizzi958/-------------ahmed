package com.wassup741.excel.comparator.common;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import impl.org.controlsfx.spreadsheet.CellView;

public final class CellViewExt extends CellView {

	private SpreadsheetHandleExt handle2;

	public CellViewExt(SpreadsheetHandleExt handle) {
		super(handle);
		handle2 = handle;
	}

	@Override
	public void updateItem(SpreadsheetCell item, boolean empty) {
		super.updateItem(item, empty);
		if (item instanceof SpreadsheetCellExt) {
			SpreadsheetCellExt itemExt = (SpreadsheetCellExt) item;
			if (handle2.getView().getGrid() instanceof GridBaseExt) {
				SheetGridUtil.applyCellStyle(this, itemExt,
					((GridBaseExt) handle2.getView().getGrid()).getSheet());
			}
		}
	}
}