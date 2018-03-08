package com.ai.ref.dom;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JNodeList implements NodeList {

	private List<Node> nodes = new ArrayList<Node>(0);

	public JNodeList(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public Node item(int index) {
		return nodes.get(index);
	}

	@Override
	public int getLength() {
		return nodes.size();
	}
	
}
