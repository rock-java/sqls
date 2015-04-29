package com.rockson.jsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedSql {
	
	/**
	 * select uid from User where id={id}, {id:"jim"} --> select uid from User where id='?' ["jim"]
	 * @param sql
	 * @param params
	 */
	public NameSqlPair tranlate(String sql , Map<String,Object> params) {
		return null;
	}
	
	public static Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
	
	public ResultSet buildQuery(Connection connection ,String sql , Map<String, ?> params, Map<String, DbType> argsType) throws SQLException {
		
		PreparedStatement prepareStatement = build(connection, sql, params, argsType);
		return  prepareStatement.executeQuery(); 
	}
	
	public int executeUpdate(Connection connection ,String sql , Map<String, ?> params, Map<String, DbType> argsType) throws SQLException {
		PreparedStatement prepareStatement = build(connection, sql, params, argsType);
		return prepareStatement.executeUpdate(); 
	}

	private PreparedStatement build(Connection connection, String sql, Map<String, ?> params,
			Map<String, DbType> argsType) throws SQLException {
		Matcher matcher = pattern.matcher(sql);
		
		List<Object> args = new ArrayList<Object>();
		StringBuffer sqlBuffer = new StringBuffer(sql.length());
		String key ;
		while(matcher.find()) {
			key = matcher.group(1);
			if(params.containsKey(key)){
				matcher.appendReplacement(sqlBuffer, "?");
				args.add(params.get(key));
			}else{
				matcher.appendReplacement(sqlBuffer, matcher.group());
			}
		}
		matcher.appendTail(sqlBuffer);
		PreparedStatement prepareStatement =  connection.prepareStatement(sqlBuffer.toString());
		
		for (int i =0 ; i<args.size() ;i++) {
			argSetter(prepareStatement ,i+1,args.get(i) , argsType.get(args.get(i)));
		}
		return prepareStatement;
	}
	
	protected void argSetter(PreparedStatement preparedStatement ,int index, Object arg , DbType type) throws SQLException {
		if(null!= type) {
			switch (type) {
			case VARCHAR:
				preparedStatement.setString(index, (String) arg);
				break;

			default:
				break;
			}
		}
		if(null == arg) {
			preparedStatement.setObject(index , null);
		}
		if(arg instanceof String) {
			preparedStatement.setString(index, (String) arg);
		}
		
	}
	
}
