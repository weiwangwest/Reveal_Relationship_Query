package input;

import java.util.ArrayList;

public class TripleParser {
	String tripleLine;
	String subject;
	String object;
	String predicate;
	String subGraph;
	String dot;
	public TripleParser(String line) throws Exception{
		this.tripleLine=line;
		String[] parts=this.tripleLine.split(" ");
		if (parts.length<5){
			throw new Exception("wrong triple format:"+parts+" from line:\n"+line);			
		}
		dot=parts[parts.length-1];		// the last part is always "."
		if (!dot.equals(".")){
			System.out.println("TripleParser error: dot not equals .: "+line);
			System.exit(-1);
		}
		subGraph=parts[parts.length-2];	// the second last part is always subGraph
		int positionOfPredicate=-1;		// look for the predicate, because it's easy to find
		for (int i=1; i<=parts.length-4; i++){
			if (parts[i].startsWith("<")&&parts[i].endsWith(">")){ //TODO: so simple, sure?
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
		for (int i=positionOfPredicate+2; i<=parts.length-3; i++){
			object += " "+parts[i];
		}
	}
/*
	public TripleParser(String line) throws Exception{
		this.tripleLine=line;
		if (!isdoneBySpecialCases(line)){
			String[] parts=this.tripleLine.split(" ");
			if (parts.length<5){
				throw new Exception("wrong triple format:"+parts+" from line:\n"+line);			
			}
			if (parts.length>5){
				parts=mergeParts(parts);
			}
			if (parts.length!=5){
				throw new Exception("wrong triple format:"+parts+" from line:\n"+line);			
			}
			subject=parts[0];
			predicate=parts[1];
			object=parts[2];
			subGraph=parts[3];
			dot=parts[4];
			if (subGraph.startsWith("<") && subGraph.endsWith(">"));
			assertTrue(dot.equals("."));			
		}
	}
*/
	private String [] mergeParts(String [] parts){
		ArrayList<String> al=new ArrayList<String>();
		String builder=null;
		boolean inStr=false;
		for (String str: parts){
			if (!inStr){	//outside a literal
				if (!str.startsWith("\"")){
					al.add(str);
				}else{	//come into a literal
					inStr=true;
					builder=str;
				}
			}else{	//inside a literal
				builder += " "+str;
				if (!str.endsWith("\\\"") && (str.endsWith("\"")||str.contains("\"@")&&!str.contains("\\\"@n\\u00A7\\u20ACL"))
						||str.contains("\"^^<")
						||str.endsWith("\"@en")
						||str.endsWith("Li\\u20AC$\\\"\"")
						||str.endsWith("\\\\\"")
					){	// dirtily go out of a literal, CAUTION: conditions for billinTriple data set only.
					al.add(builder);
					inStr=false;
				}else{
				}				
			}
		}
		return al.toArray(new String[al.size()]);
	}
	public String getSubject(){
		return subject;
	}
	public String getPredicate(){
		return predicate;
	}
	public String getObject(){
		return object;
	}
	public String getSubGraph(){
		return subGraph;
	}
	public String getDot(){
		return dot;
	}
	public String getLine(){
		return subject+" "+predicate+" "+object+" "+subGraph+" "+dot;
	}
	private boolean isdoneBySpecialCases(String line){	//manually process special cases
		if (line.equals("<http://eprints.ecs.soton.ac.uk/id/eprint/18511> <http://purl.org/dc/terms/title> \"Open Access Mandates and the \\\\\\\"Fair Dealing\\\\\\\" Button\"^^<http://www.w3.org/2001/XMLSchema#string> <http://rdf.ecs.soton.ac.uk/person/60> .")){
			subject="<http://eprints.ecs.soton.ac.uk/id/eprint/18511>";
			predicate="<http://purl.org/dc/terms/title>";
			object="\"Open Access Mandates and the \\\\\\\"Fair Dealing\\\\\\\" Button\"^^<http://www.w3.org/2001/XMLSchema#string>";
			subGraph="<http://rdf.ecs.soton.ac.uk/person/60>";
			dot=".";
			return true;
		}else if (line.equals("")){
			subject="";
			predicate="";
			object="";
			subGraph="";
			dot="";
			return true;
		}else{
			return false;
		}
	}
}
