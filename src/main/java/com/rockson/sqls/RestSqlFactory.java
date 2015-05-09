package com.rockson.sqls;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class RestSqlFactory {
	public static RestSqlFactory instance = new RestSqlFactory();
	
	
	public ExecuteObject postSql(String table , Object object){
		Map<String,Object> params =	params = beanToMap(object);
		if(null == object || null == params) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		StringBuilder values = new StringBuilder();
		sql.append("insert ");
		sql.append(table);
		sql.append(' ');
		sql.append('(');
		for(String name:params.keySet()){
			sql.append('`');
			sql.append(name);
			sql.append('`');
			sql.append(',');
			values.append('{');
			values.append(name);
			values.append('}');
			values.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		values.deleteCharAt(values.length()-1);
		sql.append(") values(");
		sql.append(values);
		sql.append(')');
		return new ExecuteObject(sql.toString(), params);
	}
	/**
	 * update users set name={name},age={age} where id={id}
	 * @param table
	 * @param newValues
	 * @param conditions
	 * @return
	 */
	public ExecuteObject putSql(String table , Object newValues , Object conditions){
		Map<String,Object> params = beanToMap(newValues);
		Map<String,Object> conditionParams = beanToMap(conditions);
		StringBuilder sql = new StringBuilder();
		sql.append("update `");
		sql.append(table);
		sql.append("` set ");
		sql.append(makeKeyValues(params));
		sql.append(" where ");
		if(conditions instanceof String) {
			sql.append(conditions);
		}else{
			sql.append(makeConditionSql(conditionParams));
		}
		Map<String,Object> allParams = new HashMap<String, Object>(params.size()+conditionParams.size());
		allParams.putAll(params);
		if(null!=conditionParams)allParams.putAll(conditionParams);
		return new ExecuteObject(sql.toString(), allParams);
	}
	/**
	 * delete from user where id={id}
	 * @param table
	 * @param object
	 * @param conditions
	 * @return
	 */
	public ExecuteObject deleteSql(String table, Object conditions){
		Map<String,Object> params = beanToMap(conditions);
		StringBuilder sql = new StringBuilder();
		sql.append("delete from `");
		sql.append(table);
		sql.append("` where ");
		if(conditions instanceof String) {
			sql.append(conditions);
		}else{
			sql.append(makeConditionSql(params));
		}
		return new ExecuteObject(sql.toString(), params);
	}
	/**
	 * select `id`,`name` from uesrs where id={id}
	 * @param table
	 * @param conditions
	 * @param keys
	 * @return
	 */
	public ExecuteObject getSql(String table, Object conditions,String... keys){
		Map<String,Object> params = beanToMap(conditions);
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		if(null == keys || 0>=keys.length){
			sql.append('*');
		}else{
			sql.append(makeKeys(keys));
		}
		sql.append(" from ");
		sql.append(table);
		sql.append(" where ");
		if(conditions instanceof String) {
			sql.append(conditions);
		}else{
			sql.append(makeConditionSql(params));
		}
		return new ExecuteObject(sql.toString(), params);
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected StringBuilder makeKeys(String... keys) {
		StringBuilder sql = new StringBuilder();
		for (String key : keys) {
			sql.append('`');
			sql.append(key);
			sql.append('`');
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		return sql;
	}
	/**
	 * map -> k1={k1},k2={k2}
	 * @param object
	 * @return
	 */
	protected StringBuilder makeKeyValues(Object object) {
		StringBuilder sql = new StringBuilder();
		Map<String, ?> params = beanToMap(object);
		for(String name:params.keySet()){
			sql.append('`');
			sql.append(name);
			sql.append('`');
			sql.append('=');
			sql.append('{');
			sql.append(name);
			sql.append('}');
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		return sql;
	}
	/**
	 * map -> key1={key1} and key2={key2}
	 * @param conditions
	 * @return
	 */
	protected StringBuilder makeConditionSql(Object conditions) {
		StringBuilder sql = new StringBuilder();
		Map<String, ?> conditionMap = beanToMap(conditions);
		for(String name:conditionMap.keySet()){
			sql.append('`');
			sql.append(name);
			sql.append('`');
			sql.append('=');
			sql.append('{');
			sql.append(name);
			sql.append('}');
			sql.append(" and ");
		}
		sql.delete(sql.length()-5, sql.length());
		return sql;
	}
	
	public static Map<String, Object> beanToMap(Object bean) {
		if(bean instanceof String) {
			return null;
		}
		if(bean instanceof Map) {
			return (Map<String, Object>)bean;
		}
		PropertyDescriptor[] pds;
		try {
			pds = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
			Map<String, Object> map = new HashMap<String, Object>(pds.length);
			for (PropertyDescriptor pd : pds) {
				if("class".equals(pd.getName())){
					continue;
				}
				map.put(pd.getName(), pd.getReadMethod().invoke(bean));
			}
			return map;
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}
}
