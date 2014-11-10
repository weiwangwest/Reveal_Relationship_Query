package input;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

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
		int lastFileNo=6;
		//generate difference files and map files
		NqToGraphConverter.generateFilesDifferentEachOther(StringArrayOfFileNames.getFileNames("vertex", 0, lastFileNo));		
		index = NqToGraphConverter.generateMultipleMapFiles(StringArrayOfFileNames.getFileNames("vertex.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);

		NqToGraphConverter.generateFilesDifferentEachOther(StringArrayOfFileNames.getFileNames("predicate", 0, lastFileNo));
		index =NqToGraphConverter.generateMultipleMapFiles(StringArrayOfFileNames.getFileNames("predicate.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);

		NqToGraphConverter.generateFilesDifferentEachOther(StringArrayOfFileNames.getFileNames("subgraph", 0, lastFileNo));
		index =NqToGraphConverter.generateMultipleMapFiles(StringArrayOfFileNames.getFileNames("subgraph.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);

		//use the map to replace the lines file
		NqToGraphConverter.generateMapsReplacedFiles(StringArrayOfFileNames.getFileNames("vertex.final.map.final", 0, lastFileNo), 
				StringArrayOfFileNames.getFileNames("line", 0, lastFileNo), NqToGraphConverter.VERTEX_TYPE_OF_REPLACEMENT);
		NqToGraphConverter.generateMapsReplacedFiles(StringArrayOfFileNames.getFileNames("predicate.final.map.final", 0, lastFileNo), 
				StringArrayOfFileNames.getFileNames("line", 0, lastFileNo), NqToGraphConverter.PREDICATE_TYPE_OF_REPLACEMENT);
		NqToGraphConverter.generateMapsReplacedFiles(StringArrayOfFileNames.getFileNames("subgraph.final.map.final", 0, lastFileNo), 
				StringArrayOfFileNames.getFileNames("line", 0, lastFileNo), NqToGraphConverter.SUBGRAPH_TYPE_OF_REPLACEMENT);
		//check if they are totally mapped.
		assertTrue(NqToGraphConverter.areNqFilesTotallyMapped(StringArrayOfFileNames.getFileNames("line", 0, lastFileNo)));
	}
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main("input.NqToGraphConverterTest"); 
	}
}
