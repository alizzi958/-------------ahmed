package com.wassup741.excel.comparator2;

import java.util.List;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.wassup741.excel.comparator.common.GridCellExtFactory;
import com.wassup741.excel.comparator.common.SpreadsheetHandleExt;

import impl.org.controlsfx.spreadsheet.GridViewSkin;
import impl.org.controlsfx.spreadsheet.SpreadsheetGridView;
import impl.org.controlsfx.spreadsheet.SpreadsheetViewSelectionModel;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;

public class SpreadsheetWidget {
	private SpreadsheetView table;
	private SpreadsheetGridView gridView;
	private GridCellExtFactory gridCellFactory;
	private String fileName;
	private ScrollBar scrollBar;

	public SpreadsheetWidget() {
		table = new SpreadsheetView(new GridBase(0, 0));
		table.setEditable(false);
		table.setVisible(false);

		gridView = (SpreadsheetGridView) table.getSkin().getNode();
		SpreadsheetHandleExt handle = new SpreadsheetHandleExt() {

			@Override
			public SpreadsheetView getView() {
				return table;
			}

			@Override
			public SpreadsheetGridView getGridView() {
				return gridView;
			}

			@Override
			public GridViewSkin getCellsViewSkin() {
				return (GridViewSkin) (gridView.getSkin());
			}
		};
		gridView.setSelectionModel(
			new SpreadsheetViewSelectionModelExt(table, gridView));
		gridView.getSelectionModel().cellSelectionEnabledProperty().set(false);
		gridCellFactory = new GridCellExtFactory(handle);
	}

	public void fill(Grid grid, List<Double> columnWidths) {
		if (grid != null) {
			table.setGrid(grid);
			table.setVisible(true);
			IntStream.range(0, grid.getColumnCount()).forEach(index -> {
				table.getColumns().get(index)
						.setPrefWidth(columnWidths.get(index));
			});

			for (TableColumn<ObservableList<SpreadsheetCell>, ?> column : gridView
					.getColumns()) {
				@SuppressWarnings("unchecked")
				TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> gridColumn = (TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>) column;
				gridColumn.setCellFactory(gridCellFactory);
			}
		}
	}

	public SpreadsheetView getTable() {
		return table;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public SpreadsheetGridView getGridView() {
		return gridView;
	}

	public DoubleProperty getScrollProperty() {
		if (scrollBar == null) {
			scrollBar = (ScrollBar) gridView.lookup(".scroll-bar:vertical");
		}
		return scrollBar == null ? null : scrollBar.valueProperty();
	}

	public static class SpreadsheetViewSelectionModelExt
			extends SpreadsheetViewSelectionModel {

		public SpreadsheetViewSelectionModelExt(SpreadsheetView spreadsheetView,
				SpreadsheetGridView cellsView) {
			super(spreadsheetView, cellsView);
		}

		@Override
		public void select(int row,
				TableColumnBase<ObservableList<SpreadsheetCell>, ?> column) {
			if (isCellSelectionEnabled()) {
				super.select(row, column);
			} else {
				return;
			}
		}

		@Override
		public void select(int row) {
			if (isCellSelectionEnabled()) {
				super.select(row);
			} else {
				return;
			}
		}

		@Override
		public void select(int row,
				TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
			if (isCellSelectionEnabled()) {
				super.select(row, column);
			} else {
				return;
			}
		}

		@Override
		public void select(ObservableList<SpreadsheetCell> obj) {
			if (isCellSelectionEnabled()) {
				super.select(obj);
			} else {
				return;
			}
		}

		@Override
		public void clearAndSelect(int row) {
			if (isCellSelectionEnabled()) {
				super.clearAndSelect(row);
			} else {
				return;
			}
		}

		@Override
		public void clearAndSelect(int row,
				TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
			if (isCellSelectionEnabled()) {
				super.clearAndSelect(row, column);
			} else {
				return;
			}
		}

		@Override
		public void clearAndSelect(int row,
				TableColumnBase<ObservableList<SpreadsheetCell>, ?> column) {
			if (isCellSelectionEnabled()) {
				super.clearAndSelect(row, column);
			} else {
				return;
			}
		}
	}

}
