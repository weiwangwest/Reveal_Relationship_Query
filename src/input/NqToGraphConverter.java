package input;

import java.io.*;
import java.util.*;

import fundamental.FileNameManager;

import output.GzipNqFileWriter;

public class NqToGraphConverter {
	public static final int VERTEX_TYPE_OF_REPLACEMENT=0;
	public static final int PREDICATE_TYPE_OF_REPLACEMENT=1;
	public static final int SUBGRAPH_TYPE_OF_REPLACEMENT=2;

	//related to java.lang.OutOfMemoryError: Java heap space
	static final int  maxSize=5000000;	//	max int (32bit) = 2,147,483,647

	/**
	 * Scans all RDF parts in file B, when any part of a RDF can be found as a key of A, 
	 * the part is replaced with corresponding value. Writes the resulting file into mappedFile.
	 * 
	 * @param A map of keyword and Integer.
	 * @param nqFileReader original file from which the resulting file is to be generated.
	 * @param mappedFile resulting file.
	 * @return total number of lines replaced.
	 * @throws Exception 
	 */
	public static long generateMapReplacedFile(HashMap<String, Integer>A, GzipNqFileReader nqFileReader, String mappedFile, int typeOfReplacement) throws Exception{
		long numberOfLinesReplaced=0;
		//NqFileWriter diff=new NqFileWriter (new GZIPOutputStream(new FileOutputStream(differenceFile));
		GzipNqFileWriter mapped=new GzipNqFileWriter (mappedFile);
		while (nqFileReader.hasNext()){
			String line=nqFileReader.next();
			TripleParser parser=new TripleParser(line);
			boolean changed=false;
			switch (typeOfReplacement){
				case VERTEX_TYPE_OF_REPLACEMENT:
					if (A.containsKey(parser.getSubject())){
						parser.setSubject(A.get(parser.getSubject()).toString());
						changed=true;
					}
					if (A.containsKey(parser.getObject())){
						parser.setObject(A.get(parser.getObject()).toString());
						changed=true;
					}
					break;
				case PREDICATE_TYPE_OF_REPLACEMENT:
					if (A.containsKey(parser.getPredicate())){
						parser.setPredicate(A.get(parser.getPredicate()).toString());
						changed=true;
					}
					break;
				case SUBGRAPH_TYPE_OF_REPLACEMENT:
					if (A.containsKey(parser.getSubGraph())){
						parser.setSubGraph(A.get(parser.getSubGraph()).toString());
						changed=true;
					}
					break;
				default:					
			}
			mapped.writeLine(parser.getLine());
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
	 * @param nqFileReader original file from which the difference file is to be generated.
	 * @param differenceFile difference file contains all elements belonging to B but not to A.
	 * @return number of lines of the difference file.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static long generateFileDifference(HashSet<String>A, GzipNqFileReader nqFileReader, String differenceFile) throws FileNotFoundException, IOException{
		long numberOfLines=0;
		//NqFileWriter diff=new NqFileWriter (new OutputStreamWriter (new GZIPOutputStream(new FileOutputStream(differenceFile)));
		GzipNqFileWriter diff=new GzipNqFileWriter(differenceFile);
		while (nqFileReader.hasNext()){
			String line=nqFileReader.next();
			if (!A.contains(line)){
				diff.writeLine(line);
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
		GzipNqFileWriter writer=null;
		for (int fId=0,  lapsOfCut=0; fId<srcFiles.length;){
			if (writer==null){
				writer=new GzipNqFileWriter(srcFiles[fId]+".toJoin");
			}
			BulkLinesReader reader=new BulkLinesReader(srcFiles[fId]+".toCut."+lapsOfCut);	
			HashSet<String> A=reader.nextDistinctLines(maxSize);
			if (A==null){
				reader.close();
				//	 delete the current "toCut.x" if no line left to be cut. 
				//delete current "srcFiles[fId].toCut.x" files
				new File(srcFiles[fId]+".toCut."+lapsOfCut).delete();
				//rename ".toJoin" to ".final"
				if (writer!=null){
					writer.close();
					writer=null;
				}
				new File(srcFiles[fId]+".toJoin").renameTo(new File(FileNameManager.getChangedGzipFileName(srcFiles[fId], ".final")));
				fId=fId+1;
				continue;
			}
			//append A  into current .toJoin file
			writer.writeLines(A);	
			lapsOfCut++;
			//shrink each srcFile with A into a new "srcFile[j].toCut.lapsOfCut", delete ".toCut.lapsOfCut-1"
			//long numberOfLines=0;
			for (int j=fId; j<srcFiles.length; j++){
				if (j==fId){
					//numberOfLines=
					generateFileDifference(A, reader.getScanner(), srcFiles[fId]+".toCut."+lapsOfCut);					
					reader.close();
				}else{
					generateFileDifference(A, new GzipNqFileReader(srcFiles[j]+".toCut."+(lapsOfCut-1)), srcFiles[j]+".toCut."+lapsOfCut);					
				}
				new File(srcFiles[j]+".toCut."+(lapsOfCut-1)).delete();
			} 
		}
	}
	/**
	 * @param mapFiles must be provided in ascending order according to their data set IDs
	 * @param srcFiles must be provided in ascending order according to their data set IDs
	 * @throws Exception
	 */
	public static void generateMapsReplacedFiles(String [] mapFiles, String [] srcFiles, int typeOfReplacement) throws Exception{
		int lapsOfCut=0;
		for (String file: srcFiles){
			new File(file).renameTo(new File(file+".toReplace."+lapsOfCut));
		}
		BulkFilesMapReader reader=new BulkFilesMapReader(mapFiles);
		while (reader.hasNextLine()){
			HashMap<String, Integer> A=reader.nextMap(maxSize);
			lapsOfCut++;
			for (int i=0; i<srcFiles.length; i++){
				String file=srcFiles[i];
				NqToGraphConverter.generateMapReplacedFile(A, new GzipNqFileReader(file+".toReplace."+(lapsOfCut-1)), file+".toReplace."+lapsOfCut, typeOfReplacement);				
				new File(file+".toReplace."+(lapsOfCut-1)).delete();
			}
		}
		reader.close();	
		for (String file: srcFiles){
			for (int i=0; i<=lapsOfCut; i++){
				new File(file+".toReplace."+i).renameTo(new File(file));					
			}
		}
	}
	/**
	 * @param fileName Text file that needs to have line numbers added
	 * @param previousIndex for the first line of this file, index=previousIndex + 1 
	 * @return total number of lines in the file
	 * @throws IOException
	 */
	public static long generateMapFile(String fileName, long previousIndex) throws IOException{
		GzipNqFileReader in=new GzipNqFileReader(fileName);
		GzipNqFileWriter out=new GzipNqFileWriter(fileName+".map.temp");
		long index;
		for (index=previousIndex+1; in.hasNext(); index++){
			String line=in.next();
			line = line + " "+index;
			out.writeLine(line);
		}
		out.close();
		new File(fileName+".map.temp").renameTo(new File(FileNameManager.getChangedGzipFileName(fileName, ".map.final")));
		return index-previousIndex-1;
	}
	/**
	 * @param fileNames Files to be continuously numbered together
	 * @param previousIndex index for the first line of the first file = perviousIndex + 1
	 * @return total number of lines.
	 * @throws IOException
	 */
	public static long generateMultipleMapFiles(String [] fileNames, long previousIndex) throws IOException{
		long index=previousIndex;
		for (String file: fileNames){
			index += generateMapFile(file, index);
		}
		return index;
	}
	/**
	 * @param nqFile the nq file to be checked.
	 * @return true if every sub-pred-obj-subgraph of the nqFile has been mapped into integers.	 * 	
	 * @throws Exception
	 */
	public static boolean isNqFileMapped(String nqFile, boolean isGzip) throws Exception{
		boolean isMapped=true;
		GzipNqFileReader in;
		if (isGzip){
			in=new GzipNqFileReader(nqFile);
		}else{
			in=new GzipNqFileReader(nqFile);
		}
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
			if (!isNqFileMapped(file, false)){
				isMapped=false;
				break;
			}
		}
		return isMapped; 
	}
}