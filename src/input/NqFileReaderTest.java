package input;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import output.NqFileWriter;

public class NqFileReaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void nqFileReaderTest() throws IOException, InterruptedException {
		for (int i=0; i<3; i++){
			String fileName="/data/data-"+i+".nq";
			NqFileReader reader= new NqFileReader(fileName);
			NqFileWriter writer=new NqFileWriter(fileName+".temp");
			while (reader.hasNext()){
				writer.writeLine(reader.next());
			}
			writer.close();
			reader.close();
			NqFileReader.assertFilesEqual(fileName, fileName+".temp");
		}
	}
	@Test
	public void gzipNqFileReaderTest() throws IOException, InterruptedException {
		for (int i=0; i<3; i++){
			String fileName="/data/data-"+i+".nq.gz";
			GzipNqFileReader reader= new GzipNqFileReader(fileName);
			NqFileWriter writer=new NqFileWriter(fileName+".temp");
			while (reader.hasNext()){
				writer.writeLine(reader.next());
			}
			writer.close();
			reader.close();
			NqFileReader.assertFilesEqual(fileName, fileName+".temp");
		}
	}
}
