package org.purl.wf4ever.robundle.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveDeleteVisitor extends SimpleFileVisitor<Path> {
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

	public static void deleteRecursively(Path p) throws IOException {
		if (Files.isDirectory(p)) {
			Files.walkFileTree(p, new RecursiveDeleteVisitor());
		} else {
			Files.delete(p);
		}
	}
}