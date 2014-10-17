package org.purl.wf4ever.robundle.utils;

import java.net.URI;

public class PathHelper {
	private static final URI ROOT = URI.create("/");

	public static URI relativizeFromBase(String uri, URI base) {
		return relativizeFromBase(URI.create(uri), base);
	}

	public static URI relativizeFromBase(URI uri, URI base) {
		return ROOT.resolve(base.relativize(uri));
	}
}
