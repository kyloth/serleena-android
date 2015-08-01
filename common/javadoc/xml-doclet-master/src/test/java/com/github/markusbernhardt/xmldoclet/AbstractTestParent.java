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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.xmldoclet.xjc.Root;

public class AbstractTestParent {

	private final static Logger log = LoggerFactory.getLogger(AbstractTestParent.class);

	/**
	 * Processes the source code using javadoc.
	 * 
	 * @param extendedClassPath
	 *            Any classpath information required to help along javadoc.
	 *            Javadoc will actualy compile the source code you specify; so
	 *            if there are any jars or classes that are referenced by the
	 *            source code to process, then including those compiled items in
	 *            the classpath will give you more complete data in the
	 *            resulting XML.
	 * @param sourcePaths
	 *            Usually sourcePaths is specified in conjuction with
	 *            either/both packages & subpackages. The sourcepaths value
	 *            should be the path of the source files right before the
	 *            standard package-based folder layout of projects begins. For
	 *            example, if you have code that exists in package foo.bar, and
	 *            your code is physically in /MyFolder/foo/bar/ , then the
	 *            sourcePaths would be /MyFolder
	 * @param packages
	 *            Use if you want to detail specific packages to process
	 *            (contrast with subpackages, which is probably the easiest/most
	 *            brute force way of using xml-doclet). If you have within your
	 *            code two packages, foo.bar and bar.foo, but only wanted
	 *            foo.bar processed, then specify just 'foo.bar' for this
	 *            argument.
	 * @param sourceFiles
	 *            You can specify source files individually. This usually is
	 *            used instead of sourcePaths/subPackages/packages. If you use
	 *            this parameter, specify the full path of any java file you
	 *            want processed.
	 * @param subPackages
	 *            You can specify 'subPackages', which simply gives one an easy
	 *            way to specify the root package, and have javadoc recursively
	 *            look through everything under that package. So for instance,
	 *            if you had foo.bar, foo.bar.bar, and bar.foo, specifying 'foo'
	 *            will process foo.bar and foo.bar.bar packages, but not bar.foo
	 *            (unless you specify 'bar' as a subpackage, too)
	 * @return XStream compatible data structure
	 */
	public Root executeJavadoc(String extendedClassPath, String[] sourcePaths, String[] packages, String[] sourceFiles,
			String[] subPackages, String[] additionalArguments) {
		try {
			OutputStream errors = new LoggingOutputStream(log, LoggingLevelEnum.ERROR);
			OutputStream warnings = new LoggingOutputStream(log, LoggingLevelEnum.WARN);
			OutputStream notices = new LoggingOutputStream(log, LoggingLevelEnum.INFO);

			PrintWriter errorWriter = new PrintWriter(errors, false);
			PrintWriter warningWriter = new PrintWriter(warnings, false);
			PrintWriter noticeWriter = new PrintWriter(notices, false);

			// aggregate arguments and packages
			ArrayList<String> argumentList = new ArrayList<String>();

			// by setting this to 'private', nothing is omitted in the parsing
			argumentList.add("-private");

			String classPath = System.getProperty("java.class.path", ".");
			if (extendedClassPath != null) {
				classPath += File.pathSeparator + extendedClassPath;
			}
			argumentList.add("-classpath");
			argumentList.add(classPath);

			if (sourcePaths != null) {
				String concatedSourcePaths = join(File.pathSeparator, sourcePaths);
				if (concatedSourcePaths.length() > 0) {
					argumentList.add("-sourcepath");
					argumentList.add(concatedSourcePaths);
				}
			}

			if (subPackages != null) {
				String concatedSubPackages = join(";", subPackages);
				if (concatedSubPackages.length() > 0) {
					argumentList.add("-subpackages");
					argumentList.add(concatedSubPackages);
				}
			}

			if (packages != null) {
				argumentList.addAll(Arrays.asList(packages));
			}

			if (sourceFiles != null) {
				argumentList.addAll(Arrays.asList(sourceFiles));
			}

			if (additionalArguments != null) {
				argumentList.addAll(Arrays.asList(additionalArguments));
			}

			log.info("Executing doclet with arguments: " + join(" ", argumentList));

			String[] arguments = argumentList.toArray(new String[] {});
			com.sun.tools.javadoc.Main.execute("xml-doclet", errorWriter, warningWriter, noticeWriter,
					XmlDoclet.class.getName(), arguments);

			errors.close();
			warnings.close();
			notices.close();

			log.info("done with doclet processing");
		} catch (Exception e) {
			log.error("doclet exception", e);
		} catch (Error e) {
			log.error("doclet error", e);
		}

		return XmlDoclet.root;
	}

	public static String join(String glue, String[] strings) {
		return join(glue, Arrays.asList(strings));
	}

	public static String join(String glue, Iterable<String> strings) {
		if (strings == null) {
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();
		String verkett = "";
		for (String string : strings) {
			stringBuilder.append(verkett);
			stringBuilder.append(string);
			verkett = glue;
		}
		return stringBuilder.toString();
	}

}
