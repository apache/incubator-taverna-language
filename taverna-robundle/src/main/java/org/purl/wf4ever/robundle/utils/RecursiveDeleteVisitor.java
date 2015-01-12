package org.purl.wf4ever.robundle.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveDeleteVisitor extends SimpleFileVisitor<Path> {
	public static void deleteRecursively(Path p) throws IOException {
		if (Files.isDirectory(p)) {
			Files.walkFileTree(p, new RecursiveDeleteVisitor());
		} else {
			Files.delete(p);
		}
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		super.postVisitDirectory(dir, exc);
		Files.delete(dir);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		Files.delete(file);
		return FileVisitResult.CONTINUE;
	}
}
