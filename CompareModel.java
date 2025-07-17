package com.wassup741.excel.comparator.compare;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType.DateType;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType.DoubleType;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType.IntegerType;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType.ObjectType;

import com.wassup741.excel.comparator.common.Column;
import com.wassup741.excel.comparator.common.Link;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CompareModel {

	private static final String EMPTY_CLASS = "compare-empty";
	private static final String CONFLICT_CLASS = "compare-conflict";
	private static final String EQUAL_CLASS = "compare-equal";
	private CellStyle destCellStyle;

	public CompareResult compare(Grid grid1, Grid grid2,
			List<Link> uniqueLinks, List<Link> comparableLinks) {

		List<Column> firstUniqueColumns = getColumns(uniqueLinks,
			link -> link.getFirstColumn());
		List<Column> firstComparableColumns = getColumns(comparableLinks,
			link -> link.getFirstColumn());

		List<Column> secondUniqueColumns = getColumns(uniqueLinks,
			link -> link.getSecondColumn());
		List<Column> secondComparableColumns = getColumns(comparableLinks,
			link -> link.getSecondColumn());

//		Map<List<Object>, Integer> index1 = createIndexRowNum(grid1,
//			firstUniqueColumns);
		Map<List<Object>, Integer> index2 = createIndexRowNum(grid2,
			secondUniqueColumns);

		TwoGridsView firstMain = fillGrids(grid1, grid2, index2,
			firstUniqueColumns, firstComparableColumns, secondUniqueColumns,
			secondComparableColumns);

//		TwoGridsView secondMain = fillGrids(grid2, grid1, index1,
//			secondUniqueColumns, secondComparableColumns, firstUniqueColumns,
//			firstComparableColumns);

		CompareResult result = new CompareResult();
		result.firstMain = firstMain;
//		result.secondMain = secondMain;

		return result;
	}

	public TwoGridsView fillGrids(Grid mainGrid, Grid dependentGrid,
			Map<List<Object>, Integer> dependentIndex,
			List<Column> mainUniqueColumns, List<Column> mainComparableColumns,
			List<Column> dependentUniqueColumns,
			List<Column> dependentComparableColumns) {

		TwoGridsView result = new TwoGridsView();
		List<Column> mainPartialColumns = new ArrayList<>(mainUniqueColumns);
		mainPartialColumns.addAll(mainComparableColumns);
		List<Column> dependentPartialColumns = new ArrayList<>(
				dependentUniqueColumns);
		List<Integer> foundRows = new ArrayList<>();
		dependentPartialColumns.addAll(dependentComparableColumns);

		for (ObservableList<SpreadsheetCell> mainRow : mainGrid.getRows()) {
			List<Object> mainUniqueValues = getUniqueValues(mainRow,
				mainUniqueColumns);

			Integer dependentRowIndex = dependentIndex.get(mainUniqueValues);
			if (dependentRowIndex != null) {
				foundRows.add(dependentRowIndex);
				ObservableList<SpreadsheetCell> dependentRow = dependentGrid
						.getRows().get(dependentRowIndex);
				if (!compareColumns(mainRow, dependentRow,
					mainComparableColumns, dependentComparableColumns)) {

					addRow(result.left.allRows, mainRow,
						CONFLICT_CLASS);
					addRow(result.mainPartialColumns.allRows, mainRow,
						mainPartialColumns, CONFLICT_CLASS);
					addRow(result.left.confilctRows, mainRow,
						CONFLICT_CLASS);
					addRow(result.mainPartialColumns.confilctRows, mainRow,
						mainPartialColumns, CONFLICT_CLASS);

					addRow(result.right.allRows, dependentRow,
						CONFLICT_CLASS);
					addRow(result.dependentPartialColumns.allRows,
						dependentRow, dependentPartialColumns, CONFLICT_CLASS);

					addRow(result.right.confilctRows,
						dependentRow, CONFLICT_CLASS);
					addRow(result.dependentPartialColumns.confilctRows,
						dependentRow, dependentPartialColumns, CONFLICT_CLASS);
				} else {
					addRow(result.left.allRows, mainRow, EQUAL_CLASS);
					addRow(result.mainPartialColumns.allRows, mainRow,
						mainPartialColumns, EQUAL_CLASS);
					addRow(result.right.allRows, dependentRow,
						EQUAL_CLASS);
					addRow(result.dependentPartialColumns.allRows,
						dependentRow, dependentPartialColumns, EQUAL_CLASS);
				}
			} else {
				addRow(result.left.allRows, mainRow, EMPTY_CLASS);
				addRow(result.mainPartialColumns.allRows, mainRow,
					mainPartialColumns, EMPTY_CLASS);
				addRow(
					result.right.allRows,
					createEmptyRow(result.right.allRows.size(),
						dependentGrid.getColumnCount()), EMPTY_CLASS);
				addRow(
					result.dependentPartialColumns.allRows,
					createEmptyRow(
						result.dependentPartialColumns.allRows.size(),
						mainPartialColumns.size()), EMPTY_CLASS);
				addRow(result.left.blankRows, mainRow, EMPTY_CLASS);
				addRow(result.mainPartialColumns.blankRows, mainRow,
					mainPartialColumns, EMPTY_CLASS);
				addRow(
					result.right.blankRows,
					createEmptyRow(
						result.right.blankRows.size(),
						dependentGrid.getColumnCount()), EMPTY_CLASS);
				addRow(
					result.dependentPartialColumns.blankRows,
					createEmptyRow(
						result.dependentPartialColumns.blankRows.size(),
						dependentPartialColumns.size()), EMPTY_CLASS);
			}
		}

		for (int i = 0; i < dependentGrid.getRowCount(); i++) {
			if (!foundRows.contains(i)) {
				addRow(
					result.left.blankRows,
					createEmptyRow(result.left.blankRows.size(),
						mainGrid.getColumnCount()), EMPTY_CLASS);
				addRow(
					result.mainPartialColumns.blankRows,
					createEmptyRow(result.mainPartialColumns.blankRows.size(),
						mainPartialColumns.size()), EMPTY_CLASS);
				addRow(
					result.left.allRows,
					createEmptyRow(result.left.allRows.size(),
						mainGrid.getColumnCount()), EMPTY_CLASS);
				addRow(
					result.mainPartialColumns.allRows,
					createEmptyRow(result.mainPartialColumns.allRows.size(),
						mainPartialColumns.size()), EMPTY_CLASS);

				ObservableList<SpreadsheetCell> dependentRow = dependentGrid
						.getRows().get(i);
				addRow(result.right.blankRows, dependentRow,
					EMPTY_CLASS);
				addRow(result.dependentPartialColumns.blankRows, dependentRow,
					dependentPartialColumns, EMPTY_CLASS);
				addRow(result.right.allRows, dependentRow,
					EMPTY_CLASS);
				addRow(result.dependentPartialColumns.allRows, dependentRow,
					dependentPartialColumns, EMPTY_CLASS);
			}
		}

		result.createGrids();
		return result;
	}

	private void addRow(
			ObservableList<ObservableList<SpreadsheetCell>> destRows,
			ObservableList<SpreadsheetCell> srcRow,
			List<Column> partialColumns, String addClassName) {
		destRows.add(copyRow(srcRow, destRows.size(), partialColumns,
			addClassName));
	}

	private void addRow(
			ObservableList<ObservableList<SpreadsheetCell>> destRows,
			ObservableList<SpreadsheetCell> srcRow, String addClassName) {
		destRows.add(copyRow(srcRow, destRows.size(), addClassName));
	}

	private ObservableList<SpreadsheetCell> copyRow(
			ObservableList<SpreadsheetCell> src, int rowIndex,
			List<Column> columns, String addClassName) {
		ObservableList<SpreadsheetCell> dest = FXCollections
				.observableArrayList();
		int destColumnIndex = 0;
		for (Column column : columns) {
			int srcColumnIndex = column.getIndex();
			dest.add(copyCell(src.get(srcColumnIndex), rowIndex,
				destColumnIndex++, addClassName));
		}
		return dest;
	}

	private ObservableList<SpreadsheetCell> copyRow(
			ObservableList<SpreadsheetCell> src, int rowIndex,
			String addClassName) {
		ObservableList<SpreadsheetCell> dest = FXCollections
				.observableArrayList();
		src.forEach(cell -> {
			dest.add(copyCell(cell, rowIndex, cell.getColumn(), addClassName));
		});
		return dest;
	}

	private SpreadsheetCell copyCell(SpreadsheetCell src, int rowIndex,
			int columnIndex, String addClassName) {
		SpreadsheetCell dest = null;
		SpreadsheetCellType<?> cellType = src.getCellType();
		if (cellType instanceof DateType) {
			dest = SpreadsheetCellType.DATE.createCell(rowIndex, columnIndex,
				1, 1, src.getItem() == null ? null : (LocalDate) src.getItem());
		} else if (cellType instanceof DoubleType) {
			dest = SpreadsheetCellType.DOUBLE.createCell(rowIndex, columnIndex,
				1, 1, src.getItem() == null ? null : (Double) src.getItem());
		} else if (cellType instanceof IntegerType) {
			dest = SpreadsheetCellType.INTEGER.createCell(rowIndex,
				columnIndex, 1, 1,
				src.getItem() == null ? null : (Integer) src.getItem());
		} else if (cellType instanceof ObjectType) {
			dest = ((ObjectType) SpreadsheetCellType.OBJECT).createCell(
				rowIndex, columnIndex, 1, 1, src.getItem());
		} else {
			dest = SpreadsheetCellType.STRING.createCell(rowIndex, columnIndex,
				1, 1, src.getItem() == null ? null : (String) src.getItem());
		}

		if (dest != null && addClassName != null) {
			dest.getStyleClass().add(addClassName);
		}
		return dest;
	}

	private ObservableList<SpreadsheetCell> createEmptyRow(int rowIndex,
			int columnCount) {
		ObservableList<SpreadsheetCell> row = FXCollections
				.observableArrayList();
		IntStream.range(0, columnCount).forEach(index -> {
			row.add(createEmptyCell(rowIndex, index));
		});

		return row;
	}

	private SpreadsheetCell createEmptyCell(int rowIndex, int columnIndex) {
		return SpreadsheetCellType.STRING.createCell(rowIndex, columnIndex, 1,
			1, null);
	}

	private boolean compareColumns(ObservableList<SpreadsheetCell> row1,
			ObservableList<SpreadsheetCell> row2,
			List<Column> comparableColumns1, List<Column> comparableColumns2) {

		boolean result = true;
		for (int i = 0; i < comparableColumns1.size() && result; i++) {
			int cellIndex1 = comparableColumns1.get(i).getIndex();
			int cellIndex2 = comparableColumns2.get(i).getIndex();
			Object cellValue1 = row1.get(cellIndex1).getItem();
			Object cellValue2 = row2.get(cellIndex2).getItem();
			if (cellValue1 != cellValue2) {
				if (cellValue1 == null || cellValue2 == null) {
					result = false;
				} else {
					result = cellValue1.equals(cellValue2);
				}
			}
		}
		return result;
	}

	private Map<List<Object>, Integer> createIndexRowNum(Grid grid,
			List<Column> uniqueColumns) {
		Map<List<Object>, Integer> index = new HashMap<List<Object>, Integer>();
		Integer rowNum = 0;
		for (ObservableList<SpreadsheetCell> row : grid.getRows()) {
			List<Object> key = getUniqueValues(row, uniqueColumns);
			index.put(key, rowNum++);
		}
		return index;
	}

	private List<Object> getUniqueValues(ObservableList<SpreadsheetCell> row,
			List<Column> uniqueColumns) {
		List<Object> uniqueValues = new ArrayList<Object>();
		uniqueColumns.forEach(column -> {
			int index = column.getIndex();
			uniqueValues.add(row.get(index).getText());
		});

		return uniqueValues;
	}

	private List<Column> getColumns(List<Link> links,
			Function<Link, Column> func) {
		return links.stream().map(func).collect(Collectors.toList());
	}

	public void saveToFile(Grid leftGrid, Grid rightGrid, Path excelPath) {
		boolean isXls = excelPath.getFileName().toString().endsWith(".xls");
		try (Workbook workbook = isXls ? new HSSFWorkbook()
				: new XSSFWorkbook()) {

			Sheet sheet = workbook.createSheet();
			fillSheet(leftGrid, rightGrid, sheet, workbook);

			workbook.getSheetAt(0).setColumnWidth(
				leftGrid.getColumnCount() + 1, 8 * 256);
			FileOutputStream fileOut = new FileOutputStream(excelPath.toFile());
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void fillSheet(Grid left, Grid right, Sheet sheet, Workbook workbook) {
		int rowCount = left.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			Row row = sheet.createRow(i);
			ObservableList<SpreadsheetCell> leftRow = left.getRows().get(i);
			ObservableList<SpreadsheetCell> rightRow = right.getRows().get(i);
			fillRow(leftRow, rightRow, row, workbook);
		}
	}

	private void fillRow(ObservableList<SpreadsheetCell> leftRow,
			ObservableList<SpreadsheetCell> rightRow, Row row, Workbook workbook) {
		int colIndex = 0;
		for (SpreadsheetCell leftCell : leftRow) {
			Cell cell = row.createCell(colIndex++);
			fillCell(leftCell, cell, workbook);
		}

		Cell delimeter = row.createCell(colIndex++);
		fillSeparatorCell(delimeter, workbook);

		for (SpreadsheetCell rightCell : rightRow) {
			Cell cell = row.createCell(colIndex++);
			fillCell(rightCell, cell, workbook);
		}

	}

	private void fillSeparatorCell(Cell dest, Workbook workbook) {
		destCellStyle = workbook.createCellStyle();
		destCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		destCellStyle.setFillPattern(CellStyle.THICK_BACKWARD_DIAG);
		dest.setCellStyle(destCellStyle);
	}

	/**
	 * @param src
	 * @param dest
	 * @param workbook
	 */
	private void fillCell(SpreadsheetCell src, Cell dest, Workbook workbook) {
		Object srcValue = src.getItem();
		if (srcValue == null) {
			return;
		}
		SpreadsheetCellType<?> cellType = src.getCellType();
		CellStyle destCellStyle = workbook.createCellStyle();
		dest.setCellStyle(destCellStyle);
		CreationHelper createHelper = workbook.getCreationHelper();
		if (cellType instanceof DateType) {
			destCellStyle.setDataFormat(createHelper.createDataFormat()
					.getFormat("dd.MM.yyyy"));
			LocalDate srcValueDate = (LocalDate) srcValue;
			Instant stcValueInstant = srcValueDate.atStartOfDay()
					.atZone(ZoneId.systemDefault()).toInstant();
			dest.setCellValue(Date.from(stcValueInstant));
		} else if (cellType instanceof DoubleType) {
			dest.setCellValue((Double) srcValue);
		} else if (cellType instanceof IntegerType) {
			dest.setCellValue(((Double) srcValue).intValue());
		} else if (cellType instanceof ObjectType) {
			dest.setCellValue(srcValue.toString());
		} else {
			dest.setCellValue(srcValue.toString());
		}
	}

}
