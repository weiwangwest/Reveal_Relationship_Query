package output.format;

import java.io.PrintStream;

public class WikiTable {
	Object title;
	Object[] heads;
	Object[][] data;
	int numbeOfDataLines;
	int numbeOfDataColumns;
	//append a new line of data
	public void appendLine(Object[] dataLine){
		//create a copy of the data
		Object [][] dataTemp=new Object[this.numbeOfDataLines+1][this.numbeOfDataColumns]; 
		for (int i=0; i<this.numbeOfDataLines; i++){
					dataTemp[i]=data[i];
		}
		for (int j=0; j<this.numbeOfDataColumns;j++){
			dataTemp[this.numbeOfDataLines][j]=dataLine[j];
		}
		this.data=dataTemp;
		this.numbeOfDataLines++;
	}
	//Insert a single column into the data array, column id starts from 1
	public void insertColume(int columnId, Object[] columnData){
		if (data==null){
			//initialize data
		}
		for (int i=0; i<columnData.length; i++){
			data[i][columnId-1]=columnData[i];
		}
	}
	public void insertMatrix(Object[][] dataOriginal, int lines, int columns){		
		Object [][] dataTemp=new Object[lines][columns]; 
		for (int i=0; i<lines; i++)
			for (int j=0; j<columns; j++){
				if (i<dataOriginal.length && j<dataOriginal[i].length){
					dataTemp[i][j]=dataOriginal[i][j];
				}else{
					dataTemp[i][j]=null;
				}
			}
	}
	//Format the data into a table grid
	public void formatData(){
		//calculate the max size of the current data
		int lines, columns=0;
		lines=data.length;
		for (int i=0; i<lines; i++){
			if (data[i].length>columns){
				columns=data[i].length;
			}
		}
		//create a copy of the data
		Object [][] dataTemp=new Object[lines][columns]; 
		for (int i=0; i<lines; i++){
			for (int j=0; j<columns; j++){
				if (i<data.length && j<data[i].length){
					dataTemp[i][j]=data[i][j];
				}else{
					dataTemp[i][j]=null;
				}
			}
		}
		this.data=dataTemp;
		this.numbeOfDataLines=lines;
		this.numbeOfDataColumns=columns;
	}
	public WikiTable(Object title, Object[] heads){ 
		this.title=title;
		this.heads=heads;
		this.data=new Object[0][heads.length];		
		this.numbeOfDataColumns=heads.length;
		this.numbeOfDataLines=0;
	}
	public WikiTable(Object title, Object[] heads, Object[][] data){ 
		this(title, heads);
		this.data=data;		
		this.numbeOfDataLines=data.length;
	}
	public WikiTable(Object title, Object[] heads, Object[][] data, int NumberOfLines, int NumberOfColumns){
		this(title, heads, data);
		//if the given data does not have enough number of lines/columns, then insert them with null Objects.		
	}
	public void print(PrintStream out){
		//new line
		out.print("\n\n");
		//begin
		out.print("{| ");
		//style 
		out.println("border=\"1\" style=\"overflow:auto; border-collapse:collapse\"");
		//title
		if (title!=null){
			out.println("|+"+title);
		}
		//heads
		if (heads!=null){					
			out.println("|-");
			for (Object head: heads){				
				String [] headParts=head.toString().split("\\|");
				if (headParts.length==2){
					out.print("! "+headParts[0]);
					out.println("| "+headParts[1]);
				}else{
					out.println("| "+headParts[0]);					
				}
			}
		}
		//data
		if (data!=null)
		for (Object[] line: data){
			out.println("|-");
			for (Object element: line){
				out.println("|"+(element!=null?element:""));
			}
		}
		//end
		out.println("|}");		
	}
	public static void main(String[] args) {
		//table 1
		WikiTable table1=new WikiTable("Table 1. Virtual machine specs",		//title 
				new String[] {"CPU Processes", "Memory size (GB)", "Disk space (GB)"},	//heads 
				new Object [][] {	//data
			            {"1~4", "8~16","20~30"},
			        }
				);
		table1.print(System.out);

		//table 2
		System.out.println("<div id=\"Table 2. Test overview\"></div>");
		WikiTable table2=new WikiTable("Table 2. Test overview",		//title 
				new String[] {"testResults", "datasets", "entitiesList","triples/NQuads", "distinctRdfsSubclassStmts", "distnctRdfClassTyps"},	//heads 
				new Object [][] {	//data
			            {"(see [[#3|Table 3]])", "{0..2}", null, null, null, null},
			            {"(see [[#4|Table 4]])", "{0..3}", null, null, null, null},
			            {"(see [[#5|Table 5]])", "{0..4}", null, null, null, null},
			            {"(see [[#6|Table 6]])", "{0..5}", null, null, null, null},
			            {"(see [[#7|Table 7]])", "{0..6}", null, null, null, null},
			        }
				);
		table2.print(System.out);

		//table 3, 4, 5, 6, 7
		for (int i=3; i<=7; i++){
			String titleString="Table "+i+". Runtime of queries on datasets {0.."+(i-1)+"}";
			System.out.println("<div id=\""+i+"\"></div>");
			WikiTable table3=new WikiTable(titleString,		//title 
					new String[] {"", "colspan=\"3\" |query type 1", "colspan=\"3\" |query type 2", "colspan=\"3\" |query type 3"},	//heads 
					new Object [][] {	//data
							{"Test No.", "Q1.1", "Q1.2", "Q1.3", "Q2.1", "Q2.2", "Q2.3", "Q3.1", "Q3.2", "Q3.3"},
				            {"1", null, null, null, null, null, null, null, null, null},
				            {"2", null, null, null, null, null, null, null, null, null},
				            {"3", null, null, null, null, null, null, null, null, null},
				            {"4", null, null, null, null, null, null, null, null, null},
				            {"5", null, null, null, null, null, null, null, null, null},
				            {"6", null, null, null, null, null, null, null, null, null},
				            {"7", null, null, null, null, null, null, null, null, null},
				            {"8", null, null, null, null, null, null, null, null, null},
				            {"9", null, null, null, null, null, null, null, null, null},
				            {"10", null, null, null,null , null, null, null, null, null},
				            {"Avg", null, null, null, null, null, null, null, null, null}
				        }
					);
			table3.print(System.out);	
			System.out.println("(back to "+"[[#Table 2. Test overview|Table 2. Test overview]]"+")");
		}
	}
}