package input;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import output.GzipNqFileWriter;

public class NqFileReaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	@Test	//TODO how to compare two gzip files?
	public void gzipNqFileReaderTest() throws IOException, InterruptedException {
		for (int i=0; i<3; i++){
			String fileName="/data/data-"+i+".nq.gz";
			GzipNqFileReader reader= new GzipNqFileReader(fileName);
			GzipNqFileWriter writer=new GzipNqFileWriter(fileName+".temp");
			while (reader.hasNext()){
				writer.writeLine(reader.next());
			}
			writer.close();
			reader.close();
			//GzipNqFileReader.assertFilesEqual(fileName, fileName+".temp");
		}
	}
}
