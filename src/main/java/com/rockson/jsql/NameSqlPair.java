package com.rockson.jsql;

public class NameSqlPair {
	public String sql;
	public Object[] params;
	public NameSqlPair(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}
	public NameSqlPair() {
	}
	
}
