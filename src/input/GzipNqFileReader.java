package input;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class GzipNqFileReader extends NqFileReader {
	public GzipNqFileReader(String gzipFileName) throws IOException {
		super(new GZIPInputStream(new FileInputStream(gzipFileName)));
	}
}
