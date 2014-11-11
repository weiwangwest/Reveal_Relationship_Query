package input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/** Read a HashMap from multiple text format map files.
 * Each line of the files should has following format:
 * String int	.\n
 * @author wang
 *
 */
public class BulkFilesMapReader {
	private String [] fileNames;
	private int currentFileId;
	private GzipNqFileReader reader;
	private boolean active;
	public BulkFilesMapReader(String [] fileNames) throws IOException{
			this.fileNames=fileNames;
			currentFileId=0;
			reader=new GzipNqFileReader(this.fileNames[0]);
			active=true;
	}
	public void close(){
		reader.close();
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
	 * @throws IOException 
	 */
	public boolean hasNextLine() throws IOException{
		//ensure the next line is available if there is still a file to be read, or 
		while (currentFileId<fileNames.length-1&& !reader.hasNext()){	
			reader.close();
			currentFileId++;
			reader=new GzipNqFileReader(this.fileNames[currentFileId]);
		}
		return reader.hasNext();
	}
	public HashMap<String, Integer> nextMap(int maxSize) throws NumberFormatException, IOException {
		HashMap<String, Integer> elements = new HashMap<String, Integer>();
		while (elements.size()<maxSize && this.hasNextLine()) {
			String [] tripleString=reader.next().split(" ");
			String str=tripleString[0];
			for (int i=1; i<tripleString.length-1; i++){
					str += " " + tripleString[i];
			}
			Integer id=new Integer(tripleString[tripleString.length-1]);	//last item is an integer
			elements.put(str, id);
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
