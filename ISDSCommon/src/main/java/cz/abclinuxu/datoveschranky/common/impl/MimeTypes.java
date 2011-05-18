package cz.abclinuxu.datoveschranky.common.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author xrosecky
 */
public class MimeTypes {

    private static final Map<String, String> mimeTypes = new HashMap<String, String>();

    static {
	InputStream is = MimeTypes.class.getResourceAsStream("/mimetypes.txt");
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	String line = null;
	try {
	    while ((line = reader.readLine()) != null) {
		String[] splitted = line.split(" ");
		if (splitted.length == 2) {
		    mimeTypes.put(splitted[0], splitted[1]);
		} else {
		    throw new RuntimeException("Error in mimetypes.txt");
		}
	    }
	} catch (IOException ioe) {
	    throw new RuntimeException("Error when processing mimetypes.txt");
	}
    }

    public static String fileNameToMimeType(String filename) {
	int dotPosition = filename.lastIndexOf(".");
	if (dotPosition != -1) {
	    String suffix = filename.substring(dotPosition+1, filename.length());
	    return mimeTypes.get(suffix);
	} else {
	    return null;
	}
    }
}
