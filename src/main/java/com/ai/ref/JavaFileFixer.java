package com.ai.ref;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class JavaFileFixer {
	
	private static final String[] SERVICE_ANONTION_LONG_NAME = "org.springframework.stereotype.Service".split("\\.");
	private static final String SERVICE_ANONTION_SHORT_NAME = SERVICE_ANONTION_LONG_NAME[SERVICE_ANONTION_LONG_NAME.length-1];
	
	private static final String[] Controller_ANONTION_LONG_NAME = "org.springframework.stereotype.Controller".split("\\.");
	private static final String Controller_ANONTION_SHORT_NAME = Controller_ANONTION_LONG_NAME[Controller_ANONTION_LONG_NAME.length-1];
	
	private static final String[] RequestMapping_ANONTION_LONG_NAME = "org.springframework.web.bind.annotation.RequestMapping".split("\\.");
	private static final String RequestMapping_ANONTION_SHORT_NAME = RequestMapping_ANONTION_LONG_NAME[RequestMapping_ANONTION_LONG_NAME.length-1];
	
	private static final String[] RequestMethod_ANONTION_LONG_NAME = "org.springframework.web.bind.annotation.RequestMethod".split("\\.");
	private static final String RequestMethod_ANONTION_SHORT_NAME = RequestMethod_ANONTION_LONG_NAME[RequestMethod_ANONTION_LONG_NAME.length-1];
	
	private static final String[] Component_ANONTION_LONG_NAME = "org.springframework.stereotype.Component".split("\\.");
	private static final String Component_ANONTION_SHORT_NAME = Component_ANONTION_LONG_NAME[Component_ANONTION_LONG_NAME.length-1];
	
	private static final String[] Component_Autowired_LONG_NAME = "org.springframework.beans.factory.annotation.Autowired".split("\\.");
	private static final String Component_Autowired_SHORT_NAME = Component_Autowired_LONG_NAME[Component_Autowired_LONG_NAME.length-1];
	
	private static final String[] Component_RQM_LONG_NAME = "org.springframework.web.bind.annotation.RequestParam".split("\\.");
	private static final String Component_RQM_SHORT_NAME = Component_RQM_LONG_NAME[Component_RQM_LONG_NAME.length-1];

	public static void main(String[] args) {
		ASTParser astParser = ASTParser.newParser(AST.JLS8);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setResolveBindings(true);
	    Map<String, String> compilerOptions = JavaCore.getOptions();
	    compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6); //设置Java语言版本
	    compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
	    compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
	    compilerOptions.put(JavaCore.CORE_ENCODING, "UTF-8");
	    astParser.setCompilerOptions(compilerOptions); //设置编译选项
	    astParser.setEnvironment(getClassPaths(), getSourcePath(), null, true);
	    String[] files = getAllJavaFiles();
	    astParser.createASTs(files, null, files, new FileASTRequestor() {
			@Override
			public void acceptAST(String sourceFilePath, CompilationUnit cu) {
				try {
					final String source = FileUtils.readFileToString(new File(sourceFilePath),StandardCharsets.UTF_8);
					ASTRewrite rewriter = ASTRewrite.create(cu.getAST());   
					org.eclipse.jface.text.Document document = new org.eclipse.jface.text.Document(source);
					long stamp = document.getModificationStamp();
					cu.recordModifications();
//					addServiceAnnontion(rewriter, cu);
//					addDaoAnnontion(rewriter, cu);
//					addActionRequestMapAnnontion(rewriter, cu);
//					removeDataObject(rewriter, cu);
					storeDataObject(rewriter, cu);
//					addServiceDaoAutoWire(rewriter, cu);
//					modifyParamtoSpringRequestParm(rewriter, cu);
					TextEdit te = rewriter.rewriteAST(document,null);
					te.apply(document);
					if ( document.getModificationStamp() != stamp ){
						FileUtils.write(new File(sourceFilePath), document.get(),StandardCharsets.UTF_8);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MalformedTreeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		}, null);
	}
	

	protected static void removeDataObject(ASTRewrite rewriter, CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		TypeDeclaration typeDeclaration = null;
		if (typeDecl instanceof TypeDeclaration) {
			typeDeclaration = (TypeDeclaration) typeDecl;
		} else {
			return;
		}
		Type type = typeDeclaration.getSuperclassType();
		if (type instanceof SimpleType) {
			String name = ((SimpleType) type).getName().getFullyQualifiedName();
			if (name .equals("DataObject")) {
				rewriter.remove(type, null);
			}
		}
	}
	
	private static boolean isDataObject(ASTRewrite rewriter, CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		TypeDeclaration typeDeclaration = null;
		if (typeDecl instanceof TypeDeclaration) {
			typeDeclaration = (TypeDeclaration) typeDecl;
		} else {
			return false;
		}
		Type type = typeDeclaration.getSuperclassType();
		if (type instanceof SimpleType) {
			String name = ((SimpleType) type).getName().getFullyQualifiedName();
			if (name .equals("DataObject")) {
				return true;
			}
		}
		return false;
	}

	protected static void storeDataObject(ASTRewrite rewriter, CompilationUnit cu) {
		if (isDataObject(rewriter, cu)) {
			TypeDeclaration typeDecl = (TypeDeclaration) cu.types().get(0);
			List<?> ms = typeDecl.modifiers();
			for (Object object : ms) {
				IExtendedModifier iem = (IExtendedModifier) object;
				if (iem.isAnnotation() ) {
					Annotation annotation = (Annotation) iem;
					IAnnotationBinding iab = annotation.resolveAnnotationBinding();
					if ("javax.persistence.Table".equals(iab.getAnnotationType().getQualifiedName())) {
						IMemberValuePairBinding[] imvpbs = iab.getAllMemberValuePairs();
						String schema = null;
						String name = null;
						String catalog = null;
						String packageName = cu.getPackage().getName().getFullyQualifiedName();
						if (!packageName.endsWith(".entity")) {
							continue;
						}
						String[] pn = packageName.split("\\.");
						String modulename= pn[pn.length-2];
						String moName = typeDecl.getName().getFullyQualifiedName();
						for (int i = 0; i < imvpbs.length; i++) {
							IMemberValuePairBinding imvb = imvpbs[i];
							if (imvb.getName().equals("name")) {
								name = String.valueOf(imvb.getValue());
							}
							if (imvb.getName().equals("schema")) {
								schema = String.valueOf(imvb.getValue());
							}
							if (imvb.getName().equals("catalog")) {
								catalog = String.valueOf(imvb.getValue());
							}
						}
						
						FieldDeclaration[] sds = typeDecl.getFields();
						String idCoum = null;
						for (FieldDeclaration fieldDeclaration : sds) {
							IdFieldASTVisitor iav = new IdFieldASTVisitor();
							fieldDeclaration.accept(iav);
							if (iav.isIdMarker()) {
								idCoum = iav.getColumnName();
							}
						}
						
						String s = 
					    "<!-- <table tableName=\"%s\" schema=\""+schema+"\" domainObjectName=\"%s.entity.%s\" mapperName=\"%s.dao.%sDao\">\n";
						if (idCoum!=null) {
							s+="  <generatedKey column=\"%s\" sqlStatement=\"JDBC\" identity=\"true\" />\n";
						}
					    s += "</table> -->\n";
					    
					    String realConf ;
					    if (idCoum!=null) {
					    	realConf = String.format(s, new Object[]{name,modulename,moName,modulename,moName,idCoum});
					    } else {
					    	realConf = String.format(s, new Object[]{name,modulename,moName,modulename,moName});
					    }
					    System.out.println(realConf);
					}
				}
			}
			// ms.
		}
	}

	private static class IdFieldASTVisitor extends ASTVisitor {
		
		private boolean isIdMarker = false;
		private String columnName;
		@Override
		public boolean visit(MarkerAnnotation node) {
			IAnnotationBinding iab = node.resolveAnnotationBinding();
			if (iab.getAnnotationType().getQualifiedName().equals("javax.persistence.Id")) {
				isIdMarker = true;
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(NormalAnnotation node) {
			IAnnotationBinding iab = node.resolveAnnotationBinding();
			if (iab.getAnnotationType().getQualifiedName().equals("javax.persistence.Column")) {
				IMemberValuePairBinding[] imvbs = iab.getAllMemberValuePairs();
				for (int i = 0; i < imvbs.length; i++) {
					if (imvbs[i].getName().endsWith("name")) {
						columnName = String.valueOf(imvbs[i].getValue());
					}
				}
			}
			return super.visit(node);
		}

		public boolean isIdMarker() {
			return isIdMarker;
		}

		public String getColumnName() {
			return columnName;
		}
		
	}
	
	private static String[] getSourcePath() {
		try {
			return FileUtils.readLines(new File("sourcepath.lst"), "utf-8").toArray(new String[0]);
		} catch (IOException e) {
			return new String[0];
		}
	}

	private static String[] getClassPaths() {
		try {
			return FileUtils.readLines(new File("classpath.lst"), "utf-8")
					.toArray(new String[0]);
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
	
	@SuppressWarnings("unchecked")
	private static void addServiceAnnontion(ASTRewrite rewriter,CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		if (!typeDecl.getName().getFullyQualifiedName().endsWith("ServiceImpl")) {
			return;
		}
		final NormalAnnotation javaTaskInfoA = cu.getAST().newNormalAnnotation();
		javaTaskInfoA.setTypeName(cu.getAST().newSimpleName(SERVICE_ANONTION_SHORT_NAME));
		MemberValuePair mV = cu.getAST().newMemberValuePair();
		mV.setName(cu.getAST().newSimpleName("value"));
		StringLiteral sl = cu.getAST().newStringLiteral();
		String pName = cu.getPackage().getName().getFullyQualifiedName();
		pName = pName.substring(pName.lastIndexOf('.') +1);
		sl.setLiteralValue(pName+"_" + typeDecl.getName().getFullyQualifiedName());
		mV.setValue(sl);
		javaTaskInfoA.values().add(mV);
		rewriter.getListRewrite(typeDecl, typeDecl.getModifiersProperty()).insertAt(javaTaskInfoA, 0, null);
		addImports(rewriter, cu, SERVICE_ANONTION_LONG_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private static void addDaoAnnontion(ASTRewrite rewriter, CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		if (!typeDecl.getName().getFullyQualifiedName().endsWith("DaoImpl")) {
			return;
		}
		final NormalAnnotation javaTaskInfoA = cu.getAST().newNormalAnnotation();
		javaTaskInfoA.setTypeName(cu.getAST().newSimpleName(Component_ANONTION_SHORT_NAME));
		MemberValuePair mV = cu.getAST().newMemberValuePair();
		mV.setName(cu.getAST().newSimpleName("value"));
		StringLiteral sl = cu.getAST().newStringLiteral();
		String pName = cu.getPackage().getName().getFullyQualifiedName();
		String[] s = pName.split("\\.");
		pName = s[s.length-2];
		sl.setLiteralValue(pName+"_" + typeDecl.getName().getFullyQualifiedName());
		mV.setValue(sl);
		javaTaskInfoA.values().add(mV);
		rewriter.getListRewrite(typeDecl, typeDecl.getModifiersProperty()).insertAt(javaTaskInfoA, 0, null);
		addImports(rewriter, cu, Component_ANONTION_LONG_NAME);
	}
	
	
	private static void addImports(ASTRewrite rewriter,CompilationUnit cu,String[] importEle) {
		List<?> imports = cu.imports();
		Name qName = cu.getAST().newName(importEle);
		for (Object object : imports) {
			if (object instanceof ImportDeclaration) {
				ImportDeclaration id = (ImportDeclaration) object;
				if (id.getName().getFullyQualifiedName().equals(qName.getFullyQualifiedName()) ) {
					return;
				}
			}
		}
		ImportDeclaration id = cu.getAST().newImportDeclaration();
		id.setName(qName);
		rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY).insertLast(id, null);
	}
	
	@SuppressWarnings("unchecked")
	private static void addActionRequestMapAnnontion(ASTRewrite rewriter,CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		if (!typeDecl.getName().getFullyQualifiedName().endsWith("ActionImpl")) {
			return;
		}
		String packageName = cu.getPackage().getName().getFullyQualifiedName();
		String efModuleName = packageName.substring(packageName.lastIndexOf(".")+1);
		
		TypeDeclaration actionType = null;
		if (typeDecl instanceof TypeDeclaration) {
			actionType = (TypeDeclaration) typeDecl;
		} else {
			return;
		}
		SimpleType type = (SimpleType) actionType.superInterfaceTypes().get(0);
		String actionName = type.getName().getFullyQualifiedName();
		
		{
			addImports(rewriter, cu, Controller_ANONTION_LONG_NAME);
			final NormalAnnotation javaTaskInfoA = cu.getAST().newNormalAnnotation();
			javaTaskInfoA.setTypeName(cu.getAST().newSimpleName(Controller_ANONTION_SHORT_NAME));
			MemberValuePair mV = cu.getAST().newMemberValuePair();
			mV.setName(cu.getAST().newSimpleName("value"));
			StringLiteral sl = cu.getAST().newStringLiteral();
			sl.setLiteralValue(efModuleName +"_" +actionName);
			mV.setValue(sl);
			javaTaskInfoA.values().add(mV);
			rewriter.getListRewrite(typeDecl, typeDecl.getModifiersProperty()).insertAt(javaTaskInfoA, 0, null);
		}
		
		{
			addImports(rewriter, cu, RequestMapping_ANONTION_LONG_NAME);
			final NormalAnnotation javaTaskInfoA = cu.getAST().newNormalAnnotation();
			javaTaskInfoA.setTypeName(cu.getAST().newSimpleName(RequestMapping_ANONTION_SHORT_NAME));
			MemberValuePair mV = cu.getAST().newMemberValuePair();
			mV.setName(cu.getAST().newSimpleName("value"));
			StringLiteral sl = cu.getAST().newStringLiteral();
			sl.setLiteralValue("/"+efModuleName+"/"+actionName);
			mV.setValue(sl);
			javaTaskInfoA.values().add(mV);
			rewriter.getListRewrite(typeDecl, typeDecl.getModifiersProperty()).insertAt(javaTaskInfoA,0,null);
		}

		MethodDeclaration[] mds = actionType.getMethods();
		ITypeBinding supperInterface = type.resolveBinding();
		
		for (MethodDeclaration methodDeclaration : mds) {
			IMethodBinding imb = methodDeclaration.resolveBinding();
			if (isActionOverRiadeMethod(imb, supperInterface)) {
				final NormalAnnotation javaTaskInfoA = cu.getAST().newNormalAnnotation();
				javaTaskInfoA.setTypeName(cu.getAST().newSimpleName(RequestMapping_ANONTION_SHORT_NAME));
				javaTaskInfoA.setProperty("value", "/"+imb.getName() +".go");
				
				MemberValuePair mV = cu.getAST().newMemberValuePair();
				mV.setName(cu.getAST().newSimpleName("value"));
				StringLiteral sl = cu.getAST().newStringLiteral();
				sl.setLiteralValue("/"+imb.getName() +".go");
				mV.setValue(sl);
				javaTaskInfoA.values().add(mV);
				
				FieldAccess expression = cu.getAST().newFieldAccess();
				
				addImports(rewriter, cu, RequestMethod_ANONTION_LONG_NAME);
				expression.setExpression(cu.getAST().newSimpleName(RequestMethod_ANONTION_SHORT_NAME));
				expression.setName(cu.getAST().newSimpleName("POST"));
				javaTaskInfoA.setProperty("method", expression);
				
				mV = cu.getAST().newMemberValuePair();
				mV.setName(cu.getAST().newSimpleName("method"));
				mV.setValue(expression);
				javaTaskInfoA.values().add(mV);
				
				rewriter.getListRewrite(methodDeclaration, methodDeclaration.getModifiersProperty()).insertAt(javaTaskInfoA,0,null);
			}
		}
		
	}
	
	private static boolean isActionOverRiadeMethod(IMethodBinding imb,ITypeBinding supperInterface) {
		IMethodBinding[] imbs = supperInterface.getDeclaredMethods();
		for (IMethodBinding iMethodBinding : imbs) {
			if ( imb.overrides(iMethodBinding)) return true;
		}
		return false;
	}
	
	private static void addServiceDaoAutoWire(ASTRewrite rewriter, CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		if (!typeDecl.getName().getFullyQualifiedName().endsWith("ServiceImpl")) {
			return;
		}
		TypeDeclaration actionType = null;
		if (typeDecl instanceof TypeDeclaration) {
			actionType = (TypeDeclaration) typeDecl;
		} else {
			return;
		}
		addImports(rewriter, cu, Component_Autowired_LONG_NAME);
		
		FieldDeclaration[] fieldDeclarations = actionType.getFields();
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			int modifiers = fieldDeclaration.getModifiers();
			if ( Modifier.isStatic(modifiers) ) {
				continue;
			}
			String type = fieldDeclaration.getType().toString();
			if (type.endsWith("Service") || type.endsWith("Dao")) {
				final MarkerAnnotation autowire = cu.getAST().newMarkerAnnotation();
				autowire.setTypeName(cu.getAST().newSimpleName(Component_Autowired_SHORT_NAME));
				rewriter.getListRewrite(fieldDeclaration, fieldDeclaration.getModifiersProperty()).insertAt(autowire,0,null);
			}
		}
	}
	
	protected static void modifyParamtoSpringRequestParm(ASTRewrite rewriter, CompilationUnit cu) {
		AbstractTypeDeclaration typeDecl = (AbstractTypeDeclaration) cu.types().get(0);
		if (!( typeDecl.getName().getFullyQualifiedName().endsWith("Action")
				|| typeDecl.getName().getFullyQualifiedName().endsWith("ActionImpl")
				)) {
			return;
		}
		TypeDeclaration actionType = null;
		if (typeDecl instanceof TypeDeclaration) {
			actionType = (TypeDeclaration) typeDecl;
		} else {
			return;
		}
//		addImports(rewriter, cu, Component_Autowired_LONG_NAME);
		
		MethodDeclaration[] fieldDeclarations = actionType.getMethods();
		for (MethodDeclaration fieldDeclaration : fieldDeclarations) {
			int modifiers = fieldDeclaration.getModifiers();
			if ( Modifier.isStatic(modifiers) ) {
				continue;
			}
			List<?> params = fieldDeclaration.parameters();
			for (Object object : params) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) object;
				List<IExtendedModifier> modifys = svd.modifiers();
				
				SingleMemberAnnotation annotation = null;
				for (IExtendedModifier iExtendedModifier : modifys) {
					if (iExtendedModifier instanceof SingleMemberAnnotation) {
						SingleMemberAnnotation annotationTemp = (SingleMemberAnnotation) iExtendedModifier;
						ITypeBinding ityBing = annotationTemp.resolveTypeBinding();
						if (ityBing.getQualifiedName().equals("com.ailk.easyframe.web.common.annotation.Param")){
							annotation = annotationTemp;
						}
					}
				}
				if (annotation != null) {
						SingleMemberAnnotation annotationTemp = cu.getAST().newSingleMemberAnnotation();
						addImports(rewriter, cu, Component_RQM_LONG_NAME);
						annotationTemp.setTypeName(cu.getAST().newSimpleName(Component_RQM_SHORT_NAME));
						StringLiteral sl = cu.getAST().newStringLiteral();
						Expression express = annotation.getValue();
						if (express instanceof StringLiteral) {
							sl.setLiteralValue(((StringLiteral) express).getLiteralValue());
						}
						annotationTemp.setValue(sl);
						
						rewriter.getListRewrite(svd, SingleVariableDeclaration.MODIFIERS2_PROPERTY)
						.replace(annotation, annotationTemp, null);
				}
			}
		}
	}

}
