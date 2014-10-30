package input;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NqToGraphConverterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
/*
	@Test
	public void generateFilesDifferentEachOtherTest1() throws FileNotFoundException, IOException{
		NqToGraphConverter.generateFilesDifferentEachOther(new String[]{"rdfNode-0","rdfNode-1","rdfNode-2","rdfNode-3","rdfNode-4","rdfNode-5","rdfNode-6"});
		NqToGraphConverter.generateFilesDifferentEachOther(new String[]{"predicate-0","predicate-1","predicate-2","predicate-3","predicate-4","predicate-5","predicate-6"});
	}
*/
	@Test
	public void generateFilesDifferentEachOtherTest() throws FileNotFoundException, IOException {
		String fileNamePrefix="/data/dataStorage/temp/data-";
		String fileNameSuffix=".nq.parser.predicate";
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=0; i<=1; i++){
			fileNames.add(fileNamePrefix+i+fileNameSuffix);
		}
		NqToGraphConverter.generateFilesDifferentEachOther(fileNames.toArray(new String[fileNames.size()] ));
	}
}
