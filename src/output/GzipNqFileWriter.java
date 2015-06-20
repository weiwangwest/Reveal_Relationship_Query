package output;

import input.GzipNqFileReader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.zip.GZIPOutputStream;

import performance.Storable;

/** writes a line into a text file.
 * Each line of the file has the following format:
 * String .\n
 *
 */

@SuppressWarnings("rawtypes")
public class GzipNqFileWriter implements Storable{
	PrintWriter writer;
	String fileName;
	boolean active=false;
	//Used for compressed stream, and other outputStreams
	public GzipNqFileWriter(String fileName) throws FileNotFoundException, IOException{
		this.fileName=fileName;
		writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(fileName)))));
		active=true;
	}
	public boolean isActive(){
		return this.active;
	}
	@Override
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
	public static void writeLinesIntoFile(HashSet<String> collection, String file) throws IOException{
		GzipNqFileWriter w=new GzipNqFileWriter(file);
		w.writeLines(collection);
		w.close();
	}
	@Override
	public void put(String node) throws IOException {
		this.writeLine(node);		
	}
	@Override
	public void clear() throws IOException {
		if (this.active){
			this.close();
		}
		//clear = open, then close
		this.writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(this.fileName)))));
		this.writer.close();
		// open again
		this.writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(this.fileName)))));
		this.active=true;
	}
	public void putAllFromGzipFile(String fileName) throws Exception {
		GzipNqFileReader in =new GzipNqFileReader(fileName);
		while (in.hasNext()){
			this.put(in.next());
		}
		in.close();
	}
}
