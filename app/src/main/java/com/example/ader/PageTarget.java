package com.example.ader;

import org.w3c.dom.Node;

public class PageTarget extends NavBase {
	private String type;

	public PageTarget(Node anchor, String type) {
		super(anchor);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
