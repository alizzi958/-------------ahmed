package com.wassup741.excel.comparator.common;

public class CellStyles {

	private static final String FX_FONT_S_S_DPT_S = "-fx-font: %s %s %dpt \"%s\";";
	private static final String DASH = "-";
	private static final String ALIGN = "align-";
	private static final String FX_TEXT_FILL_S = "-fx-text-fill: %s;";
	private static final String FX_FONT_S_DPT_S = "-fx-font: %s %dpt \"%s\";";
	private static final String BOLD = "bold";
	private static final String NORMAL = "normal";
	private static final String ITALIC = "italic";
	private static final String FX_UNDERLINE_B = "-fx-underline: %b;";
	private static final String FX_STRIKETHROUGH_B = "-fx-strikethrough: %b;";
	private static final String FX_BORDER_STYLE_S_S_S_S = "-fx-border-style: %s %s %s %s;";
	private static final String FX_BORDER_WIDTH_S_S_S_S = "-fx-border-width: %s %s %s %s;";
	private static final String FX_BORDER_COLOR_S_S_S_S = "-fx-border-color: %s %s %s %s;";
	private static final String RGB_D_D_D = "rgb(%d,%d,%d)";
	/* ALIGNMENT */
	public static final String ALIGNMENT_TOP_LEFT = "align-top-left";
	public static final String ALIGNMENT_TOP_CENTER = "align-top-center";
	public static final String ALIGNMENT_TOP_RIGHT = "align-top-right";
	public static final String ALIGNMENT_CENTER_LEFT = "align-center-left";
	public static final String ALIGNMENT_CENTER_CENTER = "align-center-center";
	public static final String ALIGNMENT_CENTER_RIGHT = "align-center-right";
	public static final String ALIGNMENT_BOTTOM_LEFT = "align-bottom-left";
	public static final String ALIGNMENT_BOTTOM_CENTER = "align-bottom-center";
	public static final String ALIGNMENT_BOTTOM_RIGHT = "align-bottom-right";
	public static final String STRIKETHROUGH_CLASS = "strike";
	public static final String DEFAULT_BORDER_COLOR = "rgb(212,212,212)";
	public static final String SELECTED_BORDER_COLOR = "rgb(33,115,70)";
	public static StringBuilder builder = new StringBuilder(40);

	public static String getAlignmentStyle(String vAlign, String hAlign) {
		builder.setLength(0);
		builder.append(ALIGN).append(vAlign).append(DASH).append(hAlign);
		return builder.toString();
	}

	public static String getFontColorStyle(String color) {
		return String.format(FX_TEXT_FILL_S, color);
	}

	public static String getFontStyle(String name, int sizePt, boolean isBold,
			int boldWeight, boolean isItalic) {
		if (isItalic && !isBold) {
			return String.format(FX_FONT_S_DPT_S, isItalic ? ITALIC : NORMAL,
				sizePt, name);
		} else {
			return String.format(FX_FONT_S_S_DPT_S,
				isItalic ? ITALIC : NORMAL, isBold ? BOLD : NORMAL, sizePt,
				name);
		}
	}

	public static String getFontUnderlineStyle(boolean isUnderline) {
		return String.format(FX_UNDERLINE_B, isUnderline);
	}

	public static String getFontStrikethroughStyle(boolean isStrikethrough) {
		return String.format(FX_STRIKETHROUGH_B, isStrikethrough);
	}

	public static String getBorderStyle(String top, String right,
			String bottom, String left) {
		return String.format(FX_BORDER_STYLE_S_S_S_S, top, right, bottom, left);
	}

	public static String getBorderWidth(String top, String right,
			String bottom, String left) {
		return String.format(FX_BORDER_WIDTH_S_S_S_S, top, right, bottom, left);
	}

	public static String getBorderColor(String top, String right,
			String bottom, String left) {
		return String.format(FX_BORDER_COLOR_S_S_S_S, top, right, bottom, left);
	}

	public static String getColor(int red, int green, int blue) {
		return String.format(RGB_D_D_D, red, green, blue);
	}

}
