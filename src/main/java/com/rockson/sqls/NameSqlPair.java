package com.rockson.sqls;

import java.util.List;

public class NameSqlPair {
	public String sql;
	public List<Object> params;
	public NameSqlPair(String sql, List<Object> params) {
		this.sql = sql;
		this.params = params;
	}
	public NameSqlPair() {
	}
	
}
