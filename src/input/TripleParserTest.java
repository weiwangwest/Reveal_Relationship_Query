package input;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import output.NqFileWriter;

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
		for (int i=0; i<=6; i++){	//TODO: i=0; i<=6
			String nqFileName="/data/data-"+i+".nq";
			GzipNqFileReader in=new GzipNqFileReader(nqFileName+".gz");			
			//NqFileWriter lineWriter=new NqFileWriter(nqFileName+".parser.line");
			NqFileWriter lineWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.line.gz"))));
			NqFileWriter subjectWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.subject.gz"))));
			NqFileWriter predicateWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.predicate.gz"))));
			NqFileWriter objectWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.object.gz"))));
			NqFileWriter vertexWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.vertex.gz"))));			
			NqFileWriter subgraphWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.subgraph.gz"))));
			NqFileWriter errorWriter=new NqFileWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.error.gz"))));
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
