package com.wassup741.excel.comparator.common.border;


public class Coordinate {

	int row;
	int col;

	public Coordinate(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinate) {
			Coordinate another = (Coordinate) obj;
			return row == another.row && col == another.col;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (31 + row) * 31 + col;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("row:").append(row).append(" col:")
				.append(col).toString();
	}
}
