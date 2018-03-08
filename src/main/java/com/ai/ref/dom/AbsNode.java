package com.ai.ref.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

public abstract class AbsNode implements Node {

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "insertBefore");
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "replaceChild");
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "removeChild");
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "appendChild");
	}

	@Override
	public boolean hasChildNodes() {
		return getChildNodes().getLength() != 0;
	}

	@Override
	public Node getFirstChild() {
		if (hasChildNodes()) {
			return getChildNodes().item(0);
		}
		return null;
	}

	@Override
	public Node getLastChild() {
		if (hasChildNodes()) {
			return getChildNodes().item(0);
		}
		return null;
	}

	
	@Override
	public Node cloneNode(boolean deep) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "cloneNode");
	}

	@Override
	public void normalize() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "normalize");
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return false;
	}

	@Override
	public String getNamespaceURI() {
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "setPrefix");
	}

	@Override
	public boolean hasAttributes() {
		return getAttributes().getLength() != 0;
	}

	@Override
	public String getBaseURI() {
		return null;
	}

	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "setPrefix");
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "setTextContent");
	}


	@Override
	public String lookupPrefix(String namespaceURI) {
		return null;
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return false;
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return null;
	}

	@Override
	public Object getFeature(String feature, String version) {
		return null;
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "setUserData");
	}

	@Override
	public Object getUserData(String key) {
		return null;
	}

}
