package com.wassup741.excel.comparator.common;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Column {

	private StringProperty name = new SimpleStringProperty();
	private IntegerProperty index = new SimpleIntegerProperty();

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public int getIndex() {
		return index.get();
	}

	public void setIndex(Integer index) {
		this.index.set(index);
	}

	public IntegerProperty indexProperty() {
		return index;
	}

	@Override
	public String toString() {
		return name.get();
	}
}
