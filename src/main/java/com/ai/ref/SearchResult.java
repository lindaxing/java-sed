package com.ai.ref;

import org.eclipse.jdt.core.dom.ASTNode;

public class SearchResult {
	private final SourceFile sourceFile;
	private final ASTNode astNode;
	public SearchResult(SourceFile sourceFile,ASTNode astNode)  {
		this.astNode = astNode;
		this.sourceFile = sourceFile;
	}
	
	public ASTNode getAstNode() {
		return astNode;
	}

	public SourceFile getSourceFile() {
		return sourceFile;
	}

}
