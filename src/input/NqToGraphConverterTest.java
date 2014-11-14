package input;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import fundamental.FileNameManager;

public class NqToGraphConverterTest {
	final int lastFileNo=6;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	@Test
	public void generateMultipleMapFilesTest() throws Exception{
		int index=0;
		//generate difference files and map files
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getGzipFileNames("vertex", 0, lastFileNo));		
		index = NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getGzipFileNames("vertex.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);

		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getGzipFileNames("predicate", 0, lastFileNo));
		index =NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getGzipFileNames("predicate.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);

		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getGzipFileNames("subgraph", 0, lastFileNo));
		index =NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getGzipFileNames("subgraph.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);		
	}
	@Test
	public void generateFilesDifferentEachOtherTest() throws Exception {
		//use the map to replace the lines file
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getGzipFileNames("vertex.final.map.final", 0, lastFileNo), 
				FileNameManager.getGzipFileNames("line", 0, lastFileNo), NqToGraphConverter.VERTEX_TYPE_OF_REPLACEMENT);
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getGzipFileNames("predicate.final.map.final", 0, lastFileNo), 
				FileNameManager.getGzipFileNames("line", 0, lastFileNo), NqToGraphConverter.PREDICATE_TYPE_OF_REPLACEMENT);
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getGzipFileNames("subgraph.final.map.final", 0, lastFileNo), 
				FileNameManager.getGzipFileNames("line", 0, lastFileNo), NqToGraphConverter.SUBGRAPH_TYPE_OF_REPLACEMENT);
		//check if they are totally mapped.
		assertTrue(NqToGraphConverter.areNqFilesTotallyMapped(FileNameManager.getGzipFileNames("line", 0, lastFileNo)));
	}
	@Test
	public void loadGraphFromGeneratedData() throws Exception {
		//generate difference files and map files
		for (int i=0; i<lastFileNo; i++){	
		}
	}		
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main("input.NqToGraphConverterTest"); 
	}
}
