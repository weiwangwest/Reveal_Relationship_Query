package fundamental;

import java.util.ArrayList;

public class FileNameManager {
	
	//private static final String fileNamePrefix="/data/dataStorage/temp/data-";	//for r50:
	public static final String fileNamePrefix="/data/data-";		//for reveal
	public static final String fileNameSuffix=".nq";
	public static final String gzipFileNameSuffix=".gz";

	public static String getFileName(String whichPart, int idx){
		return fileNamePrefix+idx+fileNameSuffix+whichPart;
	}
	public static String getGzipFileName(String previousName, String extensionName){
		return previousName.replace(".gz", "") + extensionName + ".gz";
	}
	public static String [] getFileNames(String whichPart, int start, int end){
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=start; i<=end; i++){
			fileNames.add(getFileName(whichPart, i));
		}
		return fileNames.toArray(new String[fileNames.size()]);
	}
	
	public static String [] getGzipFileNames(String whichPart, int start, int end){
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=start; i<=end; i++){
			fileNames.add(getFileName(whichPart, i) + gzipFileNameSuffix);
		}
		return fileNames.toArray(new String[fileNames.size()]);
	}
}
