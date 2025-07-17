package com.wassup741.excel.comparator;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wassup741.excel.comparator.compare.CompareWizard;
import com.wassup741.excel.comparator.link.MakeLinksWizard;
import com.wassup741.excel.comparator.select.SelectFilesWizard;
import com.wassup741.excel.comparator.wizard.Wizard;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ExcelComparatorForm extends Application {

	static Logger logger = LoggerFactory.getLogger(ExcelComparatorForm.class);

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader wizardLoader = new FXMLLoader(getClass().getResource(
				"wizard/Wizard.fxml"));
			BorderPane wizardView = wizardLoader.load();
			Wizard wizard = wizardLoader.getController();

			// выбор файла
			FXMLLoader selectFilesLoader = new FXMLLoader(getClass()
					.getResource("select/SelectFiles.fxml"));
			selectFilesLoader.setControllerFactory(p -> new SelectFilesWizard(
					primaryStage));
			selectFilesLoader.load();
			SelectFilesWizard selectFilesController = selectFilesLoader
					.getController();
			wizard.addPage(selectFilesController);

			FXMLLoader makeLinksLoader = new FXMLLoader(getClass().getResource(
				"link/MakeLinksContainer.fxml"));
			makeLinksLoader.setControllerFactory(p -> new MakeLinksWizard(
					selectFilesController));
			makeLinksLoader.load();
			MakeLinksWizard makeLinksController = makeLinksLoader
					.getController();
			wizard.addPage(makeLinksController);

			FXMLLoader compareLoader = new FXMLLoader(getClass().getResource(
				"compare/Compare.fxml"));
			compareLoader.setControllerFactory(p -> new CompareWizard(
					primaryStage, selectFilesController, makeLinksController));
			compareLoader.load();
			CompareWizard compareController = compareLoader.getController();
			wizard.addPage(compareController);

			wizard.flushPages();
			Scene scene = new Scene(wizardView);
			scene.getStylesheets().add(
				getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(
				getClass().getResource("common/cellStyles.css")
						.toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();

		} catch (Exception e) {
			logger.error("sorry for error", e);
		}
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("uncaught ex", e);
				if (e instanceof OutOfMemoryError) {
					Platform.exit();
				}
			}
		});

		launch(args);
	}
}
