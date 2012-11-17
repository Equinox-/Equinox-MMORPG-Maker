package com.pi.graphics.device;

import java.util.List;

public class WordWrappingCache {
	private List<String> lines;
	private int height;

	WordWrappingCache(List<String> sLines, int sHeight) {
		this.lines = sLines;
		this.height = sHeight;
	}

	public Iterable<String> getLines() {
		return lines;
	}

	public int getHeight() {
		return height;
	}
}
