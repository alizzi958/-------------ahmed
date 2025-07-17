package com.wassup741.excel.comparator.wizard;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class WizardPageResult<T> {
	private ObjectProperty<T> value = new SimpleObjectProperty<T>();

	public WizardPageResult() {
		this(null);
	}

	public WizardPageResult(T value) {
		this.value.set(value);
	}

	public ObjectProperty<T> valueProperty() {
		return value;
	}

	public T getValue() {
		return value.get();
	}

	public void setValue(T value) {
		this.value.set(value);
	}

}
