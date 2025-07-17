package com.wassup741.excel.comparator.common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.wassup741.excel.comparator.common.border.BorderInfo;
import com.wassup741.excel.comparator.common.border.Coordinate;
import com.wassup741.excel.comparator.common.border.SheetBorders;

import impl.org.controlsfx.spreadsheet.GridViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

public class SheetGridUtil {

	private static final String EMPTY = "";
	private static final String ERROR = "##error##";
	private static final String RGB_PATTERN = "rgb(%d,%d,%d)";
	private static final String BOTTOM = "bottom";
	private static final String TOP = "top";
	private static final String RIGHT = "right";
	private static final String CENTER = "center";
	private static final String LEFT = "left";
	private static final String SEGMENTS_9_3_3_3_3_3 = "segments(9, 3, 3, 3, 3, 3)";
	private static final String SEGMENTS_9_3_3_3 = "segments(9, 3, 3, 3) ";
	private static final String SEGMENTS_1_1 = "segments(1, 1)";
	private static final String SEGMENTS_2_2 = "segments(2, 2)";
	private static final String SEGMENTS_9_3 = "segments(9, 3)";
	private static final String SEGMENTS_3_1 = "segments(3, 1)";
	private static final String _3PX = "3px";
	private static final String _2PX = "2px";
	private static final String _1PX = "1px";
	private static final String FX_PADDING_0_8_0_0 = "-fx-padding: 0 8 0 0;";
	private static final String WHITE = "white";
	private static final String SOLID = "solid";
	private static final String _0PX = "0px";

	public static final class HeightCallback implements
			Callback<Integer, Double> {

		private List<Double> heights;

		public HeightCallback(List<Double> heights) {
			super();
			this.heights = heights;
		}

		@Override
		public Double call(Integer param) {
			return param >= 0 && param < heights.size() ? heights.get(param)
					: GridViewSkin.DEFAULT_CELL_HEIGHT;
		}
	}

	public static DecimalFormat dubleFormat = new DecimalFormat("0");
	static {
		dubleFormat.setMaximumFractionDigits(340);
	}

	private static final Map<Sheet, SheetBorders> sheetBordersMap = new HashMap<>();
	private static StringBuilder builder = new StringBuilder(250);
	public static SimpleDateFormat dateFormat;
	static {
		dateFormat = (SimpleDateFormat) SimpleDateFormat
				.getDateInstance(SimpleDateFormat.MEDIUM);
	}

	public static GridBaseExt createColumnGrid(Column column, GridBaseExt grid) {
		int columnIndex = column.getIndex();
		GridBaseExt columnGrid = new GridBaseExt(grid.getRowCount(), 1,
				grid.getSheet(), columnIndex);
		ObservableList<ObservableList<SpreadsheetCell>> oneColumnRows = FXCollections
				.observableArrayList();
		for (int i = 0; i < grid.getRows().size(); i++) {
			ObservableList<SpreadsheetCell> row = grid.getRows().get(i);
			ObservableList<SpreadsheetCell> oneColumnCells = FXCollections
					.observableArrayList();
			SpreadsheetCell cell = row.get(columnIndex);
			SpreadsheetCellExt oneColumnCell = new SpreadsheetCellExt(i, 0, 1,
					1, cell.getCellType(), cell.getItem());
			if (cell instanceof SpreadsheetCellExt) {
				oneColumnCell.setCellStyle(((SpreadsheetCellExt) cell)
						.getCellStyle());
			}
			oneColumnCells.add(oneColumnCell);
			oneColumnRows.add(oneColumnCells);
		}

		columnGrid.setRows(oneColumnRows);
		// TODO ПОДУМАТЬ НАД ЗАГОЛОВКАМИ
		columnGrid.getColumnHeaders().add(column.getName());
		columnGrid.setRowHeightCallback(grid.getRowHeightCallback());

		return columnGrid;
	}

	public static GridBaseExt sheetToGrid(Sheet sheet) {
		ObservableList<ObservableList<SpreadsheetCell>> gridRows = FXCollections
				.observableArrayList();

		Iterable<Row> rows = () -> sheet.rowIterator();
		int maxColCount = 0;
		List<Double> heights = new ArrayList<>();
		for (Row row : rows) {
			ObservableList<SpreadsheetCell> gridCells = FXCollections
					.observableArrayList();
			Iterable<Cell> cells = () -> row.cellIterator();
			int rowIndexMustBe = 0;
			heights.add(new Double(row.getHeightInPoints() / 0.75f));
			for (Cell cell : cells) {
				int rowIndex = cell.getRowIndex();
				int colIndex = cell.getColumnIndex();
				if (rowIndexMustBe < colIndex) {
					IntStream.range(rowIndexMustBe, colIndex).forEach(
						(index -> {
							gridCells.add(createBlankCell(row.getRowNum(),
								index, sheet));
						}));
				}
				rowIndexMustBe = colIndex;
				SpreadsheetCell gridCell = createCell(cell, rowIndex, colIndex);
				gridCells.add(gridCell);
				rowIndexMustBe++;
			}
			maxColCount = Math.max(maxColCount, rowIndexMustBe);
			gridRows.add(gridCells);

		}

		for (ObservableList<SpreadsheetCell> gridCells : gridRows) {
			if (gridCells.size() < maxColCount) {
				int rowIndex = gridRows.indexOf(gridCells);
				IntStream.range(gridCells.size(), maxColCount).forEach(
					(index) -> {
						gridCells.add(createBlankCell(rowIndex, index, sheet));
					});
			}
		}

		/* удаляем пустые */
		boolean isEmpty;
		for (int i = gridRows.size() - 1; i >= 0; i--) {
			isEmpty = true;
			ObservableList<SpreadsheetCell> cells = gridRows.get(i);
			for (int j = 0; j < cells.size(); j++) {
				SpreadsheetCell cell = cells.get(j);
				if (cell.getItem() != null) {
					isEmpty = false;
					break;
				}
			}
			if (!isEmpty) {
				break;
			} else {
				gridRows.remove(i);
			}
		}

		GridBaseExt grid = new GridBaseExt(gridRows.size(),
				gridRows.isEmpty() ? 0 : gridRows.get(0).size(), sheet);
		grid.setRows(gridRows);

		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
			int firstRow = mergedRegion.getFirstRow();
			int firstColumn = mergedRegion.getFirstColumn();
			int colSpan = mergedRegion.getLastColumn() - firstColumn;
			int rowSpan = mergedRegion.getLastRow() - firstRow;
			grid.spanColumn(colSpan, firstRow, firstColumn);
			grid.spanRow(rowSpan, firstRow, firstColumn);
		}

		grid.setRowHeightCallback(new HeightCallback(heights));

		return grid;
	}

	public static SpreadsheetCell createBlankCell(int rowIndex, int colIndex,
			Sheet sheet) {
		SpreadsheetCellExt cellExt = new SpreadsheetCellExt(rowIndex, colIndex,
				1, 1, SpreadsheetCellType.STRING, null);
		return cellExt;
	}

	public static SpreadsheetCell createCell(Cell cell, int rowIndex,
			int colIndex) {
		SpreadsheetCellExt gridCell = null;
		int cellType = cell.getCellType();

		switch (cellType) {
			case Cell.CELL_TYPE_STRING:
				gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
						SpreadsheetCellType.STRING, cell
								.getRichStringCellValue().getString());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
							SpreadsheetCellType.DATE,
							toDate(cell.getDateCellValue()));
					gridCell.setFormat(dateFormat.toPattern());
				} else {
					gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
							SpreadsheetCellType.DOUBLE,
							cell.getNumericCellValue());
					gridCell.setFormat(dubleFormat.toPattern());
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
						SpreadsheetCellType.DOUBLE,
						cell.getBooleanCellValue() ? 0d : 1d);
				break;
			case Cell.CELL_TYPE_FORMULA:
				gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
						SpreadsheetCellType.STRING);
				gridCell.setItem(cell.getCellFormula());
				break;
			case Cell.CELL_TYPE_BLANK:
				gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
						SpreadsheetCellType.STRING, null);
				break;
			case Cell.CELL_TYPE_ERROR:
				gridCell = new SpreadsheetCellExt(rowIndex, colIndex, 1, 1,
						SpreadsheetCellType.STRING, ERROR);
				break;
		}
		gridCell.setCellStyle(cell.getCellStyle());
		return gridCell;
	}

	public static void applyCellStyle(
			TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> tableCell,
			SpreadsheetCellExt value, Sheet sheet) {

		CellStyle cellStyle = value.getCellStyle();
		tableCell.setStyle(EMPTY);
		applyAlingment(tableCell, cellStyle);
		applyFont(tableCell, cellStyle, sheet.getWorkbook());
		applyBorder(tableCell, cellStyle, sheet);
	}

	public static void applyBorder(
			TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> tableCell,
			CellStyle cellStyle, Sheet sheet) {

		int lastColumn = tableCell.getTableView().getColumns().size() - 1;
		int col = tableCell.getTableView().getColumns()
				.indexOf(tableCell.getTableColumn());
		int row = tableCell.getTableRow().getIndex();
		short borderTop = CellStyle.BORDER_NONE;
		short borderRight = CellStyle.BORDER_NONE;
		short borderBottom = CellStyle.BORDER_NONE;
		short borderLeft = CellStyle.BORDER_NONE;
		if (cellStyle != null) {
			borderTop = cellStyle.getBorderTop();
			borderRight = cellStyle.getBorderRight();
			borderBottom = cellStyle.getBorderBottom();
			borderLeft = cellStyle.getBorderLeft();
		}

		Coordinate coordinate = new Coordinate(row, col);
		SheetBorders sheetBorders = sheetBordersMap.get(sheet);
		if (sheetBorders == null) {
			sheetBorders = new SheetBorders();
			sheetBordersMap.put(sheet, sheetBorders);
		}
		BorderInfo borderInfo = sheetBorders.borders.get(coordinate);
		if (borderInfo == null) {
			borderInfo = new BorderInfo(borderTop, borderRight, borderBottom,
					borderLeft);
			sheetBorders.borders.put(coordinate, borderInfo);
		}
		borderInfo.top = borderTop;
		borderInfo.right = borderRight;
		borderInfo.bottom = borderBottom;
		borderInfo.left = borderLeft;

		// определяем цвет
		Workbook workbook = sheet.getWorkbook();
		String topBorderColor = null;
		String rightBorderColor = null;
		String bottomBorderColor = null;
		String leftBorderColor = null;
		if (cellStyle instanceof HSSFCellStyle) {
			HSSFCellStyle hssfCellStyle = (HSSFCellStyle) cellStyle;
			topBorderColor = getColor(hssfCellStyle.getTopBorderColor(),
				(HSSFWorkbook) workbook);
			rightBorderColor = getColor(hssfCellStyle.getRightBorderColor(),
				(HSSFWorkbook) workbook);
			bottomBorderColor = getColor(hssfCellStyle.getBottomBorderColor(),
				(HSSFWorkbook) workbook);
			leftBorderColor = getColor(hssfCellStyle.getLeftBorderColor(),
				(HSSFWorkbook) workbook);
		} else if (cellStyle instanceof XSSFCellStyle) {
			XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
			topBorderColor = getColor(xssfCellStyle.getTopBorderXSSFColor());
			rightBorderColor = getColor(xssfCellStyle.getRightBorderXSSFColor());
			bottomBorderColor = getColor(xssfCellStyle
					.getBottomBorderXSSFColor());
			leftBorderColor = getColor(xssfCellStyle.getLeftBorderXSSFColor());
		}
		borderInfo.topColor = topBorderColor;
		borderInfo.rightColor = rightBorderColor;

		Coordinate leftCoordinate = new Coordinate(row, col - 1);
		BorderInfo leftBorderInfo = sheetBorders.borders.get(leftCoordinate);
		String leftBorderWidth = null;
		String leftBorderStyle = null;
		if (leftBorderInfo == null) {
			leftBorderWidth = _0PX;
			leftBorderStyle = SOLID;
			leftBorderColor = WHITE;
		} else {
			short priorityBorder;
			if (borderLeft == CellStyle.BORDER_NONE) {
				priorityBorder = leftBorderInfo.right;
				leftBorderColor = leftBorderInfo.rightColor;
			} else {
				priorityBorder = borderLeft;
			}
			leftBorderWidth = borderToWidth(priorityBorder);
			leftBorderStyle = borderToStyle(priorityBorder);
		}
		Coordinate downCoordinate = new Coordinate(row + 1, col);
		BorderInfo downBorderInfo = sheetBorders.borders.get(downCoordinate);
		String bottomBorderWidth = null;
		String bottomBorderStyle = null;
		if (downBorderInfo == null) {
			bottomBorderWidth = _0PX;
			bottomBorderStyle = SOLID;
		} else {
			short priorityBorder;
			if (borderBottom == CellStyle.BORDER_NONE) {
				priorityBorder = downBorderInfo.top;
				bottomBorderColor = downBorderInfo.topColor;
			} else {
				priorityBorder = borderBottom;
			}
			bottomBorderWidth = borderToWidth(priorityBorder);
			bottomBorderStyle = borderToStyle(priorityBorder);
		}

		String topBorderStyle = null;
		String topBorderWidth = null;
		topBorderWidth = _0PX;
		topBorderStyle = SOLID;
		String rightBorderStyle = null;
		String rightBorderWidth = null;
		builder.append(tableCell.getStyle());
		if (col == lastColumn) {
			rightBorderWidth = borderToWidth(borderRight);
			rightBorderStyle = borderToStyle(borderRight);
			builder.append(FX_PADDING_0_8_0_0);
		} else {
			rightBorderWidth = _0PX;
			rightBorderStyle = SOLID;
			rightBorderColor = WHITE;
		}
		builder.append(CellStyles.getBorderStyle(topBorderStyle,
			rightBorderStyle, bottomBorderStyle, leftBorderStyle));
		builder.append(CellStyles.getBorderWidth(topBorderWidth,
			rightBorderWidth, bottomBorderWidth, leftBorderWidth));

		topBorderColor = ensureColor(topBorderColor);
		rightBorderColor = ensureColor(rightBorderColor);
		bottomBorderColor = ensureColor(bottomBorderColor);
		leftBorderColor = ensureColor(leftBorderColor);

		builder.append(CellStyles.getBorderColor(topBorderColor,
			rightBorderColor, bottomBorderColor, leftBorderColor));

		tableCell.setStyle(builder.toString());
		builder.setLength(0);

	}

	public static String borderToWidth(short border) {
		String result = null;
		switch (border) {
			case CellStyle.BORDER_THIN:
			case CellStyle.BORDER_DASH_DOT:
			case CellStyle.BORDER_DOTTED:
			case CellStyle.BORDER_DASH_DOT_DOT:
			case CellStyle.BORDER_DASHED:
			case CellStyle.BORDER_HAIR:
				result = _1PX;
				break;
			case CellStyle.BORDER_MEDIUM:
			case CellStyle.BORDER_DOUBLE:
			case CellStyle.BORDER_MEDIUM_DASHED:
			case CellStyle.BORDER_MEDIUM_DASH_DOT:
			case CellStyle.BORDER_MEDIUM_DASH_DOT_DOT:
			case CellStyle.BORDER_SLANTED_DASH_DOT:
				result = _2PX;
				break;
			case CellStyle.BORDER_THICK:
				result = _3PX;
				break;
			case CellStyle.BORDER_NONE:
				result = _1PX;
				break;
		}
		return result;
	}

	public static String borderToStyle(short border) {
		String result = null;
		switch (border) {
			case CellStyle.BORDER_NONE:
			case CellStyle.BORDER_THIN:
			case CellStyle.BORDER_MEDIUM:
			case CellStyle.BORDER_THICK:
				result = SOLID;
				break;
			case CellStyle.BORDER_DASHED:
				result = SEGMENTS_3_1;
				break;
			case CellStyle.BORDER_MEDIUM_DASHED:
				result = SEGMENTS_9_3;
				break;
			case CellStyle.BORDER_DOTTED:
				result = SEGMENTS_2_2;
				break;
			case CellStyle.BORDER_DOUBLE:
				result = SOLID;
				break;
			case CellStyle.BORDER_HAIR:
				result = SEGMENTS_1_1;
				break;
			case CellStyle.BORDER_DASH_DOT:
			case CellStyle.BORDER_MEDIUM_DASH_DOT:
			case CellStyle.BORDER_SLANTED_DASH_DOT:
				result = SEGMENTS_9_3_3_3;
				break;
			case CellStyle.BORDER_DASH_DOT_DOT:
			case CellStyle.BORDER_MEDIUM_DASH_DOT_DOT:
				result = SEGMENTS_9_3_3_3_3_3;
				break;
		}
		return result;
	}

	public static void applyFont(
			TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> tableCell,
			CellStyle cellStyle, Workbook workbook) {
		if (cellStyle != null) {
			short fontIndex = cellStyle.getFontIndex();
			Font font = workbook.getFontAt(fontIndex);
			applyFontStyle(tableCell, workbook, font);
			applyFontColor(tableCell, workbook, font);
		}
	}

	public static void applyFontStyle(
			TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> tableCell,
			Workbook workbook, Font font) {
		builder.append(tableCell.getStyle());
		builder.append(CellStyles.getFontStyle(font.getFontName(),
			font.getFontHeightInPoints(), font.getBold(), font.getBoldweight(),
			font.getItalic()));
		builder.append(CellStyles.getFontUnderlineStyle(font.getUnderline() != Font.U_NONE));
		if (font.getStrikeout()) {
			tableCell.getStyleClass().add(CellStyles.STRIKETHROUGH_CLASS);
		}
		tableCell.setStyle(builder.toString());
		builder.setLength(0);
	}

	public static void applyFontColor(
			TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> tableCell,
			Workbook workbook, Font font) {
		String color = null;
		if (font instanceof HSSFFont) {
			color = getColor(font.getColor(), (HSSFWorkbook) workbook);
		} else if (font instanceof XSSFFont) {
			color = getColor(((XSSFFont) font).getXSSFColor());
		}
		if (color != null) {
			tableCell.setStyle(builder.append(tableCell.getStyle())
					.append(CellStyles.getFontColorStyle(color)).toString());
			builder.setLength(0);
		}
	}

	public static void applyAlingment(
			TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> tableCell,
			CellStyle cellStyle) {

		if (cellStyle != null) {
			short hAligment = cellStyle.getAlignment();
			short vAlignment = cellStyle.getVerticalAlignment();

			String hAlign = null;
			switch (hAligment) {
				case CellStyle.ALIGN_LEFT:
				case CellStyle.ALIGN_GENERAL:
					// по ширине
				case CellStyle.ALIGN_JUSTIFY:
					// с заполнением
				case CellStyle.ALIGN_FILL:
					hAlign = LEFT;
					break;
				case CellStyle.ALIGN_CENTER:
				case CellStyle.ALIGN_CENTER_SELECTION:
					hAlign = CENTER;
					break;
				case CellStyle.ALIGN_RIGHT:
					hAlign = RIGHT;
					break;
				default:
					hAlign = LEFT;
			}

			String vAlign = null;
			switch (vAlignment) {
				case CellStyle.VERTICAL_JUSTIFY:
				case CellStyle.VERTICAL_TOP:
					vAlign = TOP;
					break;
				case CellStyle.VERTICAL_CENTER:
					vAlign = CENTER;
					break;
				case CellStyle.VERTICAL_BOTTOM:
					vAlign = BOTTOM;
					break;
				default:
					vAlign = TOP;
			}
			String style = CellStyles.getAlignmentStyle(vAlign, hAlign);
			tableCell.getStyleClass().add(style);
		}
	}

	public static LocalDate toDate(Date date) {
		Instant instant = date.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		return zdt.toLocalDate();
	}

	public static String ensureColor(String color) {
		return color == null ? CellStyles.DEFAULT_BORDER_COLOR : color;
	}

	public static String getColor(XSSFColor color) {
		String result = null;
		if (color != null) {
			byte[] rgb = color.getRgb();
			// Bytes are signed, so values of 128+ are negative!
			int red = (rgb[0] < 0) ? (rgb[0] + 256) : rgb[0];
			int green = (rgb[1] < 0) ? (rgb[1] + 256) : rgb[1];
			int blue = (rgb[2] < 0) ? (rgb[2] + 256) : rgb[2];
			result = CellStyles.getColor(red, green, blue);
		}
		return result;
	}

	public static String getColor(short color, HSSFWorkbook workbook) {
		String result = null;
		HSSFPalette palette = workbook.getCustomPalette();
		if (palette != null) {
			HSSFColor hssfColor = palette.getColor(color);
			if (hssfColor != null) {
				short[] rgb = hssfColor.getTriplet();
				int red = rgb[0];
				int green = rgb[1];
				int blue = rgb[2];
				result = String.format(RGB_PATTERN, red, green, blue);
			}
		}
		return result;
	}
}
