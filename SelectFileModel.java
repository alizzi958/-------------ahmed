package com.wassup741.excel.comparator.select;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SelectFileModel {

	private static DecimalFormat dubleFormat = new DecimalFormat("0");
	{
		dubleFormat.setMaximumFractionDigits(340);
	}

	public Sheet readFile(File file) {
		Path path = file.toPath();
		return getSheet(path);
	}

	private Sheet getSheet(Path excelPath) {
		Sheet sheet = null;
		boolean isXls = excelPath.getFileName().toString().endsWith(".xls");
		try (Workbook workbook = isXls ? new HSSFWorkbook(
				Files.newInputStream(excelPath)) : new XSSFWorkbook(
				Files.newInputStream(excelPath))) {

			sheet = workbook.getSheetAt(0);

		} catch (Exception e) {

		}

		return sheet;
	}
}
