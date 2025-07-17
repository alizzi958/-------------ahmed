package com.wassup741.excel.comparator.link;

import java.util.function.Consumer;

import com.wassup741.excel.comparator.common.Column;
import com.wassup741.excel.comparator.common.Link;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;

public class MakeLinksController {

	@FXML
	Button addButton;

	@FXML
	Button deleteButton;

	@FXML
	TableView<Link> table;

	@FXML
	TableColumn<Link, Column> column1;

	@FXML
	TableColumn<Link, Column> column2;

	private Consumer<Link> onEditConsumer;

	@FXML
	protected void initialize() {
		deleteButton.setDisable(true);
		table.setEditable(true);
		table.getSelectionModel()
				.selectedItemProperty()
				.addListener(
					e -> deleteButton.setDisable(table.getSelectionModel()
							.getSelectedItem() == null));
	}

	public void init(ObservableList<Column> list1, ObservableList<Column> list2) {
		table.getItems().clear();
		column1.setCellFactory(ComboBoxTableCell.forTableColumn(
			new ColumnStringConverter(), list1));
		column1.setCellValueFactory(param -> param.getValue()
				.firstColumnProperty());
		column1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Link, Column>>() {
			@Override
			public void handle(CellEditEvent<Link, Column> event) {
				Link rowValue = event.getRowValue();
				rowValue.setFirstColumn(event.getNewValue());
				onEditConsumer.accept(rowValue);
			}
		});

		column2.setCellFactory(ComboBoxTableCell.forTableColumn(
			new ColumnStringConverter(), list2));
		column2.setCellValueFactory(param -> param.getValue()
				.secondColumnProperty());
		column2.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Link, Column>>() {
			@Override
			public void handle(CellEditEvent<Link, Column> event) {
				Link rowValue = event.getRowValue();
				rowValue.setSecondColumn(event.getNewValue());
				onEditConsumer.accept(rowValue);
			}
		});
	}

	public void setOnEdit(Consumer<Link> consumer) {
		this.onEditConsumer = consumer;
	}

	public ReadOnlyObjectProperty<Link> selectedLinkProperty() {
		return table.getSelectionModel().selectedItemProperty();
	}

	public static class ColumnStringConverter extends StringConverter<Column> {

		public String toString(Column object) {
			return object != null ? object.getName() : "";
		}

		public Column fromString(String string) {
			return null;
		}
	}

	public ObjectProperty<ObservableList<Link>> itemsProperty() {
		return table.itemsProperty();
	}

	@FXML
	private void addLink() {
		ObservableList<Link> items = table.getItems();
		if (items == null) {
			items = FXCollections.observableArrayList();
			table.setItems(items);
		}
		items.add(new Link());
	}

	@FXML
	private void deleteLink() {
		Link selectedItem = table.getSelectionModel().getSelectedItem();
		table.getItems().remove(selectedItem);
	}
}
