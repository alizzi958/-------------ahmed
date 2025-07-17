package com.wassup741.excel.comparator.select;

import java.io.File;

import org.apache.poi.ss.usermodel.Sheet;

import com.wassup741.excel.comparator.common.GridBaseExt;
import com.wassup741.excel.comparator.common.SheetGridUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class SelectFileController {

	private FileChooser fileChooser;
	private SelectFileModel model;
	private SelectFileView view;
	private ObjectProperty<GridBaseExt> sheetProperty = new SimpleObjectProperty<>();

	public SelectFileController(Window window) {
		model = new SelectFileModel();
		view = new SelectFileView();

		fileChooser = new FileChooser();
		fileChooser.setTitle("Выберите файл");
		ExtensionFilter excelFilter = new ExtensionFilter("Файлы Excel",
				"*.xls", "*.xlsx");
		fileChooser.getExtensionFilters().addAll(
			new ExtensionFilter("Все файлы", "*.*"), excelFilter);
		fileChooser.setSelectedExtensionFilter(excelFilter);
		view.setSelectFileAction((event) -> {
			File file = fileChooser.showOpenDialog(window);
			if (file != null) {
				Sheet sheet = model.readFile(file);
				GridBaseExt grid = SheetGridUtil.sheetToGrid(sheet);
				sheetProperty.set(grid);
				view.fillTable(grid, grid.getColumnWidths());
			}
		});
	}

	public SelectFileView getView() {
		return view;
	}

	public ObjectProperty<GridBaseExt> sheetGridProperty() {
		return sheetProperty;
	}

}
