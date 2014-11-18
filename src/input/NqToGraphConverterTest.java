package input;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import output.GzipNqFileWriter;

import fundamental.FileNameManager;
import fundamental.Log4JPropertiesTest;

public class NqToGraphConverterTest  {
	final int lastFileNo=6;	//TODO: lastFileNo=6;
	private static Logger log = Logger.getLogger(Log4JPropertiesTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	@Test
	public void generateMultipleMapFilesTest() throws Exception{
		log.debug(new Date()+ " program starts.");
		for (int i=0; i<=lastFileNo; i++){
			String nqFileName=FileNameManager.fileNamePrefix+i+FileNameManager.fileNameSuffix;
			GzipNqFileReader in=new GzipNqFileReader(nqFileName+".gz");			
			GzipNqFileWriter lineWriter=new GzipNqFileWriter(nqFileName+".line.gz");
			GzipNqFileWriter subjectWriter=new GzipNqFileWriter(nqFileName+".subject.gz");
			GzipNqFileWriter predicateWriter=new GzipNqFileWriter(nqFileName+".predicate.gz");
			GzipNqFileWriter objectWriter=new GzipNqFileWriter(nqFileName+".object.gz");
			GzipNqFileWriter vertexWriter=new GzipNqFileWriter(nqFileName+".vertex.gz");			
			GzipNqFileWriter subgraphWriter=new GzipNqFileWriter(nqFileName+".subgraph.gz");
			GzipNqFileWriter errorWriter=new GzipNqFileWriter(nqFileName+".error.gz");
			while (in.hasNext()){
				String line=null;
				try{
					line=in.next();
					TripleParser parser=new TripleParser(line);
					//System.out.println(parser.getLine());
					lineWriter.writeLine(parser.getLine());	
					subjectWriter.writeLine(parser.getSubject());	
					predicateWriter.writeLine(parser.getPredicate());
					objectWriter.writeLine(parser.getObject());	
					vertexWriter.writeLine(parser.getSubject());
					vertexWriter.writeLine(parser.getObject());
					subgraphWriter.writeLine(parser.getSubGraph());
				}catch(Exception e){
					errorWriter.writeLine(line);					
				}
			}
			lineWriter.close();	//must be added
			subjectWriter.close();
			predicateWriter.close();
			objectWriter.close();
			vertexWriter.close();
			subgraphWriter.close();
			errorWriter.close();
			in.close();
		}
		log.debug(new Date()+ " parser work finished.");

		long index=0;
		//generate difference files and map files
		//data-i.nq.subgraph.final.gz
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getGzipFileNames(".subgraph", 0, lastFileNo));
		//data-i.nq.subgraph.final.map.final.gz
		index =NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getGzipFileNames(".subgraph.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);
		log.debug(new Date()+ " subgraph map generated, maxmun index="+index);
		//use the subgraph map to replace the lines file
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getGzipFileNames(".subgraph.final.map.final", 0, lastFileNo), 
				FileNameManager.getGzipFileNames(".line", 0, lastFileNo), NqToGraphConverter.SUBGRAPH_TYPE_OF_REPLACEMENT);
		log.debug(new Date()+ " lines have been replaced by subgraph map");
		
		//data-i.nq.predicate.final.gz
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getGzipFileNames(".predicate", 0, lastFileNo));
		//data-i.nq.predicate.final.map.final.gz
		index =NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getGzipFileNames(".predicate.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);
		log.debug(new Date()+ " predicate map generated, maxmun index="+index);
		//use the predicate map to replace the lines file
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getGzipFileNames(".predicate.final.map.final", 0, lastFileNo), 
				FileNameManager.getGzipFileNames(".line", 0, lastFileNo), NqToGraphConverter.PREDICATE_TYPE_OF_REPLACEMENT);
		log.debug(new Date()+ " lines have been replaced by predicate map");

		//data-i.nq.vertex.final.gz, 
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getGzipFileNames(".vertex", 0, lastFileNo));		
		//data-i.nq.vertex.final.map.final.gz
		index = NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getGzipFileNames(".vertex.final", 0, lastFileNo), index);
		System.out.println("maxmun index="+index);
		log.debug(new Date()+ " vertex map generated, maxmun index="+index);
		//use the vertex map to replace the lines file
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getGzipFileNames(".vertex.final.map.final", 0, lastFileNo), 
				FileNameManager.getGzipFileNames(".line", 0, lastFileNo), NqToGraphConverter.VERTEX_TYPE_OF_REPLACEMENT);
		log.debug(new Date()+ " lines have been replaced by vertex map");
		
		//check if they are totally mapped.
		assertTrue(NqToGraphConverter.areNqFilesTotallyMapped(FileNameManager.getGzipFileNames(".line", 0, lastFileNo)));
		log.debug(new Date()+ " lines have been totally replaced by maps");
		log.debug("all done.");
	}
/*	@Test
	public void loadGraphFromGeneratedData() throws Exception {
		//generate difference files and map files
		for (int i=0; i<lastFileNo; i++){	
		}
	}		
*/
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main("input.NqToGraphConverterTest"); 
	}
}
