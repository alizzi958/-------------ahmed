package com.wassup741.excel.comparator.link;

import java.io.IOException;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.tools.Utils;

import com.wassup741.excel.comparator.common.Column;
import com.wassup741.excel.comparator.common.GridBaseExt;
import com.wassup741.excel.comparator.common.GridCellExtFactory;
import com.wassup741.excel.comparator.common.Link;
import com.wassup741.excel.comparator.common.SheetGridUtil;
import com.wassup741.excel.comparator.common.SpreadsheetHandleExt;
import com.wassup741.excel.comparator.select.SelectFilesResult;
import com.wassup741.excel.comparator.wizard.WizardPage;

import impl.org.controlsfx.spreadsheet.GridViewSkin;
import impl.org.controlsfx.spreadsheet.SpreadsheetGridView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MakeLinksWizard extends WizardPage<MakeLinksResult> {

	private final class ChangeColumnListener implements ChangeListener<Column> {
		@Override
		public void changed(ObservableValue<? extends Column> observable,
				Column oldValue, Column newValue) {

			checkReady(newValue);
		}
	}

	@FXML
	BorderPane root;

	@FXML
	TabPane tabPanel;

	@FXML
	AnchorPane uniqueContainer;

	@FXML
	AnchorPane comparableContainer;

	@FXML
	SplitPane split;

	@FXML
	AnchorPane leftSplit;

	@FXML
	AnchorPane rightSplit;

	private MakeLinksController uniqueLinksController;
	private MakeLinksController comparableLinksController;

	private GridBaseExt sheetGrid1;
	private GridBaseExt sheetGrid2;

	private SpreadsheetView leftTable;
	private SpreadsheetView rightTable;

	private GridCellExtFactory leftCellFactory;
	private GridCellExtFactory rightCellFactory;

	private SpreadsheetGridView leftGridView;

	private SpreadsheetGridView rightGridView;

	public MakeLinksWizard(WizardPage<SelectFilesResult> selectFilesPage) {
		super(selectFilesPage);
		setCaption("Шаг 2. Установка связей");
	}

	@FXML
	private void initialize() throws IOException {
		tabPanel.getSelectionModel()
				.selectedIndexProperty()
				.addListener(
					(observable, oldValue, newValue) -> {
						if (newValue.intValue() == 0) {
							showLink(uniqueLinksController
									.selectedLinkProperty().get());
						} else if (newValue.intValue() == 1) {
							showLink(comparableLinksController
									.selectedLinkProperty().get());
						}
					});

		initUniqueLinks();
		initComparableLinks();
		leftTable = initTable();
		leftGridView = (SpreadsheetGridView) leftTable.getSkin().getNode();
		SpreadsheetHandleExt leftHandle = new SpreadsheetHandleExt() {

			@Override
			public SpreadsheetView getView() {
				return leftTable;
			}

			@Override
			public SpreadsheetGridView getGridView() {
				return leftGridView;
			}

			@Override
			public GridViewSkin getCellsViewSkin() {
				return (GridViewSkin) (leftGridView.getSkin());
			}
		};
		leftCellFactory = new GridCellExtFactory(leftHandle);

		leftSplit.getChildren().add(leftTable);
		AnchorPane.setBottomAnchor(leftTable, 0d);
		AnchorPane.setLeftAnchor(leftTable, 0d);
		AnchorPane.setRightAnchor(leftTable, 0d);
		AnchorPane.setTopAnchor(leftTable, 0d);

		rightTable = initTable();
		rightGridView = (SpreadsheetGridView) rightTable.getSkin().getNode();
		SpreadsheetHandleExt rightHandle = new SpreadsheetHandleExt() {

			@Override
			public SpreadsheetView getView() {
				return rightTable;
			}

			@Override
			public SpreadsheetGridView getGridView() {
				return rightGridView;
			}

			@Override
			public GridViewSkin getCellsViewSkin() {
				return (GridViewSkin) (rightGridView.getSkin());
			}
		};
		rightCellFactory = new GridCellExtFactory(rightHandle);
		
		rightSplit.getChildren().add(rightTable);
		AnchorPane.setBottomAnchor(rightTable, 0d);
		AnchorPane.setLeftAnchor(rightTable, 0d);
		AnchorPane.setRightAnchor(rightTable, 0d);
		AnchorPane.setTopAnchor(rightTable, 0d);
	}

	private SpreadsheetView initTable() {
		SpreadsheetView spreadsheetView = new SpreadsheetView();
		spreadsheetView.setEditable(false);
		spreadsheetView.setVisible(false);
		return spreadsheetView;
	}

	private void checkReady(Column newValue) {
		setReady(newValue != null && checkReady());
	}

	private boolean checkReady() {
		MakeLinksResult result = getResult();
		boolean ready = result != null && result.getComparableLinks() != null
				&& result.getComparableLinks().size() > 0
				&& result.getUniqueLinks() != null
				&& result.getUniqueLinks().size() > 0;
		if (ready) {
			for (Link link : result.getUniqueLinks()) {
				ready = link.getFirstColumn() != null
						&& link.getSecondColumn() != null;
			}
			for (Link link : result.getComparableLinks()) {
				ready = link.getFirstColumn() != null
						&& link.getSecondColumn() != null;
			}
		}
		return ready;
	}

	@Override
	public void initialState() {
		super.initialState();

		SelectFilesResult selectFilesResult = (SelectFilesResult) getDependencies()
				.get(0).getResult();
		leftTable.setVisible(false);
		rightTable.setVisible(false);
		this.sheetGrid1 = selectFilesResult.getSheet1();
		this.sheetGrid2 = selectFilesResult.getSheet2();

		ObservableList<Column> list1 = gridToColumns(sheetGrid1);
		ObservableList<Column> list2 = gridToColumns(sheetGrid2);

		uniqueLinksController.init(list1, list2);
		comparableLinksController.init(list1, list2);

		tabPanel.getSelectionModel().select(0);
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

	// TODO ОБОБЩИТЬ МЕТОДЫ
	private void initUniqueLinks() throws IOException {
		FXMLLoader makeLinksLoader = new FXMLLoader(getClass().getResource(
			"MakeLinks.fxml"));
		BorderPane uniqueLinksView = makeLinksLoader.load();

		uniqueLinksController = makeLinksLoader.getController();
		uniqueLinksController.setOnEdit(this::showLink);
		uniqueLinksController.selectedLinkProperty().addListener(e -> {
			showLink(uniqueLinksController.selectedLinkProperty().get());
		});
		uniqueLinksController.itemsProperty().get()
				.addListener(new ListChangeListener<Link>() {

					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends Link> c) {
						MakeLinksResult result = getResult();
						c.next();
						if (c.wasAdded() || c.wasUpdated()) {
							setReady(false);
							Link link = c.getAddedSubList().get(0);
							link.firstColumnProperty().addListener(
								new ChangeColumnListener());
							link.secondColumnProperty().addListener(
								new ChangeColumnListener());
						}
						if (c.wasRemoved()) {
							setReady(checkReady());
						}
						if (result == null) {
							result = new MakeLinksResult();
						}
						result.setUniqueLinks(uniqueLinksController
								.itemsProperty().get());
						setResult(result);
					}
				});
		uniqueContainer.getChildren().clear();
		uniqueContainer.getChildren().add(uniqueLinksView);
		AnchorPane.setBottomAnchor(uniqueLinksView, 0d);
		AnchorPane.setLeftAnchor(uniqueLinksView, 0d);
		AnchorPane.setRightAnchor(uniqueLinksView, 0d);
		AnchorPane.setTopAnchor(uniqueLinksView, 0d);

	}

	private void initComparableLinks() throws IOException {
		FXMLLoader makeLinksLoader = new FXMLLoader(getClass().getResource(
			"MakeLinks.fxml"));
		BorderPane comparableLinksView = makeLinksLoader.load();

		comparableLinksController = makeLinksLoader.getController();
		comparableLinksController.setOnEdit(this::showLink);
		comparableLinksController.selectedLinkProperty().addListener(e -> {
			showLink(comparableLinksController.selectedLinkProperty().get());
		});
		comparableLinksController.itemsProperty().get()
				.addListener(new ListChangeListener<Link>() {
					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends Link> c) {
						MakeLinksResult result = getResult();
						c.next();
						if (c.wasAdded() || c.wasUpdated()) {
							setReady(false);
							Link link = c.getAddedSubList().get(0);
							link.firstColumnProperty().addListener(
								new ChangeColumnListener());
							link.secondColumnProperty().addListener(
								new ChangeColumnListener());
						}
						if (c.wasRemoved()) {
							setReady(checkReady());
						}
						if (result == null) {
							result = new MakeLinksResult();
						}
						result.setComparableLinks(comparableLinksController
								.itemsProperty().get());
						setResult(result);
					}
				});
		comparableContainer.getChildren().clear();
		comparableContainer.getChildren().add(comparableLinksView);
		AnchorPane.setBottomAnchor(comparableLinksView, 0d);
		AnchorPane.setLeftAnchor(comparableLinksView, 0d);
		AnchorPane.setRightAnchor(comparableLinksView, 0d);
		AnchorPane.setTopAnchor(comparableLinksView, 0d);
	}

	private void showLink(Link link) {
		leftTable.setVisible(false);
		rightTable.setVisible(false);
		if (link != null) {
			Column firstColumn = link.getFirstColumn();
			showColumn(firstColumn, sheetGrid1, leftTable, leftCellFactory,
				leftGridView);
			Column secondColumn = link.getSecondColumn();
			showColumn(secondColumn, sheetGrid2, rightTable, rightCellFactory,
				rightGridView);
		}
	}

	private void showColumn(Column column, GridBaseExt grid,
			SpreadsheetView spreadsheet, GridCellExtFactory gridCellFactory,
			SpreadsheetGridView gridView) {
		if (column != null) {
			spreadsheet.setVisible(true);
			GridBaseExt columnGrid = SheetGridUtil.createColumnGrid(column,
				grid);
			spreadsheet.setGrid(columnGrid);

			spreadsheet.getColumns().get(0)
					.setPrefWidth(columnGrid.getColumnWidths().get(0));

			for (TableColumn<ObservableList<SpreadsheetCell>, ?> tableColumn : gridView
					.getColumns()) {
				@SuppressWarnings("unchecked")
				TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> gridColumn = (TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>) tableColumn;
				gridColumn.setCellFactory(gridCellFactory);
			}

		}
	}

	@Override
	public Node getContent() {
		return root;
	}
}
