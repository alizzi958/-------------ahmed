package com.wassup741.excel.comparator2;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.tools.Utils;

import com.sun.javafx.css.StyleManager;
import com.wassup741.excel.comparator.common.Column;
import com.wassup741.excel.comparator.common.GridBaseExt;
import com.wassup741.excel.comparator.common.Link;
import com.wassup741.excel.comparator.common.SheetGridUtil;
import com.wassup741.excel.comparator.compare.CompareResult;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MainController {

	@FXML
	AnchorPane leftSplit;
	@FXML
	AnchorPane rightSplit;
	@FXML
	TableView<Link> uniqueTable;
	@FXML
	TableColumn<Link, Column> uniqueLeftColumn;
	@FXML
	TableColumn<Link, Column> uniqueRightColumn;
	@FXML
	TableView<Link> compareTable;
	@FXML
	TableColumn<Link, Column> compareLeftColumn;
	@FXML
	TableColumn<Link, Column> compareRightColumn;
	@FXML
	Button addUniqueLInk;
	@FXML
	Button removeUniqueLink;
	@FXML
	Button addCompareLInk;
	@FXML
	Button removeCompareLink;
	@FXML
	Button compareButton;
	@FXML
	Button browseLeft;
	@FXML
	Button refreshLeft;
	@FXML
	Button browseRight;
	@FXML
	Button refreshRight;
	@FXML
	TabPane tabPanel;
	@FXML
	AnchorPane leftFileContainer;
	@FXML
	AnchorPane rightFileContainer;
	@FXML
	Label leftFileName;
	@FXML
	Label rightFileName;
	@FXML
	Button chooseColumnsLeft;
	@FXML
	Button chooseColumnsRight;
	@FXML
	Button chooseRangeLeft;
	@FXML
	Button chooseRangeRight;
	@FXML
	AnchorPane leftResultContainer;
	@FXML
	AnchorPane rightResultContainer;
	@FXML
	Button chooseColumnsLeftResult;
	@FXML
	Button chooseColumnsRightResult;
	@FXML
	Label leftResultFileName;
	@FXML
	Label rightResultFileName;
	@FXML
	HBox resultToolbar;
	@FXML
	Button saveButton;

	private MainModel model;
	private Window window;
	private FileChooser fileChooser;
	private FileChooser saveFileChooser;
	private SpreadsheetWidget leftTable;
	private SpreadsheetWidget rightTable;
	private AfterRightFill afterRightFill;
	private AfterLeftFill afterLeftFill;
	private ChooseColumnsPopup leftColumnsPopup;
	private ChooseColumnsPopup rightColumnsPopup;
	private ChooseColumnsPopup leftResultColumnsPopup;
	private ChooseColumnsPopup rightResultColumnsPopup;
	private ChooseRangePopup chooseRangeLeftPopup;
	private ChooseRangePopup chooseRangeRightPopup;
	private CompareResult compareResult;
	private SpreadsheetWidget leftResultAll;
	private SpreadsheetWidget rightResultAll;
	private SpreadsheetWidget leftResultConflict;
	private SpreadsheetWidget rightResultConflict;
	private SpreadsheetWidget leftResultBlank;
	private SpreadsheetWidget rightResultBlank;
	private ToggleButton allButton;
	private ToggleButton conflictButton;
	private ToggleButton blankButton;
	private BooleanProperty allScrollBinding = new SimpleBooleanProperty(false);
	private BooleanProperty conflictScrollBinding = new SimpleBooleanProperty(
			false);
	private BooleanProperty blankScrollBinding = new SimpleBooleanProperty(
			false);

	public MainController(Window window) {
		this.window = window;

		model = new MainModel();

		fileChooser = new FileChooser();
		fileChooser.setTitle("Выберите файл");
		ExtensionFilter excelFilter = new ExtensionFilter("Файлы Excel",
				"*.xls", "*.xlsx");
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Все файлы", "*.*"), excelFilter);
		fileChooser.setSelectedExtensionFilter(excelFilter);

		saveFileChooser = new FileChooser();
		saveFileChooser.setTitle("Сохранение");
		saveFileChooser.setInitialFileName("Результат сравнения");
		ExtensionFilter xlsxFilter = new ExtensionFilter("Книга Excel",
				"*.xlsx");
		ExtensionFilter xlsFilter = new ExtensionFilter("Книга Excel 97-2003",
				"*.xls");
		saveFileChooser.getExtensionFilters().addAll(xlsFilter, xlsxFilter);
		saveFileChooser.setSelectedExtensionFilter(xlsFilter);

		afterLeftFill = new AfterLeftFill();
		afterRightFill = new AfterRightFill();

		leftColumnsPopup = new ChooseColumnsPopup();
		rightColumnsPopup = new ChooseColumnsPopup();
		leftResultColumnsPopup = new ChooseColumnsPopup();
		rightResultColumnsPopup = new ChooseColumnsPopup();

		chooseRangeLeftPopup = new ChooseRangePopup();
		chooseRangeRightPopup = new ChooseRangePopup();
	}

	@FXML
	private void initialize() {
		leftTable = new SpreadsheetWidget();
		setNode(leftTable.getTable(), leftFileContainer);
		rightTable = new SpreadsheetWidget();
		setNode(rightTable.getTable(), rightFileContainer);

		leftResultAll = new SpreadsheetWidget();
		rightResultAll = new SpreadsheetWidget();
		leftResultConflict = new SpreadsheetWidget();
		rightResultConflict = new SpreadsheetWidget();
		leftResultBlank = new SpreadsheetWidget();
		rightResultBlank = new SpreadsheetWidget();

		setBrowseListener(browseLeft, leftTable, afterLeftFill);
		setBrowseListener(browseRight, rightTable, afterRightFill);

		setRefreshFileListener(refreshLeft, leftTable);
		setRefreshFileListener(refreshRight, rightTable);

		initLinkTable(uniqueTable, removeUniqueLink, addUniqueLInk);
		initLinkTable(compareTable, removeCompareLink, addCompareLInk);

		initLinksPlaceholders(uniqueTable,
			"Добавьте связи между уникальными столбцами");
		initLinksPlaceholders(compareTable,
			"Добавьте связи между столбцами, которые хотите сравнить");

		setChooseColumnsListener(chooseColumnsLeft, leftColumnsPopup,
			leftTable);
		setChooseColumnsListener(chooseColumnsRight, rightColumnsPopup,
			rightTable);
		setChooseColumnsListener(chooseColumnsLeftResult,
			leftResultColumnsPopup, leftResultAll, leftResultConflict,
			leftResultBlank);
		setChooseColumnsListener(chooseColumnsRightResult,
			rightResultColumnsPopup, rightResultAll, rightResultConflict,
			rightResultBlank);

		setChooseRangeListener(chooseRangeLeft, chooseRangeLeftPopup);
		setChooseRangeListener(chooseRangeRight, chooseRangeRightPopup);

		setCompareListener();

		setSaveListener();
		saveButton.setDisable(true);

		initResultToolbar();
	}

	private void setSaveListener() {
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				File file = saveFileChooser.showSaveDialog(window);
				if (file != null) {
					Grid leftGrid = null;
					Grid rightGrid = null;
					if (allButton.selectedProperty().get()) {
						leftGrid = leftResultAll.getTable().getGrid();
						rightGrid = rightResultAll.getTable().getGrid();
					} else if (conflictButton.selectedProperty().get()) {
						leftGrid = leftResultConflict.getTable().getGrid();
						rightGrid = rightResultConflict.getTable().getGrid();
					} else if (blankButton.selectedProperty().get()) {
						leftGrid = leftResultBlank.getTable().getGrid();
						rightGrid = rightResultBlank.getTable().getGrid();
					}

					model.saveToFile(leftGrid,
						leftResultColumnsPopup.checkModel().getCheckedItems(),
						rightGrid,
						rightResultColumnsPopup.checkModel().getCheckedItems(),
						file.toPath());
				}
			}
		});
	}

	private void initResultToolbar() {
		allButton = new SegmentableButton("Все");
		setSelectResultListener(allButton, leftResultAll, leftResultContainer,
			rightResultAll, rightResultContainer, allScrollBinding);

		conflictButton = new SegmentableButton("Конфликтные");
		setSelectResultListener(conflictButton, leftResultConflict,
			leftResultContainer, rightResultConflict, rightResultContainer,
			conflictScrollBinding);

		blankButton = new SegmentableButton("Пустые");
		setSelectResultListener(blankButton, leftResultBlank,
			leftResultContainer, rightResultBlank, rightResultContainer,
			blankScrollBinding);

		SegmentedButton gridsViewToolbar = new SegmentedButton(allButton,
				conflictButton, blankButton);
		resultToolbar.getChildren().add(gridsViewToolbar);

	}

	private void setSelectResultListener(ToggleButton button,
			SpreadsheetWidget left, AnchorPane leftContainer,
			SpreadsheetWidget right, AnchorPane rightContainer,
			BooleanProperty isListenScroll) {
		button.selectedProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue) {
						setNode(left.getTable(), leftContainer);
						setNode(right.getTable(), rightContainer);
						if (!isListenScroll.getValue()) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									if (left.getScrollProperty() != null) {
										left.getScrollProperty()
												.bindBidirectional(
													right.getScrollProperty());
										isListenScroll.setValue(true);
									}
								}
							});
						}
					}
				});
	}

	private void setCompareListener() {
		compareButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Grid leftGrid = leftTable.getTable().getGrid();
				if (!checkGrid(leftGrid, "Левый файл не выбран.")) {
					return;
				}

				Grid rightGrid = rightTable.getTable().getGrid();
				if (!checkGrid(rightGrid, "Правый файл не выбран.")) {
					return;
				}

				List<Link> uniqueLinks = uniqueTable.getItems();
				if (!checkLinks(uniqueLinks,
					"Уникальные связи отсутствуют или заполнены частично.")) {
					return;
				}

				List<Link> compareLinks = compareTable.getItems();
				if (!checkLinks(compareLinks,
					"Связи для сравнения отсутствуют или заполнены частично.")) {
					return;
				}

				compareResult = model.compare(leftGrid, rightGrid, uniqueLinks,
					compareLinks);

				List<Double> leftWidths = ((GridBaseExt) leftTable.getTable()
						.getGrid()).getColumnWidths();
				leftResultAll.fill(compareResult.firstMain.left.all,
					leftWidths);
				leftResultConflict.fill(compareResult.firstMain.left.confilct,
					leftWidths);
				leftResultBlank.fill(compareResult.firstMain.left.blank,
					leftWidths);

				List<Double> rightWidths = ((GridBaseExt) rightTable.getTable()
						.getGrid()).getColumnWidths();
				rightResultAll.fill(compareResult.firstMain.right.all,
					rightWidths);
				rightResultConflict.fill(compareResult.firstMain.right.confilct,
					rightWidths);
				rightResultBlank.fill(compareResult.firstMain.right.blank,
					rightWidths);

				leftResultColumnsPopup.checkModel().clearChecks();
				rightResultColumnsPopup.checkModel().clearChecks();

				for (Link link : uniqueLinks) {
					leftResultColumnsPopup.checkModel()
							.check(link.getFirstColumn());
					rightResultColumnsPopup.checkModel()
							.check(link.getSecondColumn());
				}

				for (Link link : compareLinks) {
					leftResultColumnsPopup.checkModel()
							.check(link.getFirstColumn());
					rightResultColumnsPopup.checkModel()
							.check(link.getSecondColumn());
				}

				tabPanel.selectionModelProperty().get().select(1);
				allButton.setSelected(true);
				saveButton.setDisable(false);
			}
		});
	}

	private boolean checkLinks(List<Link> links, String errorMessage) {
		if (links.isEmpty()) {
			showErrorDlg(errorMessage);
			return false;
		}
		Iterator<Link> iterator = links.iterator();
		while (iterator.hasNext()) {
			Link link = iterator.next();
			if (link.getFirstColumn() == null
					&& link.getSecondColumn() == null) {
				iterator.remove();
			} else if (link.getFirstColumn() == null
					|| link.getSecondColumn() == null) {
				showErrorDlg(errorMessage);
				return false;
			}
		}
		return true;
	}

	private boolean checkGrid(Grid grid, String errorMessage) {
		if (grid.getColumnCount() > 0 && grid.getRowCount() > 0) {
			return true;
		} else {
			showErrorDlg(errorMessage);
			return false;
		}
	}

	private void showErrorDlg(String message) {
		Alert dlg = new Alert(AlertType.ERROR, message);
		dlg.initModality(Modality.APPLICATION_MODAL);
		dlg.setHeaderText("Ошибка");
		dlg.showAndWait();
	}

	private void setChooseColumnsListener(Button button,
			ChooseColumnsPopup popup, SpreadsheetWidget... table) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (popup.isShowing()) {
					popup.hide();
				} else {
					popup.show(button);
				}
			}
		});
		popup.setCheckListener(new BiConsumer<Column, Boolean>() {
			@Override
			public void accept(Column column, Boolean checked) {
				for (SpreadsheetWidget widget : table) {
					widget.getGridView().getColumns().get(column.getIndex())
							.setVisible(checked);
				}
			}
		});
	}

	private void setChooseRangeListener(Button button, ChooseRangePopup popup) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (popup.isShowing()) {
					popup.hide();
				} else {
					popup.show(button);
				}
			}
		});
	}

	private void initLinksPlaceholders(TableView<Link> tableView, String text) {
		Label label = new Label(text);
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setFont(new Font(15));
		label.setPadding(new Insets(0, 8, 0, 8));
		tableView.setPlaceholder(label);
	}

	private void initLinkTable(TableView<Link> tableView, Button removeButton,
			Button addButton) {
		tableView.setEditable(true);
		tableView.getSelectionModel().selectedItemProperty()
				.addListener(e -> removeButton.setDisable(
					tableView.getSelectionModel().getSelectedItem() == null));
		setAddLinkListener(addButton, tableView);
		setRemoveLinkListener(removeButton, tableView);
	}

	private void setNode(Node node, AnchorPane anchorPane) {
		anchorPane.getChildren().clear();
		anchorPane.getChildren().add(node);
		AnchorPane.setLeftAnchor(node, 0d);
		AnchorPane.setRightAnchor(node, 0d);
		AnchorPane.setTopAnchor(node, 0d);
		AnchorPane.setBottomAnchor(node, 0d);
	}

	private void setBrowseListener(Button button, SpreadsheetWidget spreadsheet,
			Runnable afterFill) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				File file = fileChooser.showOpenDialog(window);
				if (file != null) {
					Sheet sheet = model.readFile(file);
					GridBaseExt grid = SheetGridUtil.sheetToGrid(sheet);
					spreadsheet.fill(grid, grid.getColumnWidths());
					spreadsheet.setFileName(file.getAbsolutePath());
					afterFill.run();
				}
			}
		});
	}

	private void setRefreshFileListener(Button button,
			SpreadsheetWidget spreadsheet) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String fileName = spreadsheet.getFileName();
				File file = new File(fileName);
				if (file != null && file.exists()) {
					Sheet sheet = model.readFile(file);
					GridBaseExt grid = SheetGridUtil.sheetToGrid(sheet);
					spreadsheet.fill(grid, grid.getColumnWidths());
					spreadsheet.setFileName(file.getAbsolutePath());
				}
			}
		});
	}

	private void setAddLinkListener(Button button, TableView<Link> table) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ObservableList<Link> items = table.getItems();
				if (items == null) {
					items = FXCollections.observableArrayList();
					table.setItems(items);
				}
				items.add(new Link());
			}
		});
	}

	private void setRemoveLinkListener(Button button, TableView<Link> table) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Link selectedItem = table.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					table.getItems().remove(selectedItem);
				}
			}
		});
	}

	public void initLinksBlock(TableColumn<Link, Column> column,
			ObservableList<Column> list,
			BiConsumer<Link, Column> columnConsumer,
			Callback<CellDataFeatures<Link, Column>, ObservableValue<Column>> cellValueFactory) {
		column.setCellFactory(ComboBoxTableCell
				.forTableColumn(new ColumnStringConverter(), list));

		column.setCellValueFactory(cellValueFactory);
		column.setOnEditCommit(
			new EventHandler<TableColumn.CellEditEvent<Link, Column>>() {
				@Override
				public void handle(CellEditEvent<Link, Column> event) {
					Link rowValue = event.getRowValue();
					columnConsumer.accept(rowValue, event.getNewValue());
				}
			});
	}

	private ObservableList<Column> gridToColumns(Grid grid) {
		ObservableList<Column> columns = FXCollections.observableArrayList();
		IntStream.range(0, grid.getColumnCount()).forEach(i -> {
			Column column = new Column();
			column.setIndex(i);
			column.setName(Utils.getExcelLetterFromNumber(i));
			columns.add(column);
		});
		return columns;
	}

	public static class ColumnStringConverter extends StringConverter<Column> {

		public String toString(Column object) {
			return object != null ? object.getName() : "";
		}

		public Column fromString(String string) {
			return null;
		}
	}

	public class AfterLeftFill implements Runnable {
		@Override
		public void run() {
			String fileName = Paths.get(leftTable.getFileName()).getFileName()
					.toString();
			leftFileName.setText(fileName);
			leftResultFileName.setText(fileName);
			ObservableList<Column> columns = gridToColumns(
				leftTable.getTable().getGrid());
			initLinksBlock(uniqueLeftColumn, columns,
				(link, column) -> link.setFirstColumn(column),
				param -> param.getValue().firstColumnProperty());
			initLinksBlock(compareLeftColumn, columns,
				(link, column) -> link.setFirstColumn(column),
				param -> param.getValue().firstColumnProperty());

			leftColumnsPopup.setColumns(columns);
			leftResultColumnsPopup.setColumns(columns);
		}
	}

	public class AfterRightFill implements Runnable {
		@Override
		public void run() {
			String fileName = Paths.get(rightTable.getFileName()).getFileName()
					.toString();
			rightFileName.setText(fileName);
			rightResultFileName.setText(fileName);
			ObservableList<Column> columns = gridToColumns(
				rightTable.getTable().getGrid());
			initLinksBlock(uniqueRightColumn, columns,
				(link, column) -> link.setSecondColumn(column),
				param -> param.getValue().secondColumnProperty());
			initLinksBlock(compareRightColumn, columns,
				(link, column) -> link.setSecondColumn(column),
				param -> param.getValue().secondColumnProperty());

			rightColumnsPopup.setColumns(columns);
			rightResultColumnsPopup.setColumns(columns);
		}
	}

	public static class SegmentableButton extends ToggleButton {
		@Override
		public void fire() {
			if (getToggleGroup() == null || !isSelected()) {
				super.fire();
			}
		}

		public SegmentableButton() {
			super();
		}

		public SegmentableButton(String text, Node graphic) {
			super(text, graphic);
		}

		public SegmentableButton(String text) {
			super(text);
			StyleManager.getInstance().addUserAgentStylesheet(
				SegmentedButton.class.getResource("segmentedbutton.css")
						.toExternalForm());
		}
	}

}
