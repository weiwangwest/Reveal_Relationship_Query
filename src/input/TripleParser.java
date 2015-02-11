package input;

public class TripleParser {
	private String tripleLine;
	private String subject;
	private String object;
	private String predicate;
	private String subGraph;
	public TripleParser(String line) throws Exception{
		this.tripleLine=line;
		String[] parts=this.tripleLine.split(" ");
		if (parts.length<4){
			throw new Exception("wrong triple format:"+parts+" from line:\n"+line);			
		}
		if (parts.length==4){
			subject=parts[0];
			predicate=parts[1];
			object=parts[2];
			subGraph=parts[3];
			return;
		}
		subGraph=parts[parts.length-1];	//1. the last part is always subGraph
		int positionOfPredicate=-1;		//2. look for the predicate, because it's easy to find
		for (int i=1; i<=parts.length-3; i++){
			if (parts[i].startsWith("<")&&parts[i].endsWith(">")){
				positionOfPredicate=i;
				predicate=parts[positionOfPredicate];
				break;
			}
		}
		if (positionOfPredicate==-1){
			throw new Exception("wrong triple format:"+parts+" from line:\n"+line);			
		}
		subject = parts[0];		// get the subject, after predicate has been found
		for (int i=1; i<positionOfPredicate;i++){
			subject += " "+parts[i];
		}
		object = parts[positionOfPredicate+1];	// get the object, after predicate has been found
		for (int i=positionOfPredicate+2; i<=parts.length-2; i++){
			object += " "+parts[i];
		}
	}
	public String getSubject(){
		return subject;
	}
	public void setSubject(String sub){
		this.subject=sub;
	}
	public String getPredicate(){
		return predicate;
	}
	public void setPredicate(String pred){
		this.predicate=pred;
	}
	public String getObject(){
		return object;
	}
	public void setObject(String obj){
		this.object=obj;
	}
	public String getSubGraph(){
		return subGraph;
	}
	public void setSubGraph(String subg){
		this.subGraph=subg;
	}
	//return the triple, without the subgraph
	//triple has the form: <subject> <predicate> <object> .
	public String getTriple(){
		return subject+" "+predicate+" "+object+" "+".";
	}
	//return the NQuard
	//NQuard has the form: <subject> <predicate> <object> <context> .
	public String getNQuad(){
		if (subGraph==null || subGraph.equals("")){
			return subject+" "+predicate+" "+object +" "+".";
		}else{
			return subject+" "+predicate+" "+object +" "+subGraph+" "+".";
		}
	}
	//return the whole line including the ending " ."
	public String getLine(){
		return this.getNQuad();
	}
	@Override
	public String toString(){
		return this.getLine();
	}
}
