package com.wassup741.excel.comparator2;

import java.util.List;
import java.util.function.BiConsumer;

import org.controlsfx.control.CheckListView;
import org.controlsfx.control.CheckModel;
import org.controlsfx.control.PopOver;

import com.wassup741.excel.comparator.common.Column;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ChooseColumnsPopup extends PopOver {

	private CheckListView<Column> columnsList;
	private BiConsumer<Column, Boolean> checkListener;

	public ChooseColumnsPopup() {
		setDetached(false);
		setAutoHide(true);
		arrowSizeProperty().set(0);
		arrowIndentProperty().set(0);
		arrowLocationProperty().set(ArrowLocation.TOP_LEFT);

		columnsList = new CheckListView<>();
		columnsList.setPrefHeight(200);
		columnsList.setPrefWidth(100);
		setContentNode(columnsList);
	}

	public void setColumns(ObservableList<Column> columns) {
		columnsList.setItems(columns);
		columnsList.getCheckModel().checkAll();
		columnsList.getCheckModel().getCheckedItems()
				.addListener(new ListChangeListener<Column>() {
					@Override
					public void onChanged(Change<? extends Column> c) {
						while (c.next()) {
							if (checkListener == null) {
								return;
							}
							if (c.wasRemoved()) {
								List<? extends Column> removed = c.getRemoved();
								for (Column column : removed) {
									checkListener.accept(column, false);
								}
							} else if (c.wasAdded()) {
								List<? extends Column> added = c
										.getAddedSubList();
								for (Column column : added) {
									checkListener.accept(column, true);
								}
							}
						}
					}
				});
	}

	public void setCheckListener(BiConsumer<Column, Boolean> checkListener) {
		this.checkListener = checkListener;
	}

	public CheckModel<Column> checkModel() {
		return columnsList.getCheckModel();
	}
}
