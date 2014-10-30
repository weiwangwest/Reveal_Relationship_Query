package input;


import java.io.*;
import java.util.zip.GZIPOutputStream;

public class TripleParserTest {
	public static void main(String[] args) throws Exception {                    
		for (int i=0; i<=6; i++){
			String nqFileName="/data/data-"+i+".nq";
			GzipNqFileReader in=new GzipNqFileReader(nqFileName+".gz");			
			//PrintWriter lineWriter=new PrintWriter(new  BufferedWriter(new FileWriter(nqFileName+".parser.line", false)));
			PrintWriter lineWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.line.gz")))));
			PrintWriter subjectWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.subject.gz")))));
			PrintWriter predicateWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.predicate.gz")))));
			PrintWriter objectWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.object.gz")))));
			PrintWriter subgraphWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.subgraph.gz")))));
			PrintWriter errorWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(nqFileName+".parser.error.gz")))));
			boolean firstLine=true;
			while (in.hasNext()){
				String line=null;
				try{
					line=in.next();
					TripleParser parser=new TripleParser(line);
					//System.out.println(parser.getLine());
					if (firstLine){
						firstLine=false;
					}else{
						lineWriter.println();	
						subjectWriter.println();	
						predicateWriter.println();
						objectWriter.println();	
						subgraphWriter.println();
					}
					lineWriter.print(parser.getLine());	
					subjectWriter.print(parser.getSubject());	
					predicateWriter.print(parser.getPredicate());
					objectWriter.print(parser.getObject());	
					subgraphWriter.print(parser.getSubGraph());
				}catch(Exception e){
					errorWriter.println(line);					
				}
			}
			lineWriter.close();	//must be added
			subjectWriter.close();
			predicateWriter.close();
			objectWriter.close();
			subgraphWriter.close();
			errorWriter.close();
			in.close();
		}
	}
}
