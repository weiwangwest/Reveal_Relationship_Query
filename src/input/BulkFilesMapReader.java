package input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class BulkFilesMapReader {
	private String [] fileNames;
	private int currentFileId;
	private Scanner scanner;
	private boolean active;
	public BulkFilesMapReader(String [] fileNames) throws FileNotFoundException{
			this.fileNames=fileNames;
			currentFileId=0;
			scanner=new Scanner(new File(this.fileNames[0]));
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
	/**
	 * @return true if there is at least a lines which is unread from the files
	 * @throws FileNotFoundException 
	 */
	public boolean hasNextLine() throws FileNotFoundException{
		//ensure the next line is available if there is still a file to be read, or 
		while (!scanner.hasNextLine() && currentFileId<fileNames.length-1){	
			scanner.close();
			currentFileId++;
			scanner=new Scanner(new File(this.fileNames[currentFileId]));
		}
		return scanner.hasNextLine();
	}
	public HashMap<String, Integer> nextMap(int maxSize) throws NumberFormatException, FileNotFoundException {
		HashMap<String, Integer> elements = new HashMap<String, Integer>();
		while (elements.size()<maxSize && this.hasNextLine()) {
			String [] doubleStr=scanner.nextLine().split(" ");
			elements.put(doubleStr[0], new Integer(doubleStr[1]));
		}
		if (elements.size() > 0) {
			return elements;
		} else {
			return null;
		}
	}
	public int getCurrentMapFileId(){
		return this.currentFileId;
	}
}
