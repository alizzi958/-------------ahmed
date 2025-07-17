package com.wassup741.excel.comparator.link;

import java.util.List;

import com.wassup741.excel.comparator.common.Link;

public class MakeLinksResult {
	private List<Link> uniqueLinks, comparableLinks;

	public List<Link> getComparableLinks() {
		return comparableLinks;
	}

	public void setComparableLinks(List<Link> comparableLinks) {
		this.comparableLinks = comparableLinks;
	}

	public List<Link> getUniqueLinks() {
		return uniqueLinks;
	}

	public void setUniqueLinks(List<Link> uniqueLinks) {
		this.uniqueLinks = uniqueLinks;
	}
}
