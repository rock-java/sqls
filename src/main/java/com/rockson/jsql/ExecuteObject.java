package com.rockson.jsql;

import java.util.Map;

public class ExecuteObject {
	public String sql;
	public Map<String , Object> params;
	public ExecuteObject(String sql, Map<String, Object> params) {
		this.sql = sql;
		this.params = params;
	}
	public ExecuteObject() {}
}
