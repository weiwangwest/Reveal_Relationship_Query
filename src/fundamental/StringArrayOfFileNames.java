package fundamental;

import java.util.ArrayList;

public class StringArrayOfFileNames {
	private static final String fileNamePrefix="/data/dataStorage/temp/data-";
	private static final String fileNameSuffix=".nq.parser.";
	public static String [] getFileNames(String whichPart, int start, int end){
		ArrayList<String>fileNames=new ArrayList<String>();
		for (int i=start; i<=end; i++){
			fileNames.add(fileNamePrefix+i+fileNameSuffix+whichPart);
		}
		return fileNames.toArray(new String[fileNames.size()]);
	}
}
