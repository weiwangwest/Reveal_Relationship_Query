package input;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
			PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName+".temp", false)));
			while (reader.hasNext()){
				writer.println(reader.next());
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
			PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName+".temp", false)));
			while (reader.hasNext()){
				writer.println(reader.next());
			}
			writer.close();
			reader.close();
			NqFileReader.assertFilesEqual(fileName, fileName+".temp");
		}
	}
}
