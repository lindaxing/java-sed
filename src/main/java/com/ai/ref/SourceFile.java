package com.ai.ref;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class SourceFile {
	private final String sourcePath;
	private final CompilationUnit compilationUnit;
	private ASTRewrite rewriter;
	private String source = null;
	private Document document = null;
	private long stamp = -1; 
	
	public SourceFile(String sourcePath,CompilationUnit cu) {
		this.sourcePath = sourcePath;
		this.compilationUnit = cu;
		this.rewriter = ASTRewrite.create(cu.getAST());
	}
	
	public void startEdit() throws IOException {
		this.source = FileUtils.readFileToString(new File(sourcePath),StandardCharsets.UTF_8);
		this.document = new Document(source);
		this.stamp = document.getModificationStamp();
		this.rewriter = ASTRewrite.create(this.compilationUnit.getAST());
	}

	public boolean hasEdited() {
		return document != null && stamp != -1 && stamp == document.getModificationStamp();
	}
	
	public void commitEdit() throws MalformedTreeException, BadLocationException, IOException {
		if (hasEdited()) {
			TextEdit te = rewriter.rewriteAST(document,null);
			te.apply(document);
			FileUtils.write(new File(sourcePath), document.get(),StandardCharsets.UTF_8);
		}
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}
	
}