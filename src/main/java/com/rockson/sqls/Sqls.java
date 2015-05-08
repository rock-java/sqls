package com.rockson.sqls;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class Sqls {
	public static <T> List<T> query(Connection con , String sql, Map<String, ?> params ,Class<T> t) throws SQLException {
		return query(con, sql, params, t, null);
	}
	public static <T> List<T> query(Connection con , String sql, Map<String, ?> params ,Class<T> t, Map<String, DbType> argsType) throws SQLException {
//		ResultSet resultSet = NamedSql.instance.buildQuery(con, sql, params, argsType);
//		List<T> beans= ResultSetMapProxy.instance.map(resultSet, t);
//		return beans;
		return NamedSql.instance.buildQuery(con, sql, params, argsType, (resultSet)->{
			return  ResultSetMapProxy.instance.map(resultSet, t);
		});
	}
	public static <T> T queryOne(Connection con , String sql, Map<String, ?> params ,Class<T> t
			) throws SQLException {
		return queryOne(con, sql, params, t, null);
	}
	public static <T> T queryOne(Connection con , String sql, Map<String, ?> params ,Class<T> t, Map<String, DbType> argsType) throws SQLException {
//		ResultSet resultSet = NamedSql.instance.buildQuery(con, sql, params, argsType);
//		T bean = ResultSetMapProxy.instance.mapOne(resultSet, t);
//		return bean;
		return NamedSql.instance.buildQuery(con, sql, params, argsType, (resultSet)->{
			return  ResultSetMapProxy.instance.mapOne(resultSet, t);
		});
	}
	public static List<Map<String,Object>> query(Connection con , String sql, Map<String, Object> params) throws SQLException {
//		return ResultSetMapProxy.instance.map(NamedSql.instance.buildQuery(con, sql, params ,null) );
		return NamedSql.instance.buildQuery(con, sql, params, null, (resultSet)->{
			return ResultSetMapProxy.instance.map(resultSet);
		});
	}
	public static Map<String,Object> queryOne(Connection con , String sql, Map<String, Object> params) throws SQLException {
		//return ResultSetMapProxy.instance.mapOne(NamedSql.instance.buildQuery(con, sql, params ,null) );
		return NamedSql.instance.buildQuery(con, sql, params, null, (resultSet)->{
			return ResultSetMapProxy.instance.mapOne(resultSet);
		});
	}
	public static int execute(Connection con , String sql, Map<String, Object> params, Map<String, DbType> argsType) throws SQLException {
		return NamedSql.instance.executeUpdate(con, sql, params, argsType);
	}
	public static int execute(Connection con , String sql, Map<String, Object> params) throws SQLException {
		return NamedSql.instance.executeUpdate(con, sql, params, null);
	}
	
	public static int post(Connection con,String table , Object object) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.postSql(table, object);
		return NamedSql.instance.executeUpdate(con, executeObject.sql, executeObject.params, null);
	}
	public static Map<String, Object> get(Connection con,String table,Object conditions , String... keys) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.getSql(table, conditions, keys);
		return queryOne(con, executeObject.sql, executeObject.params);
	}
	public static <T> T get(Connection con,String table,Object conditions ,Class<T> clazz, String... keys) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.getSql(table, conditions, keys);
		return queryOne(con, executeObject.sql, executeObject.params, clazz);
	}
	public static List<Map<String, Object>> gets(Connection con,String table,Object conditions , String... keys) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.getSql(table, conditions, keys);
		return query(con, executeObject.sql, executeObject.params);
	}
	public static <T> List<T> gets(Connection con,String table,Object conditions ,Class<T> clazz, String... keys) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.getSql(table, conditions, keys);
		return query(con, executeObject.sql, executeObject.params, clazz);
	}
	public static int put(Connection con,String table,Object newValues , Object conditions) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.putSql(table, newValues, conditions);
		return NamedSql.instance.executeUpdate(con, executeObject.sql, executeObject.params, null);
	}
	public static int delete(Connection con,String table , Object conditions) throws SQLException{
		ExecuteObject executeObject = RestSqlFactory.instance.deleteSql(table, conditions);
		return NamedSql.instance.executeUpdate(con, executeObject.sql, executeObject.params, null);
	}
	
}
