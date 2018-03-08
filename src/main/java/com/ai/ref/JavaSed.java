package com.ai.ref;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ai.ref.dom.JDocument;

/**
 * in java project, do command:
 * jsed 's/class:com.ai.ref.JavaSed/com.ai.ref.JavaSed2/'
 * jsed 's/method/field/
 */
public class JavaSed {

	private static List<SourceFile> files = new ArrayList<SourceFile>();
	
	public static void main(String[] args) throws XPathExpressionException {
		ASTParser astParser = ASTParser.newParser(AST.JLS8);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setResolveBindings(true);
	    Map<String, String> compilerOptions = JavaCore.getOptions();
	    compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6); //设置Java语言版本
	    compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
	    compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
	    compilerOptions.put(JavaCore.CORE_ENCODING, StandardCharsets.UTF_8.name());
	    astParser.setCompilerOptions(compilerOptions); //设置编译选项
	    astParser.setEnvironment(getClassPaths(), getSourcePath(), null, true);
	    String[] files = getAllJavaFiles();
	    astParser.createASTs(files, null, files, new FileASTRequestor() {
			@Override
			public void acceptAST(String sourceFilePath, CompilationUnit cu) {
				JavaSed.files.add(new SourceFile(sourceFilePath, cu));
			}
		},null);
	    XPathFactory factory = XPathFactory.newInstance();
	    XPath x = factory.newXPath();
	    XPathExpression expr = x.compile("/");
	    Node node = (Node)expr.evaluate(new JDocument(JavaSed.files.get(0)), XPathConstants.NODE);
	    System.out.println(node);
	}
	
	private static String[] getClassPaths() {
		try {
			return FileUtils.readLines(new File("classpath.lst"), StandardCharsets.UTF_8)
					.toArray(new String[0]);
		} catch (IOException e) {
			return new String[0];
		}
	}
	
	private static String[] getSourcePath() {
		try {
			return FileUtils.readLines(new File("sourcepath.lst"), StandardCharsets.UTF_8).toArray(new String[0]);
		} catch (IOException e) {
			return new String[0];
		}
	}
	
	private static String[] getAllJavaFiles(){
		String[] sourcePaths = getSourcePath();
		List<String> s = new ArrayList<String>();
		for (int i = 0; i < sourcePaths.length; i++) {
			Collection<File> files = FileUtils.listFiles(new File(sourcePaths[i]), new String[]{"java"}, true);
			for (File file : files) {
				s.add(file.getAbsolutePath());
			}
		}
		return s.toArray(new String[0]); 
	}
	

}
