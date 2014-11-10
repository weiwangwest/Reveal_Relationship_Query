package input;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class NqFileReader {
	Scanner scanner;
	public NqFileReader(GZIPInputStream gzipInputStream){
		this((InputStream)gzipInputStream);
	}
	public NqFileReader(String file) throws FileNotFoundException{
		this(new FileInputStream(file));
	}
	public NqFileReader(InputStream in){
		scanner=new Scanner(in);
		scanner.useDelimiter(" .\n");		
	}
	public String next (){
		return scanner.next();
	}
	public boolean hasNext(){
		return scanner.hasNext();
	}
	public void close(){
		scanner.close();
	}
	public static void assertFilesEqual(String fileName1, String fileName2) throws IOException, InterruptedException {
		ProcessBuilder   ps=new ProcessBuilder("cmp", "--silent", fileName1+" "+fileName2);
		//From the DOC:  Initially, this property is false, meaning that the 
		//standard output and error output of a subprocess are sent to two 
		//separate streams
		ps.redirectErrorStream(true);
		Process pr = ps.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			fail(line);
		}
		pr.waitFor();
		in.close();
	}
}
