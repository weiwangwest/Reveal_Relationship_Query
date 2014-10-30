package output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.AbstractCollection;

public class BulkLinesWriter {
	PrintWriter writer;
	boolean isFirstLine;
	boolean active;
	public BulkLinesWriter(String fileName) throws IOException{
			writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			active=true;
			isFirstLine=true;
	}
	//Used for compressed stream, and other outputStreams
	public BulkLinesWriter(Writer out){
		writer=new PrintWriter(new BufferedWriter(out));
		active=true;
		isFirstLine=true;
	}
	public void close(){
		writer.close();
		active=false;
	}
	@Override
	public void finalize(){
		if (active){
			this.close();
		}
	}
	public void writeLines(AbstractCollection<String> collection) {
		for (String line: collection){
			if (isFirstLine){
				isFirstLine=false;
			}else{
				writer.println();
			}
			writer.print(line);
		}
	}
}
