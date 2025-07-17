package com.wassup741.excel.comparator2;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
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
import com.wassup741.excel.comparator.common.SpreadsheetCellExt;
import com.wassup741.excel.comparator.compare.CompareResult;
import com.wassup741.excel.comparator.compare.TwoGridsView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainModel {

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

	private static final String EMPTY_CLASS = "compare-empty";
	private static final String CONFLICT_CLASS = "compare-conflict";
	private static final String EQUAL_CLASS = "compare-equal";

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

		Map<List<Object>, Integer> index2 = createIndexRowNum(grid2,
			secondUniqueColumns);

		TwoGridsView firstMain = fillGrids(grid1, grid2, index2,
			firstUniqueColumns, firstComparableColumns, secondUniqueColumns,
			secondComparableColumns);

		CompareResult result = new CompareResult();
		result.firstMain = firstMain;

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

					addRow(result.left.allRows, mainRow, CONFLICT_CLASS);
					addRow(result.left.confilctRows, mainRow, CONFLICT_CLASS);

					addRow(result.right.allRows, dependentRow, CONFLICT_CLASS);
					addRow(result.right.confilctRows, dependentRow,
						CONFLICT_CLASS);
				} else {
					addRow(result.left.allRows, mainRow, EQUAL_CLASS);
					addRow(result.right.allRows, dependentRow, EQUAL_CLASS);
				}
			} else {
				addRow(result.left.allRows, mainRow, EMPTY_CLASS);
				addRow(
					result.right.allRows,
					createEmptyRow(result.right.allRows.size(),
						dependentGrid.getColumnCount()), EMPTY_CLASS);
				addRow(result.left.blankRows, mainRow, EMPTY_CLASS);
				addRow(
					result.right.blankRows,
					createEmptyRow(result.right.blankRows.size(),
						dependentGrid.getColumnCount()), EMPTY_CLASS);
			}
		}

		for (int i = 0; i < dependentGrid.getRowCount(); i++) {
			if (!foundRows.contains(i)) {
				addRow(
					result.left.blankRows,
					createEmptyRow(result.left.blankRows.size(),
						mainGrid.getColumnCount()), EMPTY_CLASS);
				addRow(
					result.left.allRows,
					createEmptyRow(result.left.allRows.size(),
						mainGrid.getColumnCount()), EMPTY_CLASS);

				ObservableList<SpreadsheetCell> dependentRow = dependentGrid
						.getRows().get(i);
				addRow(result.right.blankRows, dependentRow, EMPTY_CLASS);
				addRow(result.right.allRows, dependentRow, EMPTY_CLASS);
			}
		}

		result.createGrids();
		return result;
	}

	private void addRow(
			ObservableList<ObservableList<SpreadsheetCell>> destRows,
			ObservableList<SpreadsheetCell> srcRow, String addClassName) {
		destRows.add(copyRow(srcRow, destRows.size(), addClassName));
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
		SpreadsheetCellExt dest = null;
		SpreadsheetCellType<?> cellType = src.getCellType();
		if (cellType instanceof DateType) {
			dest = new SpreadsheetCellExt(rowIndex, columnIndex, 1, 1,
					SpreadsheetCellType.DATE, src.getItem() == null ? null
							: (LocalDate) src.getItem());
			dest.setFormat(src.getFormat());
		} else if (cellType instanceof DoubleType) {
			dest = new SpreadsheetCellExt(rowIndex, columnIndex, 1, 1,
					SpreadsheetCellType.DOUBLE, src.getItem() == null ? null
							: (Double) src.getItem());
			dest.setFormat(src.getFormat());
		} else if (cellType instanceof IntegerType) {
			dest = new SpreadsheetCellExt(rowIndex, columnIndex, 1, 1,
					SpreadsheetCellType.INTEGER, src.getItem() == null ? null
							: (Integer) src.getItem());
		} else if (cellType instanceof ObjectType) {
			dest = new SpreadsheetCellExt(rowIndex, columnIndex, 1, 1,
					SpreadsheetCellType.OBJECT, src.getItem());
		} else {
			dest = new SpreadsheetCellExt(rowIndex, columnIndex, 1, 1,
					SpreadsheetCellType.STRING, src.getItem() == null ? null
							: (String) src.getItem());
		}

		if (dest != null && addClassName != null) {
			dest.getStyleClass().add(addClassName);
		}
		if (src instanceof SpreadsheetCellExt) {
			dest.setCellStyle(((SpreadsheetCellExt) src).getCellStyle());
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
		return new SpreadsheetCellExt(rowIndex, columnIndex, 1, 1,
				SpreadsheetCellType.STRING, null);
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

	public void saveToFile(Grid leftGrid, ObservableList<Column> leftColumns,
			Grid rightGrid, ObservableList<Column> rightColumns, Path excelPath) {
		boolean isXls = excelPath.getFileName().toString().endsWith(".xls");
		try (Workbook workbook = isXls ? new HSSFWorkbook()
				: new XSSFWorkbook()) {

			Sheet sheet = workbook.createSheet();
			fillSheet(leftGrid, leftColumns, rightGrid, rightColumns, sheet,
				workbook);

			workbook.getSheetAt(0).setColumnWidth(
				leftGrid.getColumnCount() + 1, 8 * 256);
			FileOutputStream fileOut = new FileOutputStream(excelPath.toFile());
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void fillSheet(Grid left, ObservableList<Column> leftColumns,
			Grid right, ObservableList<Column> rightColumns, Sheet sheet,
			Workbook workbook) {
		int rowCount = left.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			Row row = sheet.createRow(i);
			ObservableList<SpreadsheetCell> leftRow = left.getRows().get(i);
			ObservableList<SpreadsheetCell> rightRow = right.getRows().get(i);
			fillRow(leftRow, leftColumns, rightRow, rightColumns, row, workbook);
		}
	}

	private void fillRow(ObservableList<SpreadsheetCell> leftRow,
			ObservableList<Column> leftColumns,
			ObservableList<SpreadsheetCell> rightRow,
			ObservableList<Column> rightColumns, Row row, Workbook workbook) {
		int colIndex = 0;
		for (Column column : leftColumns) {
			SpreadsheetCell leftCell = leftRow.get(column.getIndex());
			Cell cell = row.createCell(colIndex++);
			fillCell(leftCell, cell, workbook);
		}

		Cell delimeter = row.createCell(colIndex++);
		fillSeparatorCell(delimeter, workbook);

		for (Column column : rightColumns) {
			SpreadsheetCell rightCell = rightRow.get(column.getIndex());
			Cell cell = row.createCell(colIndex++);
			fillCell(rightCell, cell, workbook);
		}
	}

	private void fillSeparatorCell(Cell dest, Workbook workbook) {
		CellStyle destCellStyle = workbook.createCellStyle();
		destCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		destCellStyle.setFillPattern(CellStyle.THICK_BACKWARD_DIAG);
		dest.setCellStyle(destCellStyle);
	}

	// private Map<Integer, CellStyle> styleMap = new HashMap<>();

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
		// CellStyle destCellStyle = null;
		// if (src instanceof SpreadsheetCellExt) {
		// CellStyle srcCellStyle = ((SpreadsheetCellExt) src).getCellStyle();
		// if (srcCellStyle != null) {
		// int cellStyleHash = srcCellStyle.hashCode();
		// destCellStyle = styleMap.get(cellStyleHash);
		// if (destCellStyle == null) {
		// destCellStyle = workbook.createCellStyle();
		// destCellStyle.cloneStyleFrom(srcCellStyle);
		// styleMap.put(cellStyleHash, destCellStyle);
		// }
		// }
		// }
		// dest.setCellStyle(destCellStyle);

		SpreadsheetCellType<?> cellType = src.getCellType();
		if (cellType instanceof DateType) {
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
