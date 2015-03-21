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
	public void testMap() throws SQLException{
		Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.200.140/rockson" , "rockson" , "hello");
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select b ,utilDate , s as sv ,i ,f, d,str,date,time,datetime from bean");
		List<Bean> beans= ResultSetMapProxy.instance.map(resultSet, Bean.class);
		System.out.println(beans);
		statement.close();
		connection.close();
	}
	@Test
	public void testMapOne() throws SQLException{
		Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.200.140/rockson" , "rockson" , "hello");
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select b ,utilDate , s as sv ,i ,f, d,str,date,time,datetime from bean");
		Bean bean= ResultSetMapProxy.instance.mapOne(resultSet, Bean.class);
		System.out.println(bean);
		statement.close();
		connection.close();
	}

}
