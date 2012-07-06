package data;

import java.sql.*;

public class DataConnection {
	
	public static void connectionOffice(){
		
		Connection conn=null;
		String url="ids-svr-1.chemeng.ucl.ac.uk:3306";
		String dbName="wenhao";
		String driver="com.mysql.jdbc.Driver";
		String userName="Wenhao";
		String pwd="slanlxd";
		
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url,userName,pwd);
			System.out.println("Connected to the database");
			conn.close();
			System.out.println("Disconnected from database");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
