package com.wassup741.excel.comparator2;

import org.controlsfx.control.PopOver;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChooseRangePopup extends PopOver {

	private NumberField endField;
	private NumberField startField;

	public ChooseRangePopup() {
		setDetached(false);
		setAutoHide(true);
		arrowSizeProperty().set(0);
		arrowIndentProperty().set(0);
		arrowLocationProperty().set(ArrowLocation.TOP_LEFT);

		Label infoLabel = new Label("Вы можете ограничить диапазон строк");
		infoLabel.setWrapText(false);

		Label startLabel = new Label("с:");
		startField = new NumberField();
		startField.setPrefWidth(50);

		Label endLabel = new Label("по:");
		endField = new NumberField();
		endField.setPrefWidth(50);

		HBox hBox = new HBox(10);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.getChildren().add(startLabel);
		hBox.getChildren().add(startField);
		hBox.getChildren().add(endLabel);
		hBox.getChildren().add(endField);

		VBox vBox = new VBox(0);
		vBox.getChildren().add(infoLabel);
		vBox.getChildren().add(hBox);

		vBox.setPadding(new Insets(8));

		setContentNode(vBox);

	}

	public int getStart() {
		String text = startField.getText();
		return Integer.valueOf(text);
	}

	public int getEnd() {
		String text = endField.getText();
		return Integer.valueOf(text);
	}

	public static class NumberField extends TextField {
		@Override
		public void replaceText(int start, int end, String text) {
			if (text.matches("[0-9]*")) {
				super.replaceText(start, end, text);
			}
		}

		@Override
		public void replaceSelection(String text) {
			if (text.matches("[0-9]*")) {
				super.replaceSelection(text);
			}
		}
	}

}
