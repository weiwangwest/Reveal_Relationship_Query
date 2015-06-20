package input;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import output.GzipNqFileWriter;

import fundamental.FileNameManager;
import fundamental.Log4JPropertiesTest;
import fundamental.OSCommander;

public class NqToGraphConverterTest  {
	final int lastFileNo=6;
	private static Logger log = Logger.getLogger(Log4JPropertiesTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	@Ignore
	public void parser() throws FileNotFoundException, IOException{
		log.debug(new Date()+ " parser starts.");
		for (int i=0; i<=lastFileNo; i++){
			String nqFileName=FileNameManager.getNqDataFilePlusName(i, "");
			//data-i.nq.gz
			GzipNqFileReader in=new GzipNqFileReader(nqFileName+".gz");			
			//data-i.nq.line.gz
			GzipNqFileWriter lineWriter=new GzipNqFileWriter(nqFileName+".line.gz");
			//data-i.nq.subject.gz
			GzipNqFileWriter subjectWriter=new GzipNqFileWriter(nqFileName+".subject.gz");
			//data-i.nq.predicate.gz
			GzipNqFileWriter predicateWriter=new GzipNqFileWriter(nqFileName+".predicate.gz");
			//data-i.nq.object.gz
			GzipNqFileWriter objectWriter=new GzipNqFileWriter(nqFileName+".object.gz");
			//data-i.nq.vertex.gz
			GzipNqFileWriter vertexWriter=new GzipNqFileWriter(nqFileName+".vertex.gz");			
			//data-i.nq.subgraph.gz
			GzipNqFileWriter subgraphWriter=new GzipNqFileWriter(nqFileName+".subgraph.gz");
			//data-i.nq.error.gz
			GzipNqFileWriter errorWriter=new GzipNqFileWriter(nqFileName+".error.gz");
			while (in.hasNext()){
				String line=null;
				try{
					line=in.next();
					TripleParser parser=new TripleParser(line);
					//System.out.println(parser.getLine());
					lineWriter.writeLine(parser.getNQuad());	
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
	}

	@Test
	@Ignore
	public void differenceFiles() throws Exception{
		log.debug(new Date() +" " + "differenceFiles starts.");
		OSCommander.exec("rm", "/data/data-?.nq.*.gz.toCut.*");
		OSCommander.exec("cp", "/data/data-?.nq.vertex.gz /data/");
		OSCommander.exec("cp", "/data/data-?.nq.predicate.gz /data/");
		OSCommander.exec("cp", "/data/data-?.nq.subgraph.gz /data/");
		//data-i.nq.vertex.gz -> data-i.nq.vertex.final.gz
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".vertex"));		
		//data-i.nq.predicate.gz -> data-i.nq.predicate.final.gz
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".predicate"));
		//data-i.nq.subgraph.gz -> data-i.nq.subgraph.final.gz
		NqToGraphConverter.generateFilesDifferentEachOther(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".subgraph"));
		OSCommander.exec("mv", "/data/data-?.nq.*.gz /data/storage/");	
		log.debug(new Date() +" " + "differenceFiles ends.");
	}

	@Test
	@Ignore
	public void mapFiles() throws Exception{
		log.debug(new Date() +" " + "mapFiles starts.");
		//OSCommander.exec("cp", "/data/storage/data-?.nq.*.final.gz /data/");	
		long index=0;
		//data-i.nq.vertex.final.gz -> data-i.nq.vertex.final.map.final.gz
		index = NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".vertex.final"), index);
		System.out.println("maxmun index="+index);
		log.debug(new Date()+ " vertex map generated, maxmun index="+index);
		//data-i.nq.predicate.final.final.gz -> data-i.nq.predicate.final.map.final.gz
		index =NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".predicate.final"), index);
		System.out.println("maxmun index="+index);
		log.debug(new Date()+ " predicate map generated, maxmun index="+index);
		//data-i.nq.subgraph.final.gz -> data-i.nq.subgraph.final.map.final.gz
		index =NqToGraphConverter.generateMultipleMapFiles(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".subgraph.final"), index);
		System.out.println("maxmun index="+index);
		//OSCommander.exec("mv", "/data/data-?.nq.*.final.map.final.gz /data/storage/");	
		log.debug(new Date()+ " subgraph map generated, maxmun index="+index);
	}
	
	@Test
	//@Ignore
	public void mapReplacedFiles() throws Exception{
		log.debug(new Date()+ " mapReplacedFiles starts.");
		// "cp /data/storage/*.map.final.gz /data/"
		// "cp /data/storage/data-?.nq.gz /data/"
		// data-i.nq.gz ------(data-i.nq.vertex.final.map.final.gz)------> data-i.nq.gz
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".vertex.final.map.final"), 
				FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ""), NqToGraphConverter.VERTEX_TYPE_OF_REPLACEMENT);
		log.debug(new Date()+ " lines have been replaced by vertex map");
		// data-i.nq.gz ------(data-i.nq.predicate.final.map.final.gz)------> data-i.nq.gz
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".predicate.final.map.final"), 
				FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ""), NqToGraphConverter.PREDICATE_TYPE_OF_REPLACEMENT);
		log.debug(new Date()+ " lines have been replaced by predicate map");
		// data-i.nq.gz ------(data-i.nq.subgraph.final.map.final.gz)------> data-i.nq.gz
		NqToGraphConverter.generateMapsReplacedFiles(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".subgraph.final.map.final"), 
				FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ""), NqToGraphConverter.SUBGRAPH_TYPE_OF_REPLACEMENT);
		log.debug(new Date()+ " lines have been replaced by subgraph map");
		// data-i.nq.gz -> data-i.nq.int.gz
		for (int i=0; i<=lastFileNo; i++){
			new File(FileNameManager.getNqGzipDataFilePlusName(i, "")).renameTo(new File(FileNameManager.getNqGzipDataFilePlusName(i, ".int")));
		}
		//check whether every part of data-i.nq.int.gz is mapped into an integer.
		assertTrue(NqToGraphConverter.areNqFilesTotallyMapped(FileNameManager.getNqGzipDataFilePlusNames(0, lastFileNo, ".int")));
		log.debug(new Date()+ " lines have been totally replaced by maps");
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
