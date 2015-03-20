package com.rockson.jsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetMapProxy {
	private Map<String, ResultSetMap<?>> mappers = new HashMap<String, ResultSetMap<?>>(); 
	private ResultSetMapFactory resultSetMapFactory = new  AsmResultSetMapFactory();
	
	public static final ResultSetMapProxy instance = new ResultSetMapProxy();
	
	public <B> List<B> map(ResultSet resultSet , Class<B> clazz) throws SQLException {
		String className = clazz.getName();
		ResultSetMap<B> map = (ResultSetMap<B>) mappers.get(className);
		if(null == map) {
			map = resultSetMapFactory.build(clazz);
			mappers.put(className, map);
		}
		return map.map(resultSet, clazz);
	}
//	<B> B mapOne(ResultSet resultSet , Class<B> clazz) throws Exception{
//		String className = clazz.getName();
//		ResultSetMap<B> map = (ResultSetMap<B>) mappers.get(className);
//		if(null == map) {
//			map = resultSetMapFactory.build(clazz);
//			mappers.put(className, map);
//		}
//		return map.mapOne(resultSet, clazz);
//	}
	
}
