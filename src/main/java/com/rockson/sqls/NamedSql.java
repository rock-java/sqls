package com.rockson.sqls;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedSql {
	
	
	public static final NamedSql instance =new NamedSql();
	
	/**
	 * select uid from User where id={id}, {id:"jim"} --> select uid from User where id='?' ["jim"]
	 * @param sql
	 * @param params
	 */
	public NameSqlPair tranlate(String sql , Map<String,Object> params) {
		return null;
	}
	
	public static Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
	
	public <T> T buildQuery(Connection connection ,String sql , Map<String, ?> params, Map<String, DbType> argsType , ResultSetFunction<T> cb) throws SQLException {
		try(PreparedStatement prepareStatement = build(connection, sql, params, argsType);){
			int limitCount = limitCount(sql);
			if(-1!=limitCount) {
				prepareStatement.setFetchSize(limitCount);
			}
			ResultSet resultSet = prepareStatement.executeQuery();
			T result =  cb.apply(resultSet);
			resultSet.close();
			return result;
		}
	}
	
	public int executeUpdate(Connection connection ,String sql , Map<String, ?> params, Map<String, DbType> argsType) throws SQLException {
		try(PreparedStatement prepareStatement = build(connection, sql, params, argsType);){
			return prepareStatement.executeUpdate(); 
		}
	}
	
	public PreparedResult execute(Connection connection ,String sql , Map<String, ?> params, ExecuteConfig executeConfig , Map<String, DbType> argsType) throws SQLException {
		try(PreparedStatement prepareStatement = build(connection, sql, params, argsType);){
			boolean executeResult = prepareStatement.execute();
			if(executeResult) {
				return new PreparedResult(0 , prepareStatement.getResultSet());
			}else{
				return new PreparedResult(1 , prepareStatement.getUpdateCount());
			}
		}
	}
	protected static final Pattern LIMIT_COUNT_PATTERN = Pattern.compile("^.*limit\\s+\\d+\\s*,\\s*(\\d+)\\s*$|^.*limit\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE); 
	protected int limitCount(String sql) {
		Matcher matcher = LIMIT_COUNT_PATTERN.matcher(sql);
		if(matcher.find()){
			return Integer.valueOf(null!=matcher.group(1)?matcher.group(1):matcher.group(2));
		}
		return -1;
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
			argSetter(prepareStatement, i + 1, args.get(i), null==argsType?null:argsType.get(args.get(i)));
		}
		return prepareStatement;
	}
	
	protected void argSetter(PreparedStatement preparedStatement ,int index, Object arg , DbType type) throws SQLException {
		if(null!= type) {
			switch (type) {
			case NCHAR:
			case NVARCHAR:
				preparedStatement.setNString(index, (String) arg);
				break;

			default:
				break;
			}
		}
		if(null == arg) {
			preparedStatement.setObject(index , null);
		}
		if(arg instanceof Boolean || boolean.class.equals(arg.getClass())) {
			preparedStatement.setBoolean(index, (boolean) arg);
		}
		if(arg instanceof Character || char.class.equals(arg.getClass())) {
			preparedStatement.setString(index, arg.toString());
		}
		if(arg instanceof Byte || byte.class.equals(arg.getClass())) {
			preparedStatement.setByte(index, (byte) arg);
		}
		if(arg instanceof Short || short.class.equals(arg.getClass())) {
			preparedStatement.setShort(index, (short) arg);
		}
		if(arg instanceof Integer || int.class.equals(arg.getClass())) {
			preparedStatement.setInt(index, (int) arg);
		}
		if(arg instanceof Long || long.class.equals(arg.getClass())) {
			preparedStatement.setLong(index, (long) arg);
		}
		if(arg instanceof Float || float.class.equals(arg.getClass())) {
			preparedStatement.setFloat(index, (float) arg);
		}
		if(arg instanceof Double || double.class.equals(arg.getClass())) {
			preparedStatement.setDouble(index, (double) arg);
		}
		if(arg instanceof String) {
			preparedStatement.setString(index, (String) arg);
		}
		if(arg instanceof BigInteger) {
			preparedStatement.setBytes(index, ((BigInteger) arg).toByteArray());
		}
		if(arg instanceof BigDecimal) {
			preparedStatement.setBigDecimal(index, (BigDecimal) arg);
		}
		if(arg instanceof byte[] ||Byte[].class.equals(arg.getClass())) {
			preparedStatement.setBytes(index, (byte[])arg);
		}
		if(arg instanceof Time) {
			preparedStatement.setTime(index, (Time) arg);
		}
		if(arg instanceof Date) {
			preparedStatement.setDate(index, (Date) arg);
		}
		if(arg instanceof Timestamp) {
			preparedStatement.setTimestamp(index, (Timestamp) arg);
		}
		if(arg instanceof Blob) {
			preparedStatement.setBlob(index, (Blob) arg);
		}
		if(arg instanceof NClob) {
			preparedStatement.setNClob(index, (NClob)arg);
		}
		if(arg instanceof Clob) {
			preparedStatement.setClob(index, (Clob)arg);
		}
		
	}
	
}
