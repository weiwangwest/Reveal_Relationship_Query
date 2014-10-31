package input;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import output.BulkLinesWriter;

public class NqToGraphConverter {
	/**
	 * Scans all RDF parts in file B, when any part of a RDF can be found as a key of A, 
	 * the part is replaced with corresponding value. Writes the resulting file into mappedFile.
	 * 
	 * @param A map of keyword and Integer.
	 * @param B original file from which the resulting file is to be generated.
	 * @param mappedFile resulting file.
	 * @return total number of lines replaced.
	 * @throws Exception 
	 */
	public static long generateMappReplacedFile(HashMap<String, Integer>A, Scanner B, String mappedFile) throws Exception{
		long numberOfLines=0;
		long numberOfLinesReplaced=0;
		//PrintWriter diff=new PrintWriter (new BufferedWriter (new OutputStreamWriter (new GZIPOutputStream(new FileOutputStream(differenceFile)))));
		PrintWriter mapped=new PrintWriter (new BufferedWriter(new FileWriter(mappedFile)));
		while (B.hasNextLine()){
			String line=B.nextLine();
			boolean changed=false;
			TripleParser parser=new TripleParser(line);
			if (A.containsKey(parser.getSubject())){
				parser.setSubject(A.get(parser.getSubject()).toString());
				changed=true;
			}
			if (A.containsKey(parser.getPredicate())){
				parser.setPredicate(A.get(parser.getPredicate()).toString());
				changed=true;
			}
			if (A.containsKey(parser.getObject())){
				parser.setObject(A.get(parser.getObject()).toString());
				changed=true;
			}
			if (A.containsKey(parser.getSubGraph())){
				parser.setSubGraph(A.get(parser.getSubGraph()).toString());
				changed=true;
			}
			if (numberOfLines>0){
				mapped.println();
			}
			mapped.print(parser.getLine());
			numberOfLines++;
			if (changed){
				numberOfLinesReplaced++;
			}
		}
		mapped.close();
		return numberOfLinesReplaced;
	}	
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
	/**
	 * @param mapFiles must be provided in ascending order according to their data set IDs
	 * @param srcFiles must be provided in ascending order according to their data set IDs
	 * @throws Exception
	 */
	public static void generateMapsReplacedFiles(String [] mapFiles, String [] srcFiles) throws Exception{
		int lapsOfCut=0;
		for (String file: srcFiles){
			new File(file).renameTo(new File(file+".toReplace."+lapsOfCut));
		}
		final int maxSize=100000;
		BulkFilesMapReader reader=new BulkFilesMapReader(mapFiles);
		while (reader.hasNextLine()){
			HashMap<String, Integer> A=reader.nextMap(maxSize);
			lapsOfCut++;
			for (int i=reader.getCurrentMapFileId(); i<srcFiles.length; i++){
				String file=srcFiles[i];
				NqToGraphConverter.generateMappReplacedFile(A, new Scanner(new File(file)), file+".toReplace."+lapsOfCut);				
				new File(file+".toReplace."+lapsOfCut).delete();
			}
			reader.close();	
			for (String file: srcFiles){
				for (int i=0; i<=lapsOfCut; i++){
					new File(file+".toReplace."+i).renameTo(new File(file+".final"));					
				}
			}
		}
	}
	/**
	 * @param fileName Text file that needs to have line numbers added
	 * @param previousIndex for the first line of this file, index=previousIndex + 1 
	 * @return total number of lines in the file
	 * @throws IOException
	 */
	public static int generateMapFile(String fileName, int previousIndex) throws IOException{
		Scanner in=new Scanner(fileName);
		PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(fileName+".map.temp")));
		int index;
		for (index=previousIndex+1; in.hasNextLine(); index++){
			String line=in.nextLine();
			line = line + " "+index;
			if (index>previousIndex+1){
				out.println();
			}
			out.print(line);
		}
		out.close();
		new File(fileName+".map.temp").renameTo(new File(fileName+".map.final"));
		return index-previousIndex-1;
	}
	/**
	 * @param fileNames Files to be continuously numbered together
	 * @param previousIndex index for the first line of the first file = perviousIndex + 1
	 * @return total number of lines.
	 * @throws IOException
	 */
	public static int generateMultipleMapFiles(String [] fileNames, int previousIndex) throws IOException{
		int index=previousIndex+1;
		for (String file: fileNames){
			int increment=generateMapFile(file, index);
			index += increment;
		}
		return index;
	}
	/**
	 * @param nqFile the nq file to be checked.
	 * @return true if every sub-pred-obj-subgraph of the nqFile has been mapped into integers.	 * 	
	 * @throws Exception
	 */
	public static boolean isNqFileMapped(String nqFile) throws Exception{
		boolean isMapped=true;
		NqFileReader in=new NqFileReader(nqFile);
		while (in.hasNext()){
			String line=in.next();
			TripleParser parser=new TripleParser(line);
			try{
				Integer.valueOf(parser.getSubject());
				Integer.valueOf(parser.getPredicate());
				Integer.valueOf(parser.getObject());
				Integer.valueOf(parser.getSubGraph());
			}catch(NumberFormatException e){
				isMapped=false;
				break;
			}
		}
		in.close();
		return isMapped;
	}
	public static boolean areNqFilesTotallyMapped(String [] nqFiles) throws Exception{
		boolean isMapped=true;
		for (String file: nqFiles){
			if (!isNqFileMapped(file)){
				isMapped=false;
				break;
			}
		}
		return isMapped; 
	}
}