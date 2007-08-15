package webdav.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.commons.codec.digest.DigestUtils;

public final class FileHelper {

	public static void writeInputToOutputStream(final InputStream in, final String path) throws IOException {
		FileHelper.writeInputToOutputStream(in, new FileOutputStream(path));
	}

	public static void writeInputToOutputStream(final InputStream in, final OutputStream out) throws IOException {
		final byte buffer[] = new byte[2048];
		try {
			while (true) {
				final int len = in.read(buffer);
				if (len == -1) break;
				out.write(buffer, 0, len);
			}
		} finally {
			try {
				in.close();
			} finally {
				out.close();
			}
		}
	}

	public static void copyFile(final String from, final String to) throws IOException {
    FileHelper.writeInputToOutputStream(new FileInputStream(from), new FileOutputStream(to));
  }

	public static String readFileToString(final String fileName) throws IOException {
		final char[] buffer = new char[8192];
		final StringBuffer rVal = new StringBuffer();
		final Reader reader = new FileReader(fileName);
		while (reader.read(buffer) != -1) {
			rVal.append(buffer);
		}
		return rVal.substring(0).trim();
	}

	public static void createDirectory(final String path) {
		final File dir = new File(path);
		if (!dir.exists()) dir.mkdirs();
	}

	public static void deleteFile(final String path) {
		new File(path).delete();
	}

	public static String getMD5HexFromFile(final String fileName) throws IOException {
		return FileHelper.getMd5HexFromFile(fileName, 512);
	}

	private static String getMd5HexFromFile(final String fileName, final int bytes) throws IOException {
		final byte[] b = new byte[bytes];
		final InputStream input = new FileInputStream(fileName);
		input.read(b);
		return DigestUtils.md5Hex(b);
	}
}