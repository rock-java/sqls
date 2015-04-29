package com.rockson.jsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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

	public <B> List<B> query(Connection connection, String sql, Object params, Class<B> clazz) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			return map(statement.executeQuery(sql), clazz);
		} finally {
			if (null != statement)
				statement.close();
		}
	}

	public <B> List<B> query(Connection connection, String sql, Class<B> clazz) throws SQLException {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return map(statement.executeQuery(sql), clazz);
		} finally {
			if (null != statement)
				statement.close();
		}
	}

}
