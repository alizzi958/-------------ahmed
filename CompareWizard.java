package com.wassup741.excel.comparator.compare;

import java.io.File;
import java.util.List;

import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.wassup741.excel.comparator.common.Link;
import com.wassup741.excel.comparator.link.MakeLinksResult;
import com.wassup741.excel.comparator.link.MakeLinksWizard;
import com.wassup741.excel.comparator.select.SelectFilesResult;
import com.wassup741.excel.comparator.select.SelectFilesWizard;
import com.wassup741.excel.comparator.wizard.WizardPage;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CompareWizard extends WizardPage<CompareResult> {

	@FXML
	BorderPane root;

	@FXML
	AnchorPane left;

	@FXML
	AnchorPane right;

	@FXML
	AnchorPane top;

	private CompareModel model = new CompareModel();

	private ToggleButton allButton;

	private ToggleButton conflictButton;

	private ToggleButton blankButton;

	private Stage window;

	private FileChooser saveFileChooser;

	private Grid leftGrid;

	private Grid rightGrid;

	public CompareWizard(Stage window, SelectFilesWizard selectFiles,
			MakeLinksWizard makeLinks) {
		super(selectFiles, makeLinks);
		this.window = window;
		setCaption("Шаг 3. Результат");
	}

	@FXML
	private void initialize() {
		allButton = new SegmentableButton("Все");
		allButton.selectedProperty().addListener(
			(observable, oldValue, newValue) -> {
				if (newValue) {
					if (getResult() != null) {
						show(getResult().firstMain.mainPartialColumns.all,
							getResult().firstMain.dependentPartialColumns.all);
					}
				}
			});

		conflictButton = new SegmentableButton("Конфликтные");
		conflictButton
				.selectedProperty()
				.addListener(
					(observable, oldValue, newValue) -> {
						if (newValue) {
							show(
								getResult().firstMain.mainPartialColumns.confilct,
								getResult().firstMain.dependentPartialColumns.confilct);
						}
					});

		blankButton = new SegmentableButton("Пустые");
		blankButton.selectedProperty().addListener(
			(observable, oldValue, newValue) -> {
				if (newValue) {
					show(getResult().firstMain.mainPartialColumns.blank,
						getResult().firstMain.dependentPartialColumns.blank);
				}
			});

		SegmentedButton gridsViewToolbar = new SegmentedButton(allButton,
				conflictButton, blankButton);
		top.getChildren().add(gridsViewToolbar);
		AnchorPane.setTopAnchor(gridsViewToolbar, 0d);
		AnchorPane.setRightAnchor(gridsViewToolbar, 0d);

		saveFileChooser = new FileChooser();
		saveFileChooser.setTitle("Сохранение");
		saveFileChooser.setInitialFileName("Результат сравнения");
		ExtensionFilter xlsxFilter = new ExtensionFilter("Книга Excel",
				"*.xlsx");
		ExtensionFilter xlsFilter = new ExtensionFilter("Книга Excel 97-2003",
				"*.xls");
		saveFileChooser.getExtensionFilters().addAll(xlsFilter, xlsxFilter);
		saveFileChooser.setSelectedExtensionFilter(xlsFilter);

		Button saveButton = new Button("Сохранить");
		top.getChildren().add(saveButton);
		saveButton.setOnAction(e -> {
			File file = saveFileChooser.showSaveDialog(window);
			if (file != null) {
				model.saveToFile(leftGrid, rightGrid, file.toPath());
			}
		});
	}

	@Override
	protected void initialState() {
		super.initialState();
		SelectFilesResult selectFilesResult = (SelectFilesResult) getDependencies()
				.get(0).getResult();
		MakeLinksResult makeLinksResult = (MakeLinksResult) getDependencies()
				.get(1).getResult();
		left.getChildren().clear();
		right.getChildren().clear();

		Grid grid1 = selectFilesResult.getSheet1();
		Grid grid2 = selectFilesResult.getSheet2();

		List<Link> uniqueLinks = makeLinksResult.getUniqueLinks();
		List<Link> comparableLinks = makeLinksResult.getComparableLinks();

		CompareResult compare = model.compare(grid1, grid2, uniqueLinks,
			comparableLinks);
		setResult(compare);

		if (getResult() != null) {
			show(getResult().firstMain.mainPartialColumns.all,
				getResult().firstMain.dependentPartialColumns.all);
		}
		allButton.setSelected(true);
	}

	private void show(Grid leftGrid, Grid rightGrid) {
		this.leftGrid = leftGrid;
		SpreadsheetView spreadsheetLeft = leftGrid == null ? null
				: new SpreadsheetView(leftGrid);
		if (spreadsheetLeft != null) {
			spreadsheetLeft.getColumns().forEach(column -> {
				column.setPrefWidth(200);
			});
		}
		addToAnchor(left, spreadsheetLeft);

		this.rightGrid = rightGrid;
		SpreadsheetView spreadsheetRight = rightGrid == null ? null
				: new SpreadsheetView(rightGrid);
		if (spreadsheetRight != null) {
			spreadsheetRight.getColumns().forEach(column -> {
				column.setPrefWidth(200);
			});
		}
		addToAnchor(right, spreadsheetRight);
	}

	private void addToAnchor(AnchorPane pane, Node node) {
		pane.getChildren().clear();
		if (node != null) {
			pane.getChildren().add(node);
			AnchorPane.setBottomAnchor(node, 0d);
			AnchorPane.setTopAnchor(node, 0d);
			AnchorPane.setLeftAnchor(node, 0d);
			AnchorPane.setRightAnchor(node, 0d);
		}
	}

	@Override
	public Node getContent() {
		return root;
	}

	public class SegmentableButton extends ToggleButton {
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
		}
	}

}
