package com.wassup741.excel.comparator.select;

import java.util.function.BiConsumer;

import com.wassup741.excel.comparator.common.GridBaseExt;
import com.wassup741.excel.comparator.wizard.WizardPage;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class SelectFilesWizard extends WizardPage<SelectFilesResult> {

	@FXML
	BorderPane container;

	@FXML
	AnchorPane left;

	@FXML
	AnchorPane right;

	private Window window;

	public BorderPane getContrainer() {
		return container;
	}

	public AnchorPane getLeft() {
		return left;
	}

	public AnchorPane getRight() {
		return right;
	}

	public SelectFilesWizard(Window window) {
		super();
		this.window = window;
		setCaption("Шаг 1. Выберите файлы для сравнения");
	}

	@FXML
	public void initialize() {

		initSelectFileBlock(window, left,
			(result, sheetGrid) -> result.setSheet1(sheetGrid));
		initSelectFileBlock(window, right,
			(result, sheetGrid) -> result.setSheet2(sheetGrid));
	}

	private void initSelectFileBlock(Window stage, AnchorPane anchorPane,
			BiConsumer<SelectFilesResult, GridBaseExt> cons) {
		SelectFileController selectFileController = new SelectFileController(
				stage);
		selectFileController.sheetGridProperty().addListener(
			e -> {
				GridBaseExt sheetGrid = selectFileController
						.sheetGridProperty().get();
				SelectFilesResult result = getResult();
				if (result == null) {
					result = new SelectFilesResult();
				}
				cons.accept(result, sheetGrid);
				setResult(result);
				setReady(result.getSheet1() != null
						&& result.getSheet2() != null);
			});
		SelectFileView selectFileView = selectFileController.getView();
		anchorPane.getChildren().add(selectFileView);
		AnchorPane.setLeftAnchor(selectFileView, 0d);
		AnchorPane.setRightAnchor(selectFileView, 0d);
		AnchorPane.setTopAnchor(selectFileView, 0d);
		AnchorPane.setBottomAnchor(selectFileView, 0d);
	}

	@Override
	public Node getContent() {
		return container;
	}
}
