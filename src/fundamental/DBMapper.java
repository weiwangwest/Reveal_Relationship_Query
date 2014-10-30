package fundamental;

import java.sql.*;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class DBMapper{
	   private static Logger log = Logger.getLogger(Log4JPropertiesTest.class);
	   private final String databaseTableName;		//which database table this mapper uses.
	   private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   private static final String DB_URL = "jdbc:mysql://localhost:3306/revealdb?connectTimeout=0&socketTimeout=0&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
	   private static final String USER = "reveal";
	   private static final String PASS = "pass4reveal";
	   private Connection conn=null;
	public Connection getConn(){
		Connection con=null;
		try{
			   	  Class.forName(JDBC_DRIVER);
				  con=DriverManager.getConnection(DB_URL, USER, PASS);			   
		   }catch(Exception e){
			    log.error( "DBMapper getConn() failed!", e );			   
		   }
			return con;
	   }
		public DBMapper(String dbTableName){
			this.databaseTableName=dbTableName;
			 conn = getConn();
		}
		@Override
		public void finalize(){
			try{
				conn.close();
			}catch(Exception e){
			    log.error( "DBMapper finalize() failed!", e );   
			}
		}
 	public boolean containsKey(String key) {
		int result=0; 
		try{
			ResultSet rs=conn.createStatement().executeQuery("SELECT COUNT(*) FROM "+databaseTableName +" WHERE name='"+key+"'");
			while(rs.next()) {
				result = rs.getInt(1);
			}
		}catch(Exception e){
		    log.error( "DBMapper containsKey() failed!", e );   
		}
		switch(result){
		case 0: 
			return false;
		case 1: 
			return true;
		default: 
			throw new Error("multiple keys ("+ key +") in " + databaseTableName);
		}
	}
	public String getKey(int value){
		String name=null; 
		try{
			ResultSet rs=conn.createStatement().executeQuery("SELECT name FROM "+databaseTableName +" WHERE id="+value);
			while(rs.next()) {
				name = rs.getString(1);
			}
		}catch(Exception e){
		    log.error( "DBMapper () failed!", e );   
		}
		return name;
	}
	public int getValue(String key) {
		int id=-1; 
		try{
			ResultSet rs=conn.createStatement().executeQuery("SELECT id FROM "+databaseTableName +" WHERE name='"+key+"'");
			if (rs.next()) {
				id=rs.getInt(1);
			}
		}catch(Exception e){
		    log.error( "DBMapper getValue() failed!", e );
		}
		return id;
	}
	public int put(String key){		
		try{
		    int result=this.getValue(key);
			if (result==-1){
				String commdStr="INSERT "+databaseTableName+" (name) VALUES ('" + key + "')";
				conn.createStatement().executeUpdate(commdStr);
				result=this.getValue(key);
			}
			return result;
		}catch(Exception e){
		    log.error( "DBMapper put () failed!", e );
		    return -1;
		}
	}
	public int size() {
		try{
		    ResultSet rs=conn.createStatement().executeQuery("SELECT COUNT(*) FROM "+databaseTableName);
			int result=-1; 
			while(rs.next()) {
				result = rs.getInt(1);
			}
			return result;
		}catch(Exception e){
		    log.error( "DBMapper size() failed!", e );
		    return -1;
		}
	}	
	public int clear() {
		try{
			int result=conn.createStatement().executeUpdate("truncate table  "+databaseTableName);
			conn.createStatement().executeUpdate("alter table  "+databaseTableName+" auto_increment=1");
			return result;
		}catch(Exception e){
		    log.error( "DBMapper clear() failed!", e );
		    return -1;
		}
	}
}