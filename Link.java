package com.wassup741.excel.comparator.common;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Link {

	private ObjectProperty<Column> column1 = new SimpleObjectProperty<>();
	private ObjectProperty<Column> column2 = new SimpleObjectProperty<>();

	public Column getFirstColumn() {
		return column1.get();
	}

	public void setFirstColumn(Column column) {
		column1.set(column);
	}

	public ObjectProperty<Column> firstColumnProperty() {
		return column1;
	}

	public Column getSecondColumn() {
		return column2.get();
	}

	public void setSecondColumn(Column column) {
		column2.set(column);
	}

	public ObjectProperty<Column> secondColumnProperty() {
		return column2;
	}

}
