package input;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;


import com.google.common.io.Files;

public class GzipNqFileReader {
	Scanner scanner;
	public GzipNqFileReader(String fileName) throws FileNotFoundException, IOException{
		scanner=new Scanner(new GZIPInputStream(new FileInputStream(fileName)));
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
		assertTrue(Files.equal(new File(fileName1) , new File(fileName2)));
/*		ProcessBuilder   ps=new ProcessBuilder("cmp", "--silent", fileName1+" "+fileName2);
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
*/	}
}
