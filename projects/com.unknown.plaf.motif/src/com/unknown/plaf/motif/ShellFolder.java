package com.unknown.plaf.motif;

import java.io.File;
import java.io.IOException;

public class ShellFolder {
	/**
	 * Canonicalizes files that don't have symbolic links in their path. Normalizes files that do, preserving
	 * symbolic links from being resolved.
	 */
	public static File getNormalizedFile(File f) throws IOException {
		File canonical = f.getCanonicalFile();
		if(f.equals(canonical)) {
			// path of f doesn't contain symbolic links
			return canonical;
		}

		// preserve symbolic links from being resolved
		return new File(f.toURI().normalize());
	}
}
