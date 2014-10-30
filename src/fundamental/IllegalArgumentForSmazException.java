package fundamental;

@SuppressWarnings("serial")
public class IllegalArgumentForSmazException extends IllegalArgumentException {
	String originalStr, resultStr;
	public IllegalArgumentForSmazException(String originalStr, String resultStr){
		this.originalStr=originalStr;
		this.resultStr=resultStr;
	}
	@Override
	public String getMessage(){
		return ("Original string contains non-ASCII char! Replacing the char..."
					+"Orginal string: \""+originalStr+"\", "
					+"Result string: \""+resultStr+"\"");
	}
}
