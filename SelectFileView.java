package com.wassup741.excel.comparator.select;

import java.util.List;
import java.util.stream.IntStream;

import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.wassup741.excel.comparator.common.GridCellExtFactory;
import com.wassup741.excel.comparator.common.SpreadsheetHandleExt;

import impl.org.controlsfx.spreadsheet.GridViewSkin;
import impl.org.controlsfx.spreadsheet.SpreadsheetGridView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SelectFileView extends BorderPane {

	private Button selectFileButton;
	private SpreadsheetView table;
	private SpreadsheetGridView gridView;
	private GridCellExtFactory gridCellFactory;

	public SelectFileView() {
		init();
	}

	private void init() {

		selectFileButton = new Button("Выбрать файл");
		setTop(selectFileButton);

		table = new SpreadsheetView();
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
		gridCellFactory = new GridCellExtFactory(handle);
		setCenter(table);

	}

	public void setSelectFileAction(EventHandler<ActionEvent> handler) {
		selectFileButton.setOnAction(handler);
	}

	public void fillTable(Grid grid, List<Double> columnWidths) {
		table.setGrid(grid);
		table.setVisible(true);
		IntStream.range(0, grid.getColumnCount()).forEach(
			index -> {
				table.getColumns().get(index)
						.setPrefWidth(columnWidths.get(index));
			});

		for (TableColumn<ObservableList<SpreadsheetCell>, ?> column : gridView
				.getColumns()) {
			@SuppressWarnings("unchecked")
			TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> gridColumn = (TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>) column;
			gridColumn.setCellFactory(gridCellFactory);
		}

		// setLeft(createSlider(0, grid.getRowCount()));
	}

	protected Region createSlider(int min, int max) {
		final TextField minField = new TextField();

		minField.setPrefColumnCount(4);

		final TextField maxField = new TextField();

		maxField.setPrefColumnCount(4);

		final RangeSlider vSlider = new RangeSlider(min, max, min + 30,
				max - 50);

		vSlider.setOrientation(Orientation.VERTICAL);

		vSlider.setPrefHeight(500);

		vSlider.setBlockIncrement(10);

		vSlider.setShowTickMarks(true);

		vSlider.setShowTickLabels(true);

		minField.setText("" + vSlider.getLowValue());

		maxField.setText("" + vSlider.getHighValue());

		minField.setEditable(false);

		minField.setPromptText("Min");

		maxField.setEditable(false);

		maxField.setPromptText("Max");

		minField.textProperty().bind(
			vSlider.lowValueProperty().asString("%.0f"));

		maxField.textProperty().bind(
			vSlider.highValueProperty().asString("%.0f"));

		VBox box = new VBox(10);

		box.setPadding(new Insets(0, 0, 0, 20));

		box.setAlignment(Pos.CENTER);

		box.setFillWidth(false);

		box.getChildren().addAll(maxField, vSlider, minField);

		return box;
	}
}
