package com.rockson.sqls;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResultSetMapProxy {
	private Map<String, ResultSetMap<?>> mappers = new HashMap<String, ResultSetMap<?>>();
	private ResultSetMapFactory resultSetMapFactory = new AsmResultSetMapFactory();

	public static final ResultSetMapProxy instance = new ResultSetMapProxy();

	public <B> List<B> map(ResultSet resultSet, Class<B> clazz) throws SQLException {
		String className = clazz.getName();
		ResultSetMap<B> mapper = (ResultSetMap<B>) mappers.get(className);
		if (null == mapper) {
			mapper = resultSetMapFactory.build(clazz);
			mappers.put(className, mapper);
		}
		return mapper.map(resultSet, clazz);
	}

	<B> B mapOne(ResultSet resultSet, Class<B> clazz) throws SQLException {
		String className = clazz.getName();
		ResultSetMap<B> mapper = (ResultSetMap<B>) mappers.get(className);
		if (null == mapper) {
			mapper = resultSetMapFactory.build(clazz);
			mappers.put(className, mapper);
		}
		return mapper.mapOne(resultSet, clazz);
	}
	
	public List<Map<String, Object>> map(ResultSet resultSet) throws SQLException {
		ResultSetMetaData meta = resultSet.getMetaData();
		int columnCount = meta.getColumnCount();
		List<Map<String, Object>> result =new LinkedList<Map<String,Object>>();
		Map<String, Object> record ;
		while(resultSet.next()) {
			record = new HashMap<String, Object>();
			for(int i = 1 ;i <= columnCount;i++){
				record.put(meta.getColumnName(i), resultSet.getObject(i));
			}
			result.add(record);
		}
//		resultSet.close();
		return result;
	}
	
	public Map<String, Object> mapOne(ResultSet resultSet) throws SQLException {
		ResultSetMetaData meta = resultSet.getMetaData();
		int columnCount = meta.getColumnCount();
		Map<String, Object> record = null ;
		if(resultSet.next()) {
			record = new HashMap<String, Object>();
			for(int i = 1 ;i <= columnCount;i++){
				record.put(meta.getColumnName(i), resultSet.getObject(i));
			}
		}
		return record;
	}
	
	

//	public <B> List<B> query(Connection connection, String sql, Object params, Class<B> clazz) throws SQLException {
//		PreparedStatement statement = null;
//		try {
//			statement = connection.prepareStatement(sql);
//			return map(statement.executeQuery(sql), clazz);
//		} finally {
//			if (null != statement)
//				statement.close();
//		}
//	}

//	public <B> List<B> query(Connection connection, String sql, Class<B> clazz) throws SQLException {
//		Statement statement = null;
//		try {
//			statement = connection.createStatement();
//			return map(statement.executeQuery(sql), clazz);
//		} finally {
//			if (null != statement)
//				statement.close();
//		}
//	}

}
