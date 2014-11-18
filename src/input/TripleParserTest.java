package input;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import fundamental.FileNameManager;
import output.GzipNqFileWriter;

public class TripleParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testTriplesPartiallyReplacedByInteger() throws Exception {
		String line="subject predicate object subgraph";
		TripleParser parser=new TripleParser(line);
		assertEquals(parser.getSubject(), "subject");
		assertEquals(parser.getPredicate(),"predicate");
		assertEquals(parser.getObject(),"object");
		assertEquals(parser.getSubGraph(), "subgraph");
		assertEquals(parser.getWholeLine(),"subject predicate object subgraph .");
	}
	@Test
	public void testFile() throws Exception {              
		for (int i=0; i<=3; i++){
			String nqFileName=FileNameManager.fileNamePrefix+i+FileNameManager.fileNameSuffix;
			GzipNqFileReader in=new GzipNqFileReader(nqFileName+".gz");			
			GzipNqFileWriter lineWriter=new GzipNqFileWriter(nqFileName+".parser.line.gz");
			GzipNqFileWriter subjectWriter=new GzipNqFileWriter(nqFileName+".parser.subject.gz");
			GzipNqFileWriter predicateWriter=new GzipNqFileWriter(nqFileName+".parser.predicate.gz");
			GzipNqFileWriter objectWriter=new GzipNqFileWriter(nqFileName+".parser.object.gz");
			GzipNqFileWriter vertexWriter=new GzipNqFileWriter(nqFileName+".parser.vertex.gz");			
			GzipNqFileWriter subgraphWriter=new GzipNqFileWriter(nqFileName+".parser.subgraph.gz");
			GzipNqFileWriter errorWriter=new GzipNqFileWriter(nqFileName+".parser.error.gz");
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
	}
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main("input.TripleParserTest"); 
	}
}
