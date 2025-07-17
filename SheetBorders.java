package com.wassup741.excel.comparator.common.border;

import java.util.HashMap;

public class SheetBorders {
	public HashMap<Coordinate, BorderInfo> borders = new HashMap<>();

	@Override
	public String toString() {
		return borders.toString();
	}
}
