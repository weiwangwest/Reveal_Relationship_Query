package fundamental;
import org.apache.commons.lang3.builder.*;
public class CompressedString {
	private static Smaz smaz=new Smaz();	//compressor
	private byte[] compressedData;	
	public CompressedString(String inString){
		byte[] bytes=smaz.compress(inString);
		this.compressedData=new byte[bytes.length];
		for (int i=0; i<bytes.length; i++){
			this.compressedData[i]=bytes[i];
		}
	}
	public String getString(){
		return smaz.decompress(this.compressedData);
	}
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31).append(compressedData). toHashCode();
    }
	@Override
	public boolean equals(Object o){
		if (!(o instanceof CompressedString)){
			return false;
		}
		if (this==o){
			return true;
		}
		CompressedString that=(CompressedString) o;
		if (this.compressedData.length!=that.compressedData.length){
			return false;
		}
		for (int i=0; i<this.compressedData.length; i++){
			if (this.compressedData[i]!=that.compressedData[i]){
				return false;
			}
		}
		return true;
	}
}
