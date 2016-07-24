package com.rockson.sqls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	public static Connection get() throws SQLException{
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/test" , "root" , "6plzHiJKdUMlFZ=");
	}
}
