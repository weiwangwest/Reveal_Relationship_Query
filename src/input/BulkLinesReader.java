package input;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

/** reads a collection from a text file.
 * Each line of the file has the following format:
 * String .\n
 *
 */
public class BulkLinesReader {
	NqFileReader reader;
	boolean active;
	public BulkLinesReader(String fileName) throws FileNotFoundException{
			reader=new NqFileReader(fileName);
			active=true;
	}
	//Used for uncompressed stream, and other inputStreams
	public BulkLinesReader(InputStream in){
		reader=new NqFileReader(in);
		active=true;
	}
	public void close(){
		reader.close();
		active=false;
	}
	@Override
	public void finalize(){
		if (active){
			this.close();
		}
	}
	public boolean hasNextLine(){
		return reader.hasNext();
	}
	public HashSet<String> nextDistinctLines(int maxSize) {
		HashSet<String> elements = new HashSet<String>();
		while (reader.hasNext() && elements.size()<maxSize) {
			String line=reader.next();
			elements.add(line);
		}
		if (elements.size() > 0) {
			return elements;
		} else {
			return null;
		}
	}
	public NqFileReader getScanner(){
		return this.reader;
	}
	public ArrayList<String> nextLines(int maxSize) {
		ArrayList<String> elements = new ArrayList<String>();
		while (reader.hasNext() && elements.size()<maxSize) {
			elements.add(reader.next());
		}
		if (elements.size() > 0) {
			return elements;
		} else {
			return null;
		}
	}
}
