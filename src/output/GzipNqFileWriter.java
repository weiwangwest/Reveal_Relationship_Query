package output;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.zip.GZIPOutputStream;

/** writes a line into a text file.
 * Each line of the file has the following format:
 * String .\n
 *
 */

public class GzipNqFileWriter {
	PrintWriter writer;
	boolean active=false;
	//Used for compressed stream, and other outputStreams
	public GzipNqFileWriter(String fileName) throws FileNotFoundException, IOException{
		writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(fileName)))));
		active=true;
	}
	public boolean isActive(){
		return this.active;
	}
	public void close() throws IOException{
		writer.close();
		active=false;
	}
	@Override
	public void finalize() throws IOException{
		if (active){
			this.close();
		}
	}
	public void writeLine(String line) throws IOException {
		writer.println(line+" .");
	}
	public void writeLines(HashSet<String> collection) throws IOException {
		for (String str: collection){
			this.writeLine(str);
		}
	}
}
