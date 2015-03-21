package com.rockson.jsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.Test;

import com.mysql.jdbc.Driver;
import com.rockson.jsql.test.bean.Bean;

public class ResultSetMapProxyTest {
	@Test
	public void test() throws SQLException{
		Driver d;
		Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.200.139/rockson" , "rockson" , "hello");
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select b ,utilDate , s as sv ,i ,f, d,str,date,time,datetime from bean");
		List<Bean> beans= ResultSetMapProxy.instance.map(resultSet, Bean.class);
		System.out.println(beans);
		
		System.out.println(Object.class.isAssignableFrom(Bean.class));
	}

}
