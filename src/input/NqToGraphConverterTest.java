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

import fundamental.StringArrayOfFileNames;

public class NqToGraphConverterTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	@Test
	public void generateFilesDifferentEachOtherTest() throws Exception {
		int index=0;
		NqToGraphConverter.generateFilesDifferentEachOther(StringArrayOfFileNames.getFileNames("vertex", 0, 2));		
		index += NqToGraphConverter.generateMultipleMapFiles(StringArrayOfFileNames.getFileNames("vertex", 0, 2), index);
		NqToGraphConverter.generateFilesDifferentEachOther(StringArrayOfFileNames.getFileNames("predicate", 0, 2));
		index +=NqToGraphConverter.generateMultipleMapFiles(StringArrayOfFileNames.getFileNames("predicate", 0, 2), index);
		NqToGraphConverter.generateFilesDifferentEachOther(StringArrayOfFileNames.getFileNames("subgraph", 0, 2));
		NqToGraphConverter.generateMultipleMapFiles(StringArrayOfFileNames.getFileNames("subgraph", 0, 2), index);
		//use the map to replace the nq lines file
		NqToGraphConverter.generateMapsReplacedFiles(StringArrayOfFileNames.getFileNames("vertex.map.final", 0, 2), 
				StringArrayOfFileNames.getFileNames("line", 0, 2));
		NqToGraphConverter.generateMapsReplacedFiles(StringArrayOfFileNames.getFileNames("predicate.map.final", 0, 2), 
				StringArrayOfFileNames.getFileNames("line", 0, 2));
		NqToGraphConverter.generateMapsReplacedFiles(StringArrayOfFileNames.getFileNames("subgraph.map.final", 0, 2), 
				StringArrayOfFileNames.getFileNames("line", 0, 2));
		assertTrue(NqToGraphConverter.areNqFilesTotallyMapped(StringArrayOfFileNames.getFileNames("line.final", 0, 2)));
	}
}
