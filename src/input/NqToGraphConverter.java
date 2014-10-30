package input;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import output.BulkLinesWriter;

public class NqToGraphConverter {
	/**
	 * Generates a difference file of  a set of elements of B - A.
	 * 
	 * @param A set of elements that should not appear in the resulted difference file
	 * @param B original file from which the difference file is to be generated.
	 * @param differenceFile difference file contains all elements belonging to B but not to A.
	 * @return number of lines of the difference file.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static long generateFileDifference(HashSet<String>A, Scanner B, String differenceFile) throws FileNotFoundException, IOException{
		long numberOfLines=0;
		//PrintWriter diff=new PrintWriter (new BufferedWriter (new OutputStreamWriter (new GZIPOutputStream(new FileOutputStream(differenceFile)))));
		PrintWriter diff=new PrintWriter (new BufferedWriter(new FileWriter(differenceFile)));
		while (B.hasNextLine()){
			String line=B.nextLine();
			if (!A.contains(line)){
				if (numberOfLines>0){
					diff.println();
				}
				diff.print(line);
				numberOfLines++;
			}
		}
		diff.close();
		return numberOfLines;
	}
	public static void generateFilesDifferentEachOther(String [] srcFiles) throws FileNotFoundException, IOException{
		for (int fId=0; fId<srcFiles.length; fId++){
			new File(srcFiles[fId]).renameTo(new File(srcFiles[fId]+".toCut.0"));
		}
		final int maxSize=100000;
		for (int fId=0,  lapsOfCut=0; fId<srcFiles.length;){
			BulkLinesReader reader=new BulkLinesReader(srcFiles[fId]+".toCut."+lapsOfCut);	
			HashSet<String> A=reader.nextDistinctLines(maxSize);
			if (A==null){
				reader.close();
				new File(srcFiles[fId]+".toCut."+lapsOfCut).delete();
				fId=fId+1;
				continue;
			}
			//reader.hasNextLine() && A!=null
			// append A  into current .toJoine file
			BulkLinesWriter writer=new BulkLinesWriter(new FileWriter(srcFiles[fId]+".toJoin", true));
			writer.writeLines(A);	
			writer.close();
			lapsOfCut++;
			//shrink each srcFile with A into a new "srcFile[j].toCut.lapsOfCut", delete ".toCut.lapsOfCut-1"
			long numberOfLines=0;
			for (int j=fId; j<srcFiles.length; j++){
				if (j==fId){
					numberOfLines=generateFileDifference(A, reader.getScanner(), srcFiles[fId]+".toCut."+lapsOfCut);					
					reader.close();
				}else{
					generateFileDifference(A, new Scanner(new File(srcFiles[j]+".toCut."+(lapsOfCut-1))), srcFiles[j]+".toCut."+lapsOfCut);					
				}
				new File(srcFiles[j]+".toCut."+(lapsOfCut-1)).delete();
			} 
			//	 delete the current "toCut.x" if no line left to be cut.
			if (numberOfLines==0){
				//delete current "srcFiles[fId].toCut.x" files
				new File(srcFiles[fId]+".toCut."+lapsOfCut).delete();
				//rename ".toJoin" to ".final"
				new File(srcFiles[fId]+".toJoin").renameTo(new File(srcFiles[fId]+".final"));
				fId=fId+1;
			}
		}
	}
}
