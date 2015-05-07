package com.rockson.jsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	public static Connection get() throws SQLException{
		return DriverManager.getConnection("jdbc:mysql://211.151.127.118:43120/iqidao" , "root" , "6plzHiJKdUMlFZ=");
	}
}
