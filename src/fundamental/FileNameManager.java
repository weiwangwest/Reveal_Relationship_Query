package fundamental;

import java.util.ArrayList;

public class FileNameManager {
	//private static final String fileNamePrefix="/data/dataStorage/temp/data-";	//for r50:
	public static final String pathToDataFiles="/data/";
	private static final String fileNamePrefix="data-";		//for reveal
	private static final String fileNameSuffix=".nq";
	private static final String gzipFileNameSuffix=".gz";

	public static String getDataFileNamePrefix(){
		return pathToDataFiles+fileNamePrefix;
	}
	public static String getNqDataFileName(int i){
		return getNqDataFileName(String.valueOf(i));		
	}	
	public static String getNqDataFileName(String i){
		return getDataFileNamePrefix()+i+fileNameSuffix;		
	}
	public static String getNqDataFilePlusName(String i, String appendix){
		return getNqDataFileName(i)+appendix;
	}
	public static String getNqDataFilePlusName(int i, String appendix){
		return getNqDataFilePlusName(String.valueOf(i), appendix);
	}	
	public static String getNqGzipDataFileName(String idx, String whichPart){
		return getNqDataFilePlusName(idx, whichPart)+gzipFileNameSuffix;
	}	
	public static String getNqGzipDataFileName(int idx, String whichPart){
		return getNqGzipDataFileName(String.valueOf(idx), whichPart);
	}
	public static String getChangedGzipFileName(String previousName, String extensionName){
		return previousName.replace(".gz", "") + extensionName + ".gz";
	}
	public static String [] getNqDataFilePlusNames(int start, int end, String whichPart){
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=start; i<=end; i++){
			fileNames.add(getNqDataFilePlusName(i, whichPart));
		}
		return fileNames.toArray(new String[fileNames.size()]);
	}	
	public static String [] getGzipDataFileNames(int start, int end, String whichPart){
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=start; i<=end; i++){
			fileNames.add(getNqGzipDataFileName(i, whichPart));
		}
		return fileNames.toArray(new String[fileNames.size()]);
	}
	public static String[]  getGzipAccumulatedDataFileNames(int start, int end, String whichPart) throws Exception{
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=start; i<=end; i++){
			fileNames.add(getGzipAccumulatedDataFileName(start,  end, whichPart));
		}
		return fileNames.toArray(new String[fileNames.size()]);		
	}
	public static String getGzipAccumulatedDataFileName(int start, int end, String whichPart) throws Exception{
			if (end<=start){
				throw new Exception("start index must be smaller than the end index");
			}
			return (getDataFileNamePrefix()+start+"_"+end+whichPart);
	}
}
