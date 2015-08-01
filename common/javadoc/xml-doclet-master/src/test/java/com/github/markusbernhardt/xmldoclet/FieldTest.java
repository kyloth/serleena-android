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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation12;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Field;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;

public class FieldTest extends AbstractTestParent {

	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void testSampledoc() {
		executeJavadoc(".", new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}

	/**
	 * testing a returns of fields
	 */
	@Test
	public void testMethod1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Field1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		List<Field> fields = classNode.getField();

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		// field0 -- test name
		Field field = findByFieldName("field0", fields);
		assertEquals(field.getName(), "field0");

		// field1 -- test public field
		field = findByFieldName("field1", fields);
		assertEquals(field.getScope(), "public");

		// field2 -- test private field
		field = findByFieldName("field2", fields);
		assertEquals(field.getScope(), "private");

		// field3 -- default scope field (non defined)
		field = findByFieldName("field3", fields);
		assertEquals(field.getScope(), "");

		// field4 -- protected scope field
		field = findByFieldName("field4", fields);
		assertEquals(field.getScope(), "protected");

		// field5 -- volatile field
		field = findByFieldName("field5", fields);
		assertTrue(field.isVolatile());

		// negative test of volatile
		assertFalse(findByFieldName("field4", fields).isVolatile());

		// field6 -- static field
		field = findByFieldName("field6", fields);
		assertTrue(field.isStatic());

		// negative test of static
		assertFalse(findByFieldName("field4", fields).isStatic());

		// field7 -- transient field
		field = findByFieldName("field7", fields);
		assertTrue(field.isTransient());

		// negative test of transient
		assertFalse(findByFieldName("field4", fields).isTransient());

		// field8 -- final field
		field = findByFieldName("field8", fields);
		assertTrue(field.isFinal());

		// negative test of final
		assertFalse(findByFieldName("field4", fields).isFinal());

		// field9 -- string final expression
		field = findByFieldName("field9", fields);
		assertEquals(field.getConstant(), "\"testy\"");

		// field10 -- int final expression
		field = findByFieldName("field10", fields);
		assertEquals(field.getConstant(), "10");

		// field11 -- annotation
		field = findByFieldName("field11", fields);
		assertEquals(field.getAnnotation().size(), 1);

		AnnotationInstance annotation = field.getAnnotation().get(0);
		assertEquals(annotation.getQualified(), "java.lang.Deprecated");
		assertEquals(annotation.getName(), "Deprecated");
		assertEquals(annotation.getArgument().size(), 0);

		// field12 -- two annotations
		field = findByFieldName("field12", fields);
		assertEquals(field.getAnnotation().size(), 2);

		annotation = field.getAnnotation().get(0);
		assertEquals(annotation.getQualified(), "java.lang.Deprecated");
		assertEquals(annotation.getName(), "Deprecated");
		assertEquals(annotation.getArgument().size(), 0);

		annotation = field.getAnnotation().get(1);
		assertEquals(annotation.getQualified(), Annotation12.class.getName());
		assertEquals(annotation.getName(), Annotation12.class.getSimpleName());
		assertEquals(annotation.getArgument().size(), 1);

		AnnotationArgument argument = annotation.getArgument().get(0);
		assertEquals(argument.getName(), "value");
		assertEquals(argument.getValue().get(0), "mister");

		// field13 - type testing
		field = findByFieldName("field13", fields);
		assertNotNull(field.getType());
		assertEquals(field.getType().getQualified(), "java.lang.String");
		assertNull(field.getType().getDimension());
		assertNull(field.getType().getWildcard());
		assertEquals(field.getType().getGeneric().size(), 0);

		// field14 - wild card
		field = findByFieldName("field14", fields);
		assertNotNull(field.getType());
		assertEquals(field.getType().getQualified(), "java.util.ArrayList");
		assertNotNull(field.getType().getGeneric());
		assertEquals(field.getType().getGeneric().size(), 1);
		assertEquals(field.getType().getGeneric().get(0).getQualified(), "?");
		assertNotNull(field.getType().getGeneric().get(0).getWildcard());

		// field15 - typed generic
		field = findByFieldName("field15", fields);
		assertNotNull(field.getType());
		assertEquals(field.getType().getQualified(), "java.util.HashMap");
		assertEquals(field.getType().getGeneric().size(), 2);
		assertEquals(field.getType().getGeneric().get(0).getQualified(), "java.lang.String");
		assertNull(field.getType().getGeneric().get(0).getWildcard());
		assertEquals(field.getType().getGeneric().get(1).getQualified(), "java.lang.Integer");
		assertNull(field.getType().getGeneric().get(1).getWildcard());

		// field16 - array
		field = findByFieldName("field16", fields);
		assertNotNull(field.getType());
		assertEquals(field.getType().getQualified(), "java.lang.String");
		assertEquals(field.getType().getDimension(), "[]");
	}

	/**
	 * Short way of finding fields.
	 * 
	 * @param fieldName
	 *            the shortname of the method
	 * @param fields
	 *            the list of methods to look through.
	 * @return The matching field
	 */
	private Field findByFieldName(String fieldName, List<Field> fields) {
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}

		fail();
		return null;
	}
}
