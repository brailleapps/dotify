package se.mtm.epub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ContentExtractor {
	private ZipInputStream zis;
	private File output;

	public ContentExtractor() throws FileNotFoundException {
	}

	public void unpack(InputStream input, File output) throws EPUB3ReaderException {
		this.output = output;
		if (!output.isDirectory()) {
			throw new EPUB3ReaderException("Path must be a directory.");
		}
		zis = new ZipInputStream(input);

		try {
			ZipEntry entry;
			try {
				entry = zis.getNextEntry();
				// this must be the first item
				// see 3.3 OCF ZIP Container Media Type Identification
				if (entry.getName().equals("mimetype")) {
					int s = (int) entry.getSize();
					byte[] b = new byte[s];
					zis.read(b);
					if (!"application/epub+zip".equals(new String(b))) {
						throw new EPUB3ReaderException("Wrong mimetype: " + new String(b));
					}
				} else {
					throw new EPUB3ReaderException("The file does not contain a mimetype file.");
				}
				while ((entry = zis.getNextEntry()) != null) {
					processEntry(entry);
				}
			} catch (IOException e) {
				throw new EPUB3ReaderException(e);
			}
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected void processEntry(ZipEntry entry) throws EPUB3ReaderException {
		File f = new File(output, entry.getName());
		if (entry.isDirectory()) {
			f.mkdirs();
		} else {
			f.getParentFile().mkdirs();
		}
		try {
			writeToStream(entry.getSize(), new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			throw new EPUB3ReaderException(e);
		} finally {
			try {
				zis.closeEntry();
			} catch (IOException e) {
			}
		}
	}

	protected void writeToStream(long s, OutputStream os) throws EPUB3ReaderException {
		int buf = 2048;
		byte[] b = new byte[buf];
		int read = 0;
		try {
			while ((read = zis.read(b)) > -1) {
				os.write(b, 0, read);
			}
		} catch (IOException e) {
			throw new EPUB3ReaderException(e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}
	}

}
