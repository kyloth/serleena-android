///////////////////////////////////////////////////////////////////////////////
// 
// This file is part of Serleena.
// 
// The MIT License (MIT)
//
// Copyright (C) 2015 Antonio Cavestro, Gabriele Pozzan, Matteo Lisotto, 
//   Nicola Mometto, Filippo Sestini, Tobia Tesan, Sebastiano Valle.    
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to 
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
//
///////////////////////////////////////////////////////////////////////////////


package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation1;
import com.github.markusbernhardt.xmldoclet.simpledata.Annotation2;
import com.github.markusbernhardt.xmldoclet.simpledata.Annotation3;
import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;

/**
 * Unit test group for Annotations
 */
@SuppressWarnings("deprecation")
public class AnnotationTest extends AbstractTestParent {
	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void testSampledoc() {
		executeJavadoc(null, new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}

	/**
	 * testing an annotation with nothing defined
	 */
	@Test
	public void testAnnotation1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Annotation annotationNode = packageNode.getAnnotation().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertEquals(packageNode.getComment(), null);
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 1);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 0);

		assertEquals(annotationNode.getComment(), null);
		assertEquals(annotationNode.getName(), Annotation1.class.getSimpleName());
		assertEquals(annotationNode.getQualified(), Annotation1.class.getName());
		assertEquals(annotationNode.getScope(), "public");
		assertEquals(annotationNode.getAnnotation().size(), 0);
		assertEquals(annotationNode.getElement().size(), 0);
		assertTrue(annotationNode.isIncluded());
	}

	/**
	 * testing an annotation with an annotation decorating it
	 */
	@Test
	public void testAnnotation2() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation2.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Annotation annotationNode = packageNode.getAnnotation().get(0);
		AnnotationInstance annotationInstance = annotationNode.getAnnotation().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertEquals(packageNode.getComment(), null);
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 1);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation2");
		assertEquals(annotationNode.getName(), Annotation2.class.getSimpleName());
		assertEquals(annotationNode.getQualified(), Annotation2.class.getName());
		assertEquals(annotationNode.getScope(), "public");
		assertEquals(annotationNode.getAnnotation().size(), 1);
		assertEquals(annotationNode.getElement().size(), 0);
		assertTrue(annotationNode.isIncluded());

		// test annotation 'deprecated' on class
		assertEquals(annotationInstance.getQualified(), "java.lang.Deprecated");
		assertEquals(annotationInstance.getName(), "Deprecated");
		assertEquals(annotationInstance.getArgument().size(), 0);
	}

	/**
	 * testing an annotation with one element field
	 */
	@Test
	public void testAnnotation3() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Annotation annotationNode = packageNode.getAnnotation().get(0);
		AnnotationElement element = annotationNode.getElement().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertEquals(packageNode.getComment(), null);
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 1);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation3");
		assertEquals(annotationNode.getName(), Annotation3.class.getSimpleName());
		assertEquals(annotationNode.getQualified(), Annotation3.class.getName());
		assertEquals(annotationNode.getScope(), "public");
		assertEquals(annotationNode.getAnnotation().size(), 0);
		assertEquals(annotationNode.getElement().size(), 1);
		assertTrue(annotationNode.isIncluded());

		// test annotation element
		assertEquals(element.getName(), "id");
		assertEquals(element.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Annotation3.id");
		assertEquals(element.getType().getQualified(), "int");
		assertEquals(element.getDefault(), Integer.toString(3));
	}

	/**
	 * testing an annotation with non-public definition
	 */
	@Test
	public void testAnnotation4() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation4.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Annotation annotationNode = packageNode.getAnnotation().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertEquals(packageNode.getComment(), null);
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 1);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation4");
		assertEquals(annotationNode.getName(), "Annotation4");
		assertEquals(annotationNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Annotation4");
		assertEquals(annotationNode.getScope(), "");
		assertEquals(annotationNode.getAnnotation().size(), 0);
		assertEquals(annotationNode.getElement().size(), 0);
		assertTrue(annotationNode.isIncluded());
	}
}