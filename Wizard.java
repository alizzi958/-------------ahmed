package com.wassup741.excel.comparator.wizard;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Wizard {

	@FXML
	AnchorPane content;

	@FXML
	HBox controlPanel;

	@FXML
	Button toBeginButton;

	@FXML
	Button prevButton;

	@FXML
	Button nextButton;

	@FXML
	TextFlow caption;

	@FXML
	BorderPane root;

	private List<WizardPage<?>> pages = new ArrayList<>();
	private IntegerProperty currentPageIndex = new SimpleIntegerProperty(0);

	@FXML
	private void initialize() {

		toBeginButton.setVisible(false);
		prevButton.setVisible(false);
		nextButton.setVisible(true);

		currentPageIndex.addListener((observable, oldValue, newValue) -> {
			toBeginButton.setVisible(newValue.intValue() != 0
					&& newValue.intValue() != 1);
			prevButton.setVisible(newValue.intValue() != 0);
			nextButton.setVisible(newValue.intValue() != pages.size() - 1);
		});
	}

	@FXML
	public void toNextPage() {
		toPage(getCurrentPageIndex() + 1);
	}

	@FXML
	public void toPrevPage() {
		toPage(getCurrentPageIndex() - 1);
	}

	@FXML
	public void toBeginPage() {
		toPage(0);
	}

	public int getCurrentPageIndex() {
		return currentPageIndex.get();
	}

	public WizardPage<?> getCurrentPage() {
		if (pages.size() > currentPageIndex.get()) {
			return pages.get(getCurrentPageIndex());
		} else {
			return null;
		}
	}

	public boolean addPage(WizardPage<?> page) {
		if (page != null && !pages.contains(page)) {
			page.readyProperty().addListener(this::readyChanged);
			return pages.add(page);
		} else {
			return false;
		}
	}

	private void readyChanged(ObservableValue<? extends Boolean> observable,
			Boolean oldValue, Boolean newValue) {
		isNextReady();
	}

	private void toPage(int index) {
		if (checkBounds(index)) {
			WizardPage<?> page = pages.get(index);

			currentPageIndex.set(index);
			content.getChildren().clear();
			if (isDependeciesDirty(page)) {
				page.initialState();
			}
			Node node = page.getContent();
			content.getChildren().add(node);
			AnchorPane.setBottomAnchor(node, 0d);
			AnchorPane.setTopAnchor(node, 0d);
			AnchorPane.setLeftAnchor(node, 0d);
			AnchorPane.setRightAnchor(node, 0d);
			caption.getChildren().clear();
			caption.getChildren().add(new Text(page.getCaption()));

			isNextReady();
		}
	}

	private boolean checkBounds(int index) {
		return index >= 0 && index < pages.size();
	}

	public void flushPages() {
		toBeginPage();
	}

	private void isNextReady() {
		int nextPageIndex = getCurrentPageIndex() + 1;
		if (checkBounds(nextPageIndex)) {
			WizardPage<?> nextPage = pages.get(nextPageIndex);
			boolean isNextReady = true;
			for (WizardPage<?> page : nextPage.getDependencies()) {
				isNextReady = isNextReady && page.isReady();
			}
			nextButton.setDisable(!isNextReady);
		}
	}

	private boolean isDependeciesDirty(WizardPage<?> page) {
		boolean result = false;
		for (WizardPage<?> iter : page.getDependencies()) {
			result = result || iter.isDirty();
		}
		return result;
	}
}
