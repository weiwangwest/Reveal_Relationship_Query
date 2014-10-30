package input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class BulkLinesReader {
	Scanner scanner;
	boolean active;
	public BulkLinesReader(String fileName) throws FileNotFoundException{
			scanner=new Scanner(new File(fileName));
			active=true;
	}
	//Used for uncompressed stream, and other inputStreams
	public BulkLinesReader(InputStream in){
		scanner=new Scanner(in);
		active=true;
	}
	public void close(){
		scanner.close();
		active=false;
	}
	@Override
	public void finalize(){
		if (active){
			this.close();
		}
	}
	public boolean hasNextLine(){
		return scanner.hasNextLine();
	}
	public HashSet<String> nextDistinctLines(int maxSize) {
		HashSet<String> elements = new HashSet<String>();
		while (scanner.hasNextLine() && elements.size()<maxSize) {
			String line=scanner.nextLine();
			elements.add(line);
		}
		if (elements.size() > 0) {
			return elements;
		} else {
			return null;
		}
	}
	public Scanner getScanner(){
		return this.scanner;
	}
	public ArrayList<String> nextLines(int maxSize) {
		ArrayList<String> elements = new ArrayList<String>();
		while (scanner.hasNextLine() && elements.size()<maxSize) {
			elements.add(scanner.nextLine());
		}
		if (elements.size() > 0) {
			return elements;
		} else {
			return null;
		}
	}
}
