package com.example.ader.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.example.ader.utilities.*;

public class BookValidator {
	private static final String TAG = "BookValidator";
	private List<String> bookList = new ArrayList<String>();

	/*
	 * Notes to Gary:
	 * 1. What are your thoughts on returning the set of books here?
	 * 2. Currently a NullPointerException is thrown if the path isn't found;
	 *    Either return a NoFilesFound (or similar) exception or return an
	 *    empty list of books.
	 */
	public void findBooks(String path) {
		
		// Some guard code follows.
		if (path == null) {
			Logging.logInfo(TAG, "null string passed as the path to findBooks. Exiting method.");
			return;
		}
			
		if (path.length() == 0 || path.startsWith(".")) {
			Logging.logInfo(TAG, String.format(
				"path [%s]is either zero length or starts with . Exiting method", path));
			return;
		}
		
		FilenameFilter dirFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		};

		if (containsBook(path)) {
			bookList.add(path);
		}
		else {
			File temp = new File(path);
			// listFiles can throw a NPE when it tries to navigate folders e.g. 
			// /sdcard/.android_secure 
			// This was found on a HTC Hero with Android 2.2
			File[] files = temp.listFiles(dirFilter);
			if (files != null) {
				for (File folder : files) {
					findBooks(folder.toString());
				}
			}
		}
		return;
	}

	public Boolean validFileSystemRoot(String path) {
		File fileSystem = new File(path);
		return fileSystem.isDirectory();
	}

	public Boolean containsBook(String path) {
		return ((new File(path, "ncc.html").exists()) || (new File(path, "NCC.HTML").exists()));
	}

	public List<String> getBookList() {
		for (String path : bookList) {
			Logging.logInfo("BookValidator", "Book available at : " + path);
		}
		return bookList;
	}
}
