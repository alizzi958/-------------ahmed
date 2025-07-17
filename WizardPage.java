package com.wassup741.excel.comparator.wizard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;

public abstract class WizardPage<T> {

	private WizardPageResult<T> result;
	private List<WizardPage<?>> dependencies;
	private BooleanProperty isReady = new SimpleBooleanProperty(false);
	private Boolean isDirty = new Boolean(true);
	private String caption;

	public WizardPage(WizardPage<?>... dependencies) {
		this.dependencies = Arrays.stream(dependencies).collect(
			Collectors.toList());
	}

	protected void initialState() {
		dependencies.forEach(page -> page.setDirty(false));
	}

	public abstract Node getContent();

	public void setResult(T resultValue) {
		if (result == null) {
			result = new WizardPageResult<>();
		}
		result.setValue(resultValue);
		setDirty(true);
	}

	public T getResult() {
		return result == null ? null : result.getValue();
	}

	public WizardPageResult<T> getPageResult() {
		return result;
	}

	public void clearResult() {
		result = null;
	}

	public List<WizardPage<?>> getDependencies() {
		return dependencies;
	}

	public BooleanProperty readyProperty() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady.set(isReady);
	}

	public boolean isReady() {
		return isReady.get();
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}
